package com.konrader.unifi.exporter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;

import com.konrader.unifi.api.UniFiController;
import com.konrader.unifi.api.UniFiManagementPortal;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.common.TextFormat;

public class UnifiCloudKeyExporterMain {
	static final int PORT = 9680;
	static final Logger log = Logger.getLogger(UnifiCloudKeyExporterMain.class.getName());
	private HttpServer server;
	private final UniFiManagementPortal ump;
	UniFiController controller;

	UnifiCloudKeyExporterMain(UniFiManagementPortal ump, UniFiController controller) throws IOException {
		this.ump = ump;
		this.controller = controller;

		server = HttpServer.create(new InetSocketAddress((InetAddress) null, PORT), 3);
		server.createContext("/metrics", new MetricsHandler());
		server.start();
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			log.info("Shutting down");
			server.stop(1);
		}, "ShutdownHook"));
		log.info("Exporter listening on port: " + server.getAddress());
	}

	private static class LocalByteArray extends ThreadLocal<ByteArrayOutputStream> {
		@Override
		protected ByteArrayOutputStream initialValue() {
			return new ByteArrayOutputStream(1 << 20);
		}
	}

	class MetricsHandler implements HttpHandler {
		private final CollectorRegistry registry = CollectorRegistry.defaultRegistry;
		private final LocalByteArray response = new LocalByteArray();

		@Override
		public void handle(HttpExchange t) throws IOException {
			fetchData();
			String query = t.getRequestURI().getRawQuery();

			ByteArrayOutputStream response = this.response.get();
			response.reset();
			OutputStreamWriter osw = new OutputStreamWriter(response);
			TextFormat.write004(osw, registry.filteredMetricFamilySamples(parseQuery(query)));
			osw.flush();
			osw.close();
			response.flush();
			response.close();

			t.getResponseHeaders().set("Content-Type", TextFormat.CONTENT_TYPE_004);
			if (shouldUseCompression(t)) {
				t.getResponseHeaders().set("Content-Encoding", "gzip");
				t.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
				final GZIPOutputStream os = new GZIPOutputStream(t.getResponseBody());
				response.writeTo(os);
				os.close();
			} else {
				t.getResponseHeaders().set("Content-Length", String.valueOf(response.size()));
				t.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.size());
				response.writeTo(t.getResponseBody());
			}
			t.close();
		}
	}

	protected static boolean shouldUseCompression(HttpExchange exchange) {
		List<String> encodingHeaders = exchange.getRequestHeaders().get("Accept-Encoding");
		if (encodingHeaders == null)
			return false;

		for (String encodingHeader : encodingHeaders) {
			String[] encodings = encodingHeader.split(",");
			for (String encoding : encodings) {
				if (encoding.trim().toLowerCase().equals("gzip")) {
					return true;
				}
			}
		}
		return false;
	}

	protected static Set<String> parseQuery(String query) throws IOException {
		Set<String> names = new HashSet<>();
		if (query != null) {
			String[] pairs = query.split("&");
			for (String pair : pairs) {
				int idx = pair.indexOf("=");
				if (idx != -1 && URLDecoder.decode(pair.substring(0, idx), "UTF-8").equals("name[]")) {
					names.add(URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
				}
			}
		}
		return names;
	}

	private void fetchData() throws IOException {
		long t1 = System.currentTimeMillis();

		try {
			ump.getCloudKey();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			controller.statDevice();
		} catch (Exception e) {
			e.printStackTrace();
		}

		long t2 = System.currentTimeMillis();
		log.fine("Fetched metrics in " + (t2 - t1) + "ms");
	}

	Map<String, Gauge> gauges = new HashMap<>();

	public static void main(String[] args) throws Exception {
		if (args.length < 4) {
			System.err.println(
					"Requires params: cloudkeyIP portal_username portal_password controller_username controller_password");
			System.exit(1);
			return;
		}
		UniFiManagementPortal ump = new UniFiManagementPortal(args[0], args[1], args[2]);
		UniFiController ctrl = new UniFiController(args[0]);
		ctrl.login(args[3], args[4]);
		new UnifiCloudKeyExporterMain(ump, ctrl);
	}

}
