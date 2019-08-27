package com.konrader.unifi.api;

import java.io.IOException;

public class TestUniFiManagementPortal {

	public static void main(String[] args) throws IOException {
		UniFiManagementPortal ump = new UniFiManagementPortal(args[0], args[1], args[2]);
		UMPInfo info = ump.getInfo();
		for (UMPInfo.Service serv : info.data.services) {
			System.out.println(" " + serv.name + " " + info.data.hostname + ":" + serv.port + " " + serv.state);
		}
		UMPSystem sys = ump.getSystem();
		System.out.println("pm8953_tz: " + sys.getTemperature("pm8953_tz").current);
	}

}
