package com.konrader.unifi.api;

import java.io.IOException;
import java.net.URI;

import org.json.JSONObject;

public class UniFiManagementPortal {
	private final URI baseUri;
	private final String username;
	private final String password;
	private final JsonClient client;
	private String token = null;

	public UniFiManagementPortal(String host, String username, String password) {
		baseUri = URI.create("https://" + host);
		this.username = username;
		this.password = password;
		client = new JsonClient(true);
	}

	public UMPInfo getInfo() throws IOException {
		JSONObject jobj = client.get(baseUri.resolve("/api/ump/info"));
		// System.out.println("" + jobj.toString(2));
		return new UMPInfo(jobj);
	}

	public boolean login() throws IOException {
		JSONObject jbody = new JSONObject();
		jbody.put("username", username);
		jbody.put("password", password);
		JSONObject jobj = client.post(baseUri.resolve("/api/ump/user/login"), jbody);
		if (jobj.has("meta") && jobj.getJSONObject("meta").getString("rc").equalsIgnoreCase("ok")) {
			token = jobj.getJSONObject("data").getString("token");
			client.setHeader("x-auth-token", token);
			return true;
		} else
			return false;
	}

	public UMPSystem getSystem() throws IOException {
		ensureLoggedIn();
		JSONObject jobj = client.get(baseUri.resolve("/api/ump/system"));
		return new UMPSystem(jobj);
	}

	public UnifiCloudKey getCloudKey() throws IOException {
		ensureLoggedIn();
		JSONObject jdev = client.get(baseUri.resolve("/api/ump/device")).getJSONObject("data");
		JSONObject jsys = client.get(baseUri.resolve("/api/ump/system")).getJSONObject("data");
		return new UnifiCloudKey(jdev, jsys);
	}

	private void ensureLoggedIn() throws IOException {
		if (token != null)
			return;
		login();
	}
}
