package com.konrader.unifi.api;

import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Builder;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpResponse.BodyHandlers;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONObject;
import org.json.JSONTokener;

public class JsonClient {
	private final HttpClient client;

	public JsonClient() {
		this(false);
	}
	
	public JsonClient(boolean trustAnyCert) {
		Builder builder = HttpClient.newBuilder().followRedirects(Redirect.NORMAL);
		if(trustAnyCert) {
			final SSLContext sslContext;
			try {
				sslContext = SSLContext.getInstance("SSL");
				sslContext.init(null, new X509TrustManager[] {new TrustAnything()}, null);
			}
			catch(GeneralSecurityException e) {
				throw new RuntimeException("Could not create SSLContext", e);
			}
			builder = builder.sslContext(sslContext);
		}
		client = builder.build();
	}

	public JSONObject get(URI uri) throws IOException {
		HttpRequest req = HttpRequest.newBuilder(uri)
				.header("Accept", "application/json").GET().build();

		try {
			return client.sendAsync(req, BodyHandlers.ofInputStream())
					.thenApply(HttpResponse::body)
					.thenApply(in -> new JSONObject(new JSONTokener(in))).get();
		} catch (ExecutionException e) {
			if(e.getCause() instanceof IOException)
				throw (IOException)e.getCause();
			else
				throw new IOException("Failed executing request: GET "+req, e);
		} catch (InterruptedException e) {
			throw new IOException("Interrupted while executing request: GET "+req, e);
		}
	}

	class TrustAnything extends X509ExtendedTrustManager {

		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			// TODO Auto-generated method stub

		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			// TODO Auto-generated method stub

		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket)
				throws CertificateException {
			// TODO Auto-generated method stub

		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket)
				throws CertificateException {
			// TODO Auto-generated method stub

		}

		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine)
				throws CertificateException {
			// TODO Auto-generated method stub

		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine)
				throws CertificateException {
			// TODO Auto-generated method stub

		}
	}

}
