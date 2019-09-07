package com.konrader.unifi.api;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UniFiController {
	private final URI baseUri;
	private final JsonClient client;

	public UniFiController(String host, String username, String password) {
		baseUri = URI.create("https://" + host + ":8443");
		client = new JsonClient(true);
		client.setLoginHandler(cli -> {
			JSONObject jbody = new JSONObject();
			jbody.put("username", username);
			jbody.put("password", password);
			JSONObject jobj = cli.post(baseUri.resolve("/api/login"), jbody);
			boolean ok = jobj.has("meta") && jobj.getJSONObject("meta").getString("rc").equalsIgnoreCase("ok");
			if (!ok)
				throw new IOException("Failed to login: " + jobj.toString());
			else
				Logger.getLogger(UniFiController.class.getName()).info("Logged in");
		});
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
		JSONArray jdata = jobj.getJSONArray("data");
		Collection<UnifiDevice> ret = new ArrayList<>();
		for (int i = 0; i < jdata.length(); i++) {
			JSONObject jdev = jdata.getJSONObject(i);
			try {
				if (jdev.getString("type").equals("ugw"))
					ret.add(new UnifiGateway(jdev));
				else if (jdev.getString("type").equals("usw"))
					ret.add(new UnifiSwitch(jdev));
			} catch (JSONException e) {
				e.printStackTrace();
				System.err.println(jdev.toString(2));
			}
		}
		return ret;
	}

}
