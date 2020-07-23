package de.azubiag.htmltester;

import java.io.IOException;

public class App {

	public static void main(String[] args) throws IOException {
		WebSiteTest webSiteTest = new WebSiteTest("testdaten",
				"https://gfigithubaccess.github.io/fragebogen/john_paul_jones/fragebogen_john_paul_jones");
		webSiteTest.testen();
		

	}

}
