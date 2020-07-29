package de.azubiag.htmltester;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class AutoTester {
	WebDriver driver;
	int anzahlReferenten;
	ArrayList<Referent> referentenListe;
	String link;
	PrintWriter printWriter;
	String kommentarZuReferent = "dddddddddd";
	String kommentarZuMassNamenVerlauf_Punkt1 = "aaaaaaaaa";
	String kommentarZuBetreung_Punkt2="bbbbbbbbb";
	String kommentarZuReferentenAllgemein_Punkt3="cccccccc";
	
	/**
	 * Webdriver von https://chromedriver.chromium.org/downloads runterladen und bei pfadChromedriver
	 * den Pfad angeben
	 *  Wird benötigt fürSystem.setProperty den Pfad angeben für andere Browser googlen nach webdriver
	 * und BrowserName
	 */
	String pfadChromedriver = "C:\\manuel.homeoffice\\chromedriver.exe";
	
	public AutoTester(String htmlLink, String ausgabeDatei) throws IOException {
		
		
		System.setProperty("webdriver.chrome.driver", pfadChromedriver);
		this.link = htmlLink;
		File file = new File(ausgabeDatei);
		FileWriter fileWriter = new FileWriter(file, true);
		printWriter = new PrintWriter(fileWriter);

	}

	private class RadioButton {
		String radioButtonGruppe;
		String geklickterButton;
	}

	private class Referent {
		String referentenName;
		ArrayList<RadioButton> radioButtons = new ArrayList<>();
		String kommentar = "";
		String id;

	}

	public void start(int anzahlFragebogen) throws IOException {

		ArrayList<String> clipBoardAntwortString = new ArrayList<>();

		for (int i = 0; i < anzahlFragebogen; i++) {
			printWriter.println("Teilnehmer" + i);
			clipBoardAntwortString.add(fragebogenAusfuellen());
		}

		printWriter.println("Alle Antwort Strings");
		for (String s : clipBoardAntwortString) {
			printWriter.println(s);
		}
		printWriter.close();

	}

	private String fragebogenAusfuellen() {
		String umfrageid;
		String startdatum;
		String enddatum;
		String massnameid;
		String seminarleitername;
		referentenListe = new ArrayList<>();
		driver = new ChromeDriver();
		driver.get(link);
		WebElement body = driver.findElement(By.tagName("body"));
		anzahlReferenten = Integer.parseInt(body.getAttribute("anzahlreferenten"));
		umfrageid = body.getAttribute("umfrageid");
		startdatum = driver.findElement(By.id("startdatum")).getText();
		enddatum = driver.findElement(By.id("enddatum")).getText();
		massnameid = driver.findElement(By.id("startdatum")).getText();
		seminarleitername = driver.findElement(By.id("seminarleitername")).getText();

		for (int i = 0; i < anzahlReferenten; i++) {
			referentenListe.add(makeReferent(i));
		}

		String[] antwortFeld_1 = massnahmenVerlaufPunkt_1();
		String[] antwortFeld_2 = massnahmenBetreuungPunkt_2();
		String antwortFeld_3 = bewertungReferentenPunkt_3();
		gebeReferentenDatenEin();
		driver.findElement(By.id("OKButton")).click();
		driver.quit();

		printWriter.println("Von Html-Seite " + link);
		printWriter.println("startdatum " + startdatum);
		printWriter.println("enddatum " + enddatum);
		printWriter.println("massnameid " + massnameid);
		printWriter.println("UmfrageID " + umfrageid);
		printWriter.println("seminarleitername " + seminarleitername);

		printWriter.println();

		printWriter.println("1.1 Wie empfinden Sie die Organisation der Massnahme ? " + "\t\t" + antwortFeld_1[0]);
		printWriter.println("1.2 Wie zufrieden sind Sie mit dem Massnahmenverlauf ?" + "\t\t" + antwortFeld_1[1]);
		printWriter.println("Was Sie uns noch mitteilen mÃ¶chten: " + "\t\t" + antwortFeld_1[2]);

		printWriter.println();

		printWriter.println("2.1 Wie zufrieden sind Sie mit der Betreuung durch uns ? " + "\t\t" + antwortFeld_2[0]);
		printWriter.println("Was Sie uns noch mitteilen mÃ¶chten: " + "\t\t" + antwortFeld_1[1]);
		printWriter.println();
		printWriter.println(
				"Welche Bewertungen bzw. Bemerkungen mÃ¶chten Sie zu den eingesetzen Referenten bzw Referentinnen abgeben ? "
						+ "\t\t" + antwortFeld_3);

		printWriter.println();

		String[] fragenAnReferenten = { "Wie war ihr/sein Unterricht vorbereitet ? ",
				"Wie umfangreich war ihr/sein Fachwissen ? ",
				"Wie ging sie/er auf spezielle thematische Probleme ein ? ",
				"Wie verstÃ¤ndlich sie/er die Inhalte vermitteln ? ",
				"Wie sagte Ihnen ihr/sein Verhalten gegenÃ¼ber den Seminarteilnehmern zu ?" };

		for (Referent referent : referentenListe) {
			int zaehler = 0;
			printWriter.println();
			printWriter.println("Referentenname " + referent.referentenName);
			for (RadioButton radioButton : referent.radioButtons) {
				String tempString = fragenAnReferenten[zaehler] + "\t\t" + radioButton.geklickterButton;
				zaehler++;
				printWriter.println(tempString);
			}
			printWriter.println("Bemerkungen" + "\t\t" + referent.kommentar);
		}

		String copyClipboard = kopierenVomClipboard();
		printWriter.println("~~~~~~~~~~~~~~~~~~");
		printWriter.println(" aus Zwischenablage");
		printWriter.println("~~~~~~~~~~~~~~~~~~");
		printWriter.println();
		printWriter.println(copyClipboard);
		printWriter.println();
		printWriter.println("~~~~~~~~~~~~~~~~~~");

		return copyClipboard;

	}

	private void gebeReferentenDatenEin() {
		for (Referent referent : referentenListe) {
			for (RadioButton radioButton : referent.radioButtons) {
				String idRadioButton = referent.id + radioButton.radioButtonGruppe;
				driver.findElement(
						By.xpath("(//input[@name=\'" + idRadioButton + "\'])[" + radioButton.geklickterButton + "]"))
						.click();
			}
			String idKommentar = referent.id + "_text";
			driver.findElement(By.id(idKommentar)).click();
			driver.findElement(By.id(idKommentar)).sendKeys(referent.kommentar);
		}

	}

	private Referent makeReferent(int referentenNummer) {

		Referent tempReferent = new Referent();
		String referentAttribut = "referent" + Integer.toString(referentenNummer);
		tempReferent.referentenName = driver.findElement(By.tagName("body")).getAttribute(referentAttribut);
		tempReferent.id = Integer.toString(referentenNummer);
		tempReferent.kommentar = kommentarZuReferent;
		int anzahlRadioButtonGruppen = 5;

		for (int i = 0; i < anzahlRadioButtonGruppen; i++) {
			String radioButtonGruppe = "_r";
			RadioButton tempRadioButton = new RadioButton();
			tempRadioButton.radioButtonGruppe = radioButtonGruppe + Integer.toString(i);
			int zufallsZahl = (int) ((Math.random() * 5) + 1);
			tempRadioButton.geklickterButton = Integer.toString(zufallsZahl);
			tempReferent.radioButtons.add(tempRadioButton);
		}
		return tempReferent;
	}

	private String[] massnahmenVerlaufPunkt_1() {
		int zufallsZahl = (int) (Math.random() * 5) + 1;
		String radioButton1_1 = Integer.toString(zufallsZahl);
		zufallsZahl = (int) (Math.random() * 5) + 1;
		String radioButton1_2 = Integer.toString(zufallsZahl);
		String textArea_1 = kommentarZuMassNamenVerlauf_Punkt1;
		driver.findElement(By.xpath("(//input[@name=\'a_r0\'])[" + radioButton1_1 + "]")).click();
		driver.findElement(By.xpath("(//input[@name=\'a_r1\'])[" + radioButton1_2 + "]")).click();
		driver.findElement(By.id("a_t0")).click();
		driver.findElement(By.id("a_t0")).sendKeys(textArea_1);
		String[] antwort = { radioButton1_1, radioButton1_2, textArea_1 };
		return antwort;

	}

	private String[] massnahmenBetreuungPunkt_2() {
		int zufallsZahl = (int) (Math.random() * 5) + 1;
		String radioButton2_1 = Integer.toString(zufallsZahl);
		String textArea_2 = kommentarZuBetreung_Punkt2;
		driver.findElement(By.xpath("(//input[@name=\'b_r0\'])[" + radioButton2_1 + "]")).click();
		driver.findElement(By.id("b_t0")).click();
		driver.findElement(By.id("b_t0")).sendKeys(textArea_2);
		String[] antwort = { radioButton2_1, textArea_2 };
		return antwort;

	}

	private String bewertungReferentenPunkt_3() {
		String textArea_3 =kommentarZuReferentenAllgemein_Punkt3;
		driver.findElement(By.id("c_t0")).click();
		driver.findElement(By.id("c_t0")).sendKeys(textArea_3);
		return textArea_3;
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
