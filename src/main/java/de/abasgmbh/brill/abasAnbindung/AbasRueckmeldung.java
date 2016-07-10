package de.abasgmbh.brill.abasAnbindung;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.abas.ceks.jedp.CantChangeFieldValException;
import de.abas.ceks.jedp.CantSaveException;
import de.abas.ceks.jedp.EDPEditAction;
import de.abas.ceks.jedp.EDPEditRefType;
import de.abas.ceks.jedp.EDPEditor;
import de.abas.ceks.jedp.EDPException;
import de.abas.ceks.jedp.EDPQuery;
import de.abas.ceks.jedp.EDPSession;
import de.abas.ceks.jedp.InvalidQueryException;
import de.abas.ceks.jedp.InvalidRowOperationException;
import de.abas.ceks.jedp.StandardEDPSelection;
import de.abas.ceks.jedp.StandardEDPSelectionCriteria;
import de.abasgmbh.brill.config.Waage;
import de.abasgmbh.brill.edp.EdpConnectionFactory;

@Component 
public class AbasRueckmeldung  {

//	Holt aus der EdpFactory die EdpSession
	
	@Autowired 
	EdpConnectionFactory edpConnectionFactory;
	
	private static Logger log = Logger.getLogger(AbasRueckmeldung.class);
	
	
	
	public  Rueckmeldung meldung(Rueckmeldung rueckMeldung , Waage waage) throws EDPException {
		Integer wert = 0;
		
		EDPSession edpSession = this.edpConnectionFactory.getSession();
		try{
		EDPEditor editor = edpSession.createEditor();
		erzeugeRueckmeldung(rueckMeldung , editor , waage);
		EDPQuery query = edpSession.createQuery();
		Double prozent = selectOffeneMengeBa(rueckMeldung, query); 
		if (prozent > waage.getGelbGrenze()) {
//			GrünLeuchtet
			wert = 1;
		}else if (prozent <= waage.getGelbGrenze() && prozent > waage.getRotGrenze() ) {
//			Gelb leuchtet
			wert = 2; 
		}else {
//			Rot leuchtet
			wert = 3;
		}
		rueckMeldung.setLed(wert);
		log.trace("Rückgabe Meldung : Prozent :" + prozent + " rotGrenze : " + waage.getRotGrenze().toString() + "gelbGrenze : " + waage.getGelbGrenze().toString() +"  R�ckgabe Wert :" + wert);
		
		return rueckMeldung ;
		}finally{
			this.edpConnectionFactory.releaseSession(edpSession);
		}
	}
	
	private Double selectOffeneMengeBa(Rueckmeldung rueckMeldung, EDPQuery query) throws EDPException {
		
		String betriebauftrag = rueckMeldung.getBetriebsauftrag();
		String krit = "idno=="  + betriebauftrag +  ";@englvar=true;@language=en;" ;		
		StandardEDPSelectionCriteria criteria = new StandardEDPSelectionCriteria(krit);
		StandardEDPSelection edpcriteria = new StandardEDPSelection("9:1", criteria); 
		edpcriteria.setDatabase("9");
		edpcriteria.setGroup("1");
		String[] fieldNames = {"id" , "unitQty" , "confYieldSUTotal" };
		
//			
			try {
				query.startQuery(edpcriteria, fieldNames);
				query.getLastRecord();
				int anzahlTreffer = query.getRecordCount();
				if (anzahlTreffer == 1) {
//					String[] fields = query.getFields();
//					int fieldCount = query.getFieldCount();
					String offeneMengeStr = query.getField("unitQty");
					String rueckgemeldeteMengeStr = query.getField("confYieldSUTotal");
					Double offeneMenge = new Double(offeneMengeStr);
					Double rueckgemeldeteMenge = new Double(rueckgemeldeteMengeStr);
					Double gesamtmenge = offeneMenge + rueckgemeldeteMenge;
					Double prozent = (offeneMenge /gesamtmenge ) * 100;
					rueckMeldung.setOfMenge(offeneMenge);
					log.trace("Query Ergebnis : BA : " + betriebauftrag + " offene Menge : " + offeneMengeStr + " Gebuchte Menge : " + rueckgemeldeteMengeStr +   " prozent " + prozent.toString());
					return prozent;
					
				}
			
			} catch (InvalidQueryException e) {
				log.error(e);
				throw new EDPException("Fehler in Query" , e);
			}finally{
				query.breakQuery();
			}
			throw new EDPException("Kein Treffer in der Selektion");
		
	}

	private void erzeugeRueckmeldung(Rueckmeldung rueckMeldung, EDPEditor editor, Waage waage) throws EDPException {
		
		
	        String databaseName = "9";
            String groupName = "1";
            
            String asnum = rueckMeldung.getBetriebsauftrag();
	        
	        log.trace("Begin EDP-EDITOR f�r R�ckmeldung " + asnum );
		editor.beginEdit(EDPEditAction.DONE, databaseName , groupName,EDPEditRefType.NUMSW , asnum);
		
//		Bemerkung füllen 
		try {
//			editor.setFieldVal("comments" , "Nr: " + rueckMeldung.getLaufendeNummer() + " W: " + rueckMeldung.getWaagenNummer() + " NG: " + rueckMeldung.getNettoGewicht().toString() + " refG: " + rueckMeldung.getRefgewicht().toString() );
			editor.setFieldVal("comments" , "Nr: " + rueckMeldung.getLaufendeNummer() + " W: " + waage.getName() + " NG: " + rueckMeldung.getNettoGewicht().toString() + " refG: " + rueckMeldung.getRefgewicht().toString() );
			editor.setFieldVal("printImmediately", "1");
			if (editor.getRowCount() == 0) {
				editor.insertRow(1);		
			}
			editor.setFieldVal(1, "confYieldSU" , rueckMeldung.getStueck().toString());
			editor.endEditSave();
		} catch (CantChangeFieldValException e) {
			log.error(e);
			throw new EDPException("Es konnte ein Feld nicht beschrieben werden!", e);
		} catch (InvalidRowOperationException e) {
			log.error(e);
			throw new EDPException("Zeilenoperation fehlgeschlagen!",e);
		} catch (CantSaveException e) {
			log.error(e);
			throw new EDPException("Es konnte nicht gespeichert werden", e);
		}finally{
			if (editor.isActive()) {
				editor.endEditCancel();
			}
		}
		log.trace("Ende EDP-EDITOR für Rückmeldung " + asnum );
	}

		
}
