package com.konrader.unifi.api;

import java.util.HashMap;
import java.util.Map;

import io.prometheus.client.Gauge;

public abstract class MetricsParser {
	static Map<String, Gauge> gauges = new HashMap<>();
	final String type;
	final String model;
	final String name;

	MetricsParser(String type, String model, String name) {
		this.type = type;
		this.model = model;
		this.name = name;
	}

	private String gaugeName(String name) {
		return "unifi_" + type + "_" + name.replace('-', '_').replace('.', '_').replace(" ", "");
	}

	protected void setGauge(String name, double val) {
		Gauge gauge = gauges.computeIfAbsent(gaugeName(name),
				fn -> Gauge.build(fn, "Value of " + name).labelNames("model", "name").register());
		gauge.labels(model, this.name).set(val);
	}

	protected void setGauge(String name, double val, String labelName1, String labelValue1) {
		Gauge gauge = gauges.computeIfAbsent(gaugeName(name),
				fn -> Gauge.build(fn, "Value of " + name).labelNames("model", "name", labelName1).register());
		gauge.labels(model, this.name, labelValue1).set(val);
	}

}
