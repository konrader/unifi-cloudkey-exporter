package com.konrader.unifi.api;

import java.io.IOException;

public class TestUniFiClient {

	public static void main(String[] args) throws IOException {
		UniFiManagementPortal ump = new UniFiManagementPortal(args[0]);
		UMPInfo info = ump.getInfo();
		for(UMPInfo.Service serv : info.data.services) {
			System.out.println(" "+serv.name+" "+info.data.hostname+":"+serv.port);
		}
	}

}
