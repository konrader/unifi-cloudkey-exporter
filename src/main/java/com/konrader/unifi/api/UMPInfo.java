package com.konrader.unifi.api;

import org.json.JSONArray;
import org.json.JSONObject;

public class UMPInfo {
	public static class Meta {
		public String rc;

		Meta(JSONObject jobj) {
			rc = jobj.getString("rc");
		}
	}

	public Meta meta;

	/**
	 * "name": "UniFi-Protect", "pkgname": "unifi-protect", "installed": true,
	 * "version": "1.9.2", "state": "active", "timestamp":
	 * "2019-04-10T00:47:41+02:00", "port": 7443, "configured": true, "inuse": false
	 *
	 */
	public static class Service {
		public String name;
		public String pkgname;
		public boolean installed;
		public String version;
		public String state;
		public String timestamp;
		int port;
		boolean configured;
		boolean inuse;

		Service(JSONObject jobj) {
			name = jobj.getString("name");
			pkgname = jobj.getString("pkgname");
			installed = jobj.getBoolean("installed");
			version = jobj.getString("version");
			state = jobj.getString("state");
			timestamp = jobj.getString("timestamp");
			port = jobj.getInt("port");
			configured = jobj.getBoolean("configured");
			inuse = jobj.getBoolean("inuse");
		}
	}

	public static class Data {
		public String hostname;
		public boolean internet;
		public String version;
		public String token;
		Service[] services;

		Data(JSONObject jobj) {
			hostname = jobj.getString("hostname");
			internet = jobj.getBoolean("internet");
			version = jobj.getString("version");
			token = jobj.getString("token");
			JSONArray arr = jobj.getJSONArray("services");
			services = new Service[arr.length()];
			for (int i = 0; i < arr.length(); i++) {
				services[i] = new Service(arr.getJSONObject(i));
			}
		}
	}

	public Data data;

	public UMPInfo(JSONObject jobj) {
		meta = new Meta(jobj.getJSONObject("meta"));
		data = new Data(jobj.getJSONObject("data"));
	}

}
