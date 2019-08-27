package com.konrader.unifi.api;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONObject;

public class UniFiController {
	private final URI baseUri;
	private final JsonClient client;

	public UniFiController(String host) {
		baseUri = URI.create("https://" + host + ":8443");
		client = new JsonClient(true);
	}

	public boolean login(String username, String password) throws IOException {
		JSONObject jbody = new JSONObject();
		jbody.put("username", username);
		jbody.put("password", password);
		JSONObject jobj = client.post(baseUri.resolve("/api/login"), jbody);
		return jobj.has("meta") && jobj.getJSONObject("meta").getString("rc").equalsIgnoreCase("ok");
	}

	public Collection<String> sites() throws IOException {
		JSONObject jobj = client.get(baseUri.resolve("/api/self/sites"));
		// System.out.println("" + jobj.toString(2));
		Collection<String> ret = new ArrayList<>();
		JSONArray arr = jobj.getJSONArray("data");
		for (int i = 0; i < arr.length(); i++)
			ret.add(arr.getJSONObject(i).getString("name"));
		return ret;
	}

	public Collection<UnifiDevice> statDevice() throws IOException {
		JSONObject jobj = client.get(baseUri.resolve("/api/s/default/stat/device"));
		// System.out.println("" + jobj.toString(2));
		JSONArray jdata = jobj.getJSONArray("data");
		Collection<UnifiDevice> ret = new ArrayList<>();
		for (int i = 0; i < jdata.length(); i++) {
			JSONObject jdev = jdata.getJSONObject(i);
			if (jdev.getString("type").equals("ugw"))
				ret.add(new UnifiGateway(jdev));
			else if (jdev.getString("type").equals("usw"))
				ret.add(new UnifiSwitch(jdev));
		}
		return ret;
	}

}
