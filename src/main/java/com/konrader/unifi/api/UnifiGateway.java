package com.konrader.unifi.api;

import org.json.JSONObject;

public class UnifiGateway extends UnifiDevice {

	UnifiGateway(JSONObject jobj) {
		super(jobj);
		JSONObject jtemps = jobj.getJSONObject("system-stats").getJSONObject("temps");
		setGauge("temperature", parseTemp(jtemps.getString("Board (CPU)")), "sensor", "board_cpu");
		setGauge("temperature", parseTemp(jtemps.getString("Board (PHY)")), "sensor", "board_phy");
		setGauge("temperature", parseTemp(jtemps.getString("CPU")), "sensor", "cpu");
		setGauge("temperature", parseTemp(jtemps.getString("PHY")), "sensor", "phy");

		parseTrafficStat("lan_", jobj.getJSONObject("stat"), "lan-");
		parseTrafficStat("wan_", jobj.getJSONObject("stat"), "wan-");
	}

	static int parseTemp(String str) {
		return Integer.parseInt(str.replace(" C", ""));
	}

}
