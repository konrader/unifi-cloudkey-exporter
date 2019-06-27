package com.konrader.unifi.exporter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.logging.Logger;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class UnifiCloudKeyExporterMain {
	static final Charset UTF8 = Charset.forName("UTF-8");
	Logger log = Logger.getLogger(UnifiCloudKeyExporterMain.class.getName());
	
	UnifiCloudKeyExporterMain() throws IOException {
		HttpServer server = HttpServer.create(new InetSocketAddress((InetAddress)null, 8080), -1);
		HttpHandler handler = new HttpHandler() {
			@Override
			public void handle(HttpExchange exchange) throws IOException {
				
				System.out.println("handle "+exchange);
				System.out.println("headers: "+exchange.getRequestHeaders());
				exchange.sendResponseHeaders(200, 0);
				try(BufferedWriter br = new BufferedWriter(new OutputStreamWriter(exchange.getResponseBody(), UTF8))) {
					br.write("hejsan");
				}
			}
		};
		server.createContext("/metrics", handler);
		server.start();
		log.info("HTTP server listening at: "+server.getAddress());
		Runtime.getRuntime().addShutdownHook(new Thread(() -> { log.info("Shutting down"); server.stop(1); }, "ShutdownHook"));
	}
	
	public static void main(String[] args) throws Exception {
		new UnifiCloudKeyExporterMain();
	}
	
}
