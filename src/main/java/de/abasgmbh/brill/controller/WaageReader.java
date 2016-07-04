/**
 * creation date: Jul 04, 2016
 * first author: marco
 * maintained by: marco
 * <p>
 * (C) Copyright abas Software AG, Karlsruhe, Germany; 2016
 */
package de.abasgmbh.brill.controller;

import de.abas.ceks.jedp.EDPException;
import de.abasgmbh.brill.abasAnbindung.AbasRueckmeldung;
import de.abasgmbh.brill.abasAnbindung.Rueckmeldung;
import de.abasgmbh.brill.config.Waage;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;

public class WaageReader {

    private Logger log = Logger.getLogger(WaagenController.class);
    
    private final String ANFANGS_ZEICHEN =  Character.toString((char)2);
	private final String ENDE_ZEICHEN =  Character.toString((char)3);

    private final Waage waage;
    private final WaageConnection wconn;
    
    @Autowired
    AbasRueckmeldung abasrueckmeldung;
    
    public WaageReader(Waage waage, WaageConnection wconn) {
        this.waage = waage;
        this.wconn = wconn;
    }

    public void start() throws EDPException {
    	
    	String waageName = this.waage.getName();
    	ArrayList<String> rueckschlange = new ArrayList<String>();
    	
        try {
        	Boolean rueckMeldungActive = false;
            Boolean errorFlag = false;
        	
        	
        	while (!errorFlag) {
				String line = wconn.readline();
				log.info(waageName + " von Waage : " + line);
				
				if (line != null) {

					if (rueckMeldungActive) {
						if (line.contains(ENDE_ZEICHEN)) {
							String teilString[] = line.split(ENDE_ZEICHEN);
							if (teilString.length > 0) {
								line = line + teilString[0];
								rueckschlange.add(line);
								log.trace(waageName + " RückmeldungString : "
										+ line);
								rueckMeldungActive = false;
							}
							for (int i = 1; i < teilString.length; i++) {
								if ((teilString[i].contains(ENDE_ZEICHEN))
										& (teilString[i]
												.contains(ANFANGS_ZEICHEN))) {
									rueckschlange.add(teilString[i]);
									log.trace(waageName
											+ " RückmeldungString : "
											+ teilString[1]);
									rueckMeldungActive = false;
								} else if (teilString[i]
										.contains(ANFANGS_ZEICHEN)) {
									line = teilString[i];
									rueckMeldungActive = true;
								}
							}
						} else if (line.contains(ANFANGS_ZEICHEN)) {
							String teilString[] = line.split(ANFANGS_ZEICHEN);
							if (teilString.length > 0) {
								line = teilString[0];
								rueckMeldungActive = true;
							}

						}
					} else {
						//					Falls kein Anfangszeichen enthalten ist, wird auch keine Rückmeldung erzeugt.

						if (line != null) {
							if (line.contains(ANFANGS_ZEICHEN)) {
								String teilString[] = line
										.split(ANFANGS_ZEICHEN);
								if (teilString.length > 1) {
									//							Der Start beginnt ja erst nach dem Startzeichen
									for (int i = 1; i < teilString.length; i++) {
										if (teilString[i]
												.contains(ENDE_ZEICHEN)) {
											rueckschlange.add(teilString[1]);
											log.trace(waageName
													+ " RückmeldungString : "
													+ teilString[1]);
											rueckMeldungActive = false;
										} else {
											line = teilString[i];
											rueckMeldungActive = true;
										}
									}
								}
							}
						} else {
							log.error("Die Variable inputstring ist null.Es erfolgt einreconnect");
							//						Todo: reconnect = true;
						}

					}

				}
				
				verarbeiteteRueckmeldungsSchlange(rueckschlange);
			}
        	
        } catch (IOException e) {
            log.error(e);
            
        }

    }

	private void verarbeiteteRueckmeldungsSchlange(
			ArrayList<String> rueckschlange) throws EDPException, IOException {

		for (String rueckMeldungString : rueckschlange) {

			Rueckmeldung rueckMeldung = new Rueckmeldung(rueckMeldungString);
			
	        if (rueckMeldung.isRueckmeldung()) {
	        	rueckMeldung = this.abasrueckmeldung.meldung(rueckMeldung , this.waage);
	        	Integer led = rueckMeldung.getLed(); 
	            switch (led) {
				case 1:
	//				grüne Lampe anschalten
					rueckmeldungAnWaageSenden(LEDS.GREEN,rueckMeldung.getOfMenge());
					break;
				case 2:
	//				gelbe Lampe anschalten
					rueckmeldungAnWaageSenden(LEDS.YELLOW,rueckMeldung.getOfMenge());
					break;
				case 3:
	//				rote Lampe anschalten
					rueckmeldungAnWaageSenden(LEDS.RED,rueckMeldung.getOfMenge());
					break;
				case 4:
	//				leiser Piepser anschalten
					rueckmeldungAnWaageSenden(LEDS.PIEPSLEISE,rueckMeldung.getOfMenge());
					break;
				case 5:
	//				lauter Piepser anschalten
					rueckmeldungAnWaageSenden(LEDS.PIEPSLAUT,rueckMeldung.getOfMenge());
					break;	
				default:
					log.info("Break in Case Wert LED : " + led );
					break;
				}
	        	
			}
        }
		
		
	}
	
	private void rueckmeldungAnWaageSenden(LEDS led, Double ofMenge) throws IOException {
    	lampeAnschalten(led);
    	sendText("offene Menge : " + ofMenge.toString());
    	labelDrucken();
    	waageZurücksetzen();
	}

	private void fehlerAnWaage(String string) throws IOException {
		lampeAnschalten(LEDS.PIEPSLAUT);
		sendText(string);
		waageZurücksetzen();
		
	}
    
	private void sendText(String string) throws IOException {
		String befehl = "<CA" + string + "><NO" + this.waage.getTextdauer().toString() + "><CC>";
    	sendMessage(befehl);
    	log.info(this.waage.getName() + " <CA" + " " + string);
		
	}
	
	private void waageZurücksetzen() throws IOException {
		String befehl = "<FR>";
//		<VS0020> löscht den Betriebsauftrag aus der Waage 
		befehl = befehl + "<VS0020>";
    	sendMessage(befehl);
    	log.info(this.waage.getName() + " Waage zurücksetzen " + befehl);
	} 
	private void labelDrucken() throws IOException{
		String befehl = "<FP099>";
    	sendMessage(befehl);
    	log.info(this.waage.getName()  + " Label drucken" + " " + befehl);
		
	}
	
	private void lampeAnschalten(LEDS led) throws IOException {
		// TODO Auto-generated method stub
    	String befehl = led.getAnschaltCmdString() + "<NO" + this.waage.getLeuchtdauer().toString() + ">" + led.getAusschaltCmdString();
    	sendMessage(befehl);
    	log.info(this.waage.getName() + " Lampen Befehl : " + led.name() + " " + befehl);
	
	}
	  private void sendMessage(String s) throws IOException {
		  wconn.writeString(s);
	  }
	
}
