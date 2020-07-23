package de.azubiag.htmltester;

import java.io.IOException;

public class App {

	public static void main(String[] args) throws IOException {
		WebSiteTest webSiteTest = new WebSiteTest("testdaten");
		webSiteTest.testen();
		

	}

}
