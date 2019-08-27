package com.konrader.unifi.api;

import java.util.Objects;

import org.json.JSONArray;
import org.json.JSONObject;

public class UMPSystem {
	public String hostname;
	public double cpuloadpercent;
	public Memory memory;
	public Temperature[] temperature;

	public static class Memory {
		public long total;
		public long free;
		public long available;
		public long buffers;
		public long cached;
		public long used;

		Memory(JSONObject jobj) {
			total = jobj.getLong("total");
			free = jobj.getLong("free");
			available = jobj.getLong("available");
			buffers = jobj.getLong("buffers");
			cached = jobj.getLong("cached");
			used = jobj.getLong("used");
		}
	}

	public static class Temperature {
		public String name;
		public float current;
		public Integer threshold;

		Temperature(JSONObject jobj) {
			name = jobj.getString("name");
			current = jobj.getFloat("current");
			if (jobj.has("threshold"))
				threshold = jobj.getInt("threshold");
		}
	}

	public UMPSystem(JSONObject jobj) {
		jobj = jobj.getJSONObject("data");
		hostname = jobj.getString("hostname");
		cpuloadpercent = jobj.getDouble("cpuloadpercent");
		memory = new Memory(jobj.getJSONObject("memory"));
		JSONArray arr = jobj.getJSONArray("temperature");
		temperature = new Temperature[arr.length()];
		for (int i = 0; i < arr.length(); i++) {
			temperature[i] = new Temperature(arr.getJSONObject(i));
		}
	}

	Temperature getTemperature(String name) {
		for (Temperature temp : temperature) {
			if (Objects.equals(temp.name, name))
				return temp;
		}
		return null;
	}

}
