package com.konrader.unifi.api;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.CookieManager;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Builder;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.pivovarit.function.ThrowingConsumer;

public class JsonClient {
	private final HttpClient client;
	private ThrowingConsumer<JsonClient, IOException> loginHandler;
	private Map<String, String> headers = new HashMap<>();

	public JsonClient() {
		this(false);
	}

	public JsonClient(boolean trustAnyCert) {
		Builder builder = HttpClient.newBuilder().followRedirects(Redirect.NORMAL);
		if (trustAnyCert) {
			final SSLContext sslContext;
			try {
				sslContext = SSLContext.getInstance("SSL");
				sslContext.init(null, new X509TrustManager[] { new TrustAnything() }, null);
			} catch (GeneralSecurityException e) {
				throw new RuntimeException("Could not create SSLContext", e);
			}
			builder = builder.sslContext(sslContext);
		}
		builder.cookieHandler(new CookieManager());
		client = builder.build();
		headers.put("Accept", "application/json");
	}

	public void setLoginHandler(ThrowingConsumer<JsonClient, IOException> loginHandler) {
		this.loginHandler = loginHandler;
	}

	public void setHeader(String name, String value) {
		headers.put(name, value);
	}

	public JSONObject get(URI uri) throws IOException {
		HttpRequest req = newRequest(uri).GET().build();
		try {
			return client.sendAsync(req, BodyHandlers.ofString()).thenApply(resp -> {
				if (resp.statusCode() == 200)
					return resp.body();

				if (resp.statusCode() == 401 || resp.statusCode() == 412) {
					if (loginHandler == null)
						throw new RuntimeException("No login handler registered");
					try {
						loginHandler.accept(JsonClient.this);
					} catch (IOException e) {
						throw new UncheckedIOException(e);
					}
					try {
						return client.send(newRequest(uri).GET().build(), BodyHandlers.ofString()).body();
					} catch (IOException e) {
						throw new UncheckedIOException(e);
					} catch (InterruptedException e) {
						throw new RuntimeException("Interrupted while resending request after login handler", e);
					}
				}
				throw new RuntimeException("Response status: " + resp.statusCode() + "\n" + resp.body());
			}).thenApply(body -> {
				try {
					return new JSONObject(new JSONTokener(body));
				} catch (JSONException e) {
					throw new RuntimeException("Invalid JSON: " + e.getMessage() + "\n" + body);
				}
			}).get();
		} catch (ExecutionException e) {
			throw new IOException("Failed executing request: GET " + req, e.getCause());
		} catch (InterruptedException e) {
			throw new IOException("Interrupted while executing request: GET " + req, e);
		}
	}

	public JSONObject post(URI uri, JSONObject body) throws IOException {
		HttpRequest req = newRequest(uri).header("Content-Type", "application/json;charset=UTF-8")
				.POST(BodyPublishers.ofString(body.toString())).build();

		try {
			return client.sendAsync(req, BodyHandlers.ofInputStream()).thenApply(HttpResponse::body)
					.thenApply(in -> new JSONObject(new JSONTokener(in))).get();
		} catch (ExecutionException e) {
			throw new IOException("Failed executing request: POST " + req, e.getCause());
		} catch (InterruptedException e) {
			throw new IOException("Interrupted while executing request: POST " + req, e);
		}
	}

	private HttpRequest.Builder newRequest(URI uri) {
		HttpRequest.Builder builder = HttpRequest.newBuilder(uri);
		for (Entry<String, String> header : headers.entrySet()) {
			builder = builder.header(header.getKey(), header.getValue());
		}
		return builder;
	}

	class TrustAnything extends X509ExtendedTrustManager {

		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket)
				throws CertificateException {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket)
				throws CertificateException {
		}

		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine)
				throws CertificateException {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine)
				throws CertificateException {
		}
	}

}
