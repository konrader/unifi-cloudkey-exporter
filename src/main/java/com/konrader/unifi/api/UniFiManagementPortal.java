package com.konrader.unifi.api;

import java.io.IOException;
import java.net.URI;

import org.json.JSONObject;


public class UniFiManagementPortal {
	private final URI baseUri;
	private final JsonClient client;

	public UniFiManagementPortal(String host) {
		baseUri = URI.create("https://"+host);

		client = new JsonClient(true);
	}

	public UMPInfo getInfo() throws IOException {
		JSONObject jobj = client.get(baseUri.resolve("/api/ump/info"));
		System.out.println(""+jobj.toString(2));
		return null;
	}
}
