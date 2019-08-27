package com.konrader.unifi.api;

import org.json.JSONObject;

public abstract class UnifiDevice extends MetricsParser {
	final String id;
	final String ip;
	final String mac;

	UnifiDevice(JSONObject jobj) {
		super(jobj.getString("type"), jobj.getString("model"), jobj.getString("name"));
		id = jobj.getString("_id");
		ip = jobj.getString("ip");
		mac = jobj.getString("mac");

		parseSystemStats(jobj);
	}

	void parseSystemStats(JSONObject jobj) {
		jobj = jobj.getJSONObject("system-stats");
		if (jobj == null)
			return;
		setGauge("system_stats_cpu", jobj.getDouble("cpu"));
		setGauge("system_stats_mem", jobj.getDouble("mem"));
	}

	void parseTrafficStat(String gaugeNamePrefix, JSONObject jobj, String jsonFieldPrefix) {
		setGauge(gaugeNamePrefix + "rx_bytes", jobj.getLong(jsonFieldPrefix + "rx_bytes"));
		setGauge(gaugeNamePrefix + "rx_packets", jobj.getLong(jsonFieldPrefix + "rx_packets"));
		setGauge(gaugeNamePrefix + "rx_dropped", jobj.getLong(jsonFieldPrefix + "rx_dropped"));
		setGauge(gaugeNamePrefix + "tx_bytes", jobj.getLong(jsonFieldPrefix + "tx_bytes"));
		setGauge(gaugeNamePrefix + "tx_packets", jobj.getLong(jsonFieldPrefix + "tx_packets"));
	}

	@Override
	public String toString() {
		return type + " " + name;
	}

}
