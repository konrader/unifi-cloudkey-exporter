package com.konrader.unifi.api;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

import org.json.JSONObject;

public class UniFiManagementPortal {
	private final URI baseUri;
	private final JsonClient client;
	private String token = null;

	public UniFiManagementPortal(String host, String username, String password) {
		baseUri = URI.create("https://" + host);
		client = new JsonClient(true);
		client.setLoginHandler(cli -> {
			JSONObject jbody = new JSONObject();
			jbody.put("username", username);
			jbody.put("password", password);
			JSONObject jobj = cli.post(baseUri.resolve("/api/ump/user/login"), jbody);
			if (jobj.has("meta") && jobj.getJSONObject("meta").getString("rc").equalsIgnoreCase("ok")) {
				token = jobj.getJSONObject("data").getString("token");
				cli.setHeader("x-auth-token", token);
				Logger.getLogger(UniFiManagementPortal.class.getName()).info("Logged in, token: " + token);
			} else {
				throw new IOException("Failed to login: " + jobj.toString());
			}
		});
	}

	public UMPInfo getInfo() throws IOException {
		JSONObject jobj = client.get(baseUri.resolve("/api/ump/info"));
		// System.out.println("" + jobj.toString(2));
		return new UMPInfo(jobj);
	}

	public UMPSystem getSystem() throws IOException {
		JSONObject jobj = client.get(baseUri.resolve("/api/ump/system"));
		return new UMPSystem(jobj);
	}

	public UnifiCloudKey getCloudKey() throws IOException {
		JSONObject jdev = client.get(baseUri.resolve("/api/ump/device")).getJSONObject("data");
		JSONObject jsys = client.get(baseUri.resolve("/api/ump/system")).getJSONObject("data");
		return new UnifiCloudKey(jdev, jsys);
	}

}
