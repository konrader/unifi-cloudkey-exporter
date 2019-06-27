package com.konrader.unifi.api;

public class TestUniFiController {

	public static void main(String[] args) {
		UniFiController uc = new UniFiController(args[0]);
		uc.login();
	}

}
