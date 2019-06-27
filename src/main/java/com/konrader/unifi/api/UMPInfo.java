package com.konrader.unifi.api;

public class UMPInfo {
	public static class Meta {
		public String rc;
	}
	public Meta meta;
	
	/**
	 * "name": "UniFi-Protect",
	 * "pkgname": "unifi-protect",
	 * "installed": true,
	 * "version": "1.9.2",
	 * "state": "active",
	 * "timestamp": "2019-04-10T00:47:41+02:00",
	 * "port": 7443,
	 * "configured": true,
	 * "inuse": false
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
	}
	
	public static class Data {
		public String hostname;
		public boolean internet;
		public String version;
		public String token;
		Service[] services;
	}
	public Data data;
	
}
