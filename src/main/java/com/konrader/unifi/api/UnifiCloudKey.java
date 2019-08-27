package com.konrader.unifi.api;

import org.json.JSONArray;
import org.json.JSONObject;

public class UnifiCloudKey extends MetricsParser {

	UnifiCloudKey(JSONObject jdev, JSONObject jsys) {
		super(jdev.getString("familyName").toLowerCase().replace(" ", ""), jdev.getString("modelCode"),
				jsys.getString("hostname"));

		setGauge("cpu_load_percent", jsys.getDouble("cpuloadpercent"));
		JSONObject jmem = jsys.getJSONObject("memory");

		setGauge("memory_total_bytes", jmem.getLong("total"));
		setGauge("memory_used_bytes", jmem.getLong("used"));

		JSONArray arr = jsys.getJSONArray("temperature");
		for (int i = 0; i < arr.length(); i++) {
			JSONObject jtemp = arr.getJSONObject(i);
			setGauge("temperature", jtemp.getDouble("current"), "sensor", jtemp.getString("name"));
		}
	}

}
