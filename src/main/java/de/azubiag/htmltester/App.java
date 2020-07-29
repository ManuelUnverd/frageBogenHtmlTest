package de.azubiag.htmltester;

import java.io.IOException;

public class App {

	public static void main(String[] args) throws IOException {
		/*
		WebSiteTest webSiteTest = new WebSiteTest("testdaten",
				"https://gfigithubaccess.github.io/fragebogen/john_paul_jones/fragebogen_john_paul_jones");
		webSiteTest.testen();
		*/
		String htmlLink = "file:///C:/Users/Admin/AppData/Local/MassnahmenBewertung/gfigithubaccess.github.io/fragebogen/msn/test.html";
		String ausgabeDatei = "C:\\manuel.homeoffice\\test1.txt";
		AutoTester autoTester = new AutoTester(htmlLink,ausgabeDatei);
		int anzahlderFragebogen = 2;
		autoTester.start(anzahlderFragebogen);

	}

}
