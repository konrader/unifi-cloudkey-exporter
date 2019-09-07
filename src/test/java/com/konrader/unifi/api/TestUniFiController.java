package com.konrader.unifi.api;

import java.io.IOException;

public class TestUniFiController {

	public static void main(String[] args) throws IOException {
		UniFiController uc = new UniFiController(args[0], args[1], args[2]);
		System.out.println("sites: " + uc.sites());
		for (UnifiDevice dev : uc.statDevice()) {
			System.out.println("" + dev);
		}
	}

}
