package com.konrader.unifi.api;

import java.net.URI;

public class UniFiController {
	private final URI baseUri;
	private final JsonClient client;

	public UniFiController(String host) {
		baseUri = URI.create("https://"+host);
		client = new JsonClient();
	}
	
	void login() {
		
	}
	
	
}
