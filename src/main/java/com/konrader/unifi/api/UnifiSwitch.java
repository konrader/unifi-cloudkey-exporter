package com.konrader.unifi.api;

import org.json.JSONArray;
import org.json.JSONObject;

public class UnifiSwitch extends UnifiDevice {

	public int generalTemperature;

	UnifiSwitch(JSONObject jobj) {
		super(jobj);
		setGauge("temperature", jobj.getInt("general_temperature"));
		JSONArray arr = jobj.getJSONArray("port_table");
		for (int i = 0; i < arr.length(); i++) {
			parsePort(arr.getJSONObject(i));
		}
	}

	void parsePort(JSONObject jobj) {
		int portIdx = jobj.getInt("port_idx");
		setGauge("port_speed", jobj.getInt("speed"), "port_idx", "" + portIdx);
		if (jobj.getBoolean("port_poe")) {
			setGauge("port_poe_current", jobj.getDouble("poe_current"), "port_idx", "" + portIdx);
			setGauge("port_poe_voltage", jobj.getDouble("poe_voltage"), "port_idx", "" + portIdx);
			setGauge("port_poe_power", jobj.getDouble("poe_power"), "port_idx", "" + portIdx);
		}
	}

	@Override
	public String toString() {
		return super.toString() + " temp:" + generalTemperature;
	}

}
