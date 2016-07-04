/**
 * creation date: Jul 04, 2016
 * first author: marco
 * maintained by: marco
 * <p>
 * (C) Copyright abas Software AG, Karlsruhe, Germany; 2016
 */
package de.abasgmbh.brill.edp;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import de.abas.ceks.jedp.CantBeginSessionException;
import de.abas.ceks.jedp.CantReadSettingException;
import de.abas.ceks.jedp.EDPFactory;
import de.abas.ceks.jedp.EDPSession;
import de.abas.ceks.jedp.EDPVariableLanguage;
import de.abasgmbh.brill.registration.WaageRegistrationService;

import de.abasgmbh.brill.utils.Utils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EdpConnectionFactory {
	
	Logger log = Logger.getLogger(EdpConnectionFactory.class);
	
	private static final int TIMEOUT =  86400000; //Timeout ein Tag
	
	@Value("${edp.host}")
    private String edpHost;
	
	@Value("${edp.port}")
    private int edpPort;
	
	@Value("${edp.mandant}")
    private String edpMandant;
	
	@Value("${edp.passwd}")
    private String edpPassword;
	
	private EDPSession lizSession;
	private AtomicBoolean lizSessionPing = new AtomicBoolean(false);
	
	@PostConstruct
	public void lizenzBelegen(){
		lizSession = getSession();
		
	}
	
	
    public EDPSession getSession() {
    	
    	EDPSession edpSession = EDPFactory.createEDPSession ();
    	
    	while (!edpSession.isConnected()) {

			sessionAufbauen(edpHost, edpPort, edpMandant, edpPassword, edpSession);
			
		}
    	
        return edpSession;
    }
    
    
    
    private void sessionAufbauen(String server, int port, String mandant, String passwort , EDPSession edpSession)   
    { 
			log.trace("Session wird versucht aufzubauen Server: " + server + " Port : " + port + " Mandant : " + mandant + " Passwort : " + passwort);
    	  		try {
    	  			 new File("java/log/").mkdirs();
    	  			edpSession.loggingOn("java/log/waageEdp.log");
    	  			edpSession.setConnectTimeout(TIMEOUT);
    	  			edpSession.beginSession(server , port, mandant, passwort, "Waage");
    	  			log.trace("Session wurde aufgebaut");
                    edpSession.setVariableLanguage(EDPVariableLanguage.ENGLISH);
                    
    	  		} catch (CantBeginSessionException ex) {
    	  			log.error("EDP-Session kann nicht gestartet werden!" , ex);
					Utils.sleep(1000);
    	  		}
    }
    
    @PreDestroy
    public void LizenzFreigeben(){
    	this.lizSession.endSession();
    	this.lizSession = null;
    }
    
    @Scheduled(initialDelay = 1000 , fixedDelay = 5000)
    private void sessionCheck(){
    	
    	if (!lizSessionPing.compareAndSet(false, true)) {
//    		läuft schon
			return;
		}
    	try {
    		
			this.lizSession.getABASVersion();
			
		} catch (CantReadSettingException e) {
			log.error("Fehler beim EDP-Ping", e);
			lizSession = getSession();
		}finally{
			lizSessionPing.set(false);	
		}
    	
    	
    	
    }
    
}
