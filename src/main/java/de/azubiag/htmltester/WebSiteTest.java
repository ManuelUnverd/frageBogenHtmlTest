package de.azubiag.htmltester;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * Webdriver von https://chromedriver.chromium.org/downloads runterladen und bei
 * System.setProperty den Pfad angeben Für andere Browser googlen nach webdriver
 * und BrowserName
 * 
 * @author manuel.unverdorben
 */
public class WebSiteTest {

	ArrayList<RadioButton> radioButtonCollection = new ArrayList<>();
	ArrayList<ReferentenKommentar> referentenKommentarCollection = new ArrayList<>();
	Properties properties = new Properties();
	WebDriver driver;
	int anzahlReferenten;
	String linkFragebogen;

	private class RadioButton {
		String id;
		String geklickterButton;
	}

	private class ReferentenKommentar {
		String id;
		String kommentar;
	}

	public WebSiteTest(String testdaten, String linkFragebogen) throws IOException {
		this.linkFragebogen = linkFragebogen;
		System.setProperty("webdriver.chrome.driver", "/home/chrx/chromedriver");
		InputStream input = new FileInputStream(testdaten);
		properties.load(input);
		anzahlReferenten = Integer.parseInt(properties.getProperty("anzahlReferenten"));
		driver = new ChromeDriver();
	}

	public void testen() {
		driver.get(linkFragebogen);

		massnahmenVerlaufPunkt_1();
		massnahmenBetreuungPunkt_2();
		bewertungReferentenPunkt_3();

		radioButtonsEinlesen();
		referentenKommentarEinlesen();

		refAntwortTextEingeben();
		refAntwortRadioButtonsDrücken();
		driver.findElement(By.id("OKButton")).click();
		System.out.println(kopierenVomClipboard());
		driver.quit();

	}

	private void massnahmenVerlaufPunkt_1() {

		String radioButton1_1 = properties.getProperty("radioButton1_1");
		String radioButton1_2 = properties.getProperty("radioButton1_2");
		String textArea_1 = properties.getProperty("textArea_1");
		driver.findElement(By.xpath("(//input[@name=\'a_r0\'])[" + radioButton1_1 + "]")).click();
		driver.findElement(By.xpath("(//input[@name=\'a_r1\'])[" + radioButton1_2 + "]")).click();
		driver.findElement(By.id("a_t0")).click();
		driver.findElement(By.id("a_t0")).sendKeys(textArea_1);

	}

	private void massnahmenBetreuungPunkt_2() {
		String radioButton2_1 = properties.getProperty("radioButton2_1");
		String textArea_2 = properties.getProperty("textArea_2");
		driver.findElement(By.xpath("(//input[@name=\'b_r0\'])[" + radioButton2_1 + "]")).click();
		driver.findElement(By.id("b_t0")).click();
		driver.findElement(By.id("b_t0")).sendKeys(textArea_2);

	}

	// Datei mit Testdaten einlesen

	private void bewertungReferentenPunkt_3() {
		String textArea_3 = properties.getProperty("textArea_3");
		driver.findElement(By.id("c_t0")).click();
		driver.findElement(By.id("c_t0")).sendKeys(textArea_3);
	}

	private void radioButtonsEinlesen() {
		// Variabeln für die Erfassung der Radiobuttons

		int anzahlRadioButtonGruppen = 5;
		String aufBauRadioButtonGruppeId;
		String radioButtonGruppeId;

		// Radio buttons einlesen
		// Schleife die durch alle Referenten durchgeht
		for (int i = 0; i < anzahlReferenten; i++) {
			aufBauRadioButtonGruppeId = "_r";
			aufBauRadioButtonGruppeId = Integer.toString(i) + aufBauRadioButtonGruppeId;
			// innere Schleife für alle Radiobuttongruppen eines Referenten
			for (int j = 0; j < anzahlRadioButtonGruppen; j++) {
				radioButtonGruppeId = "";
				radioButtonGruppeId = aufBauRadioButtonGruppeId + Integer.toString(j);
				RadioButton temp = new RadioButton();
				temp.id = radioButtonGruppeId;
				temp.geklickterButton = properties.getProperty(radioButtonGruppeId);
				radioButtonCollection.add(temp);

			}
		}
	}

	private void referentenKommentarEinlesen() {
		// Kommentar Text zu Referenten einlesen
		for (int i = 0; i < anzahlReferenten; i++) {
			String textKey = "_text";
			textKey = Integer.toString(i) + textKey;
			ReferentenKommentar temp = new ReferentenKommentar();
			temp.id = textKey;
			temp.kommentar = properties.getProperty(textKey);
			referentenKommentarCollection.add(temp);
		}
	}

	private void refAntwortTextEingeben() {
		for (ReferentenKommentar r : referentenKommentarCollection) {
			driver.findElement(By.id(r.id)).click();
			driver.findElement(By.id(r.id)).sendKeys(r.kommentar);
		}
	}

	private void refAntwortRadioButtonsDrücken() {
		for (RadioButton r : radioButtonCollection) {
			driver.findElement(By.xpath("(//input[@name=\'" + r.id + "\'])[" + r.geklickterButton + "]")).click();
		}
	}

	private String kopierenVomClipboard() {
		String text = "";
		try {
			text = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
		} catch (HeadlessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedFlavorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return text;

	}

}
