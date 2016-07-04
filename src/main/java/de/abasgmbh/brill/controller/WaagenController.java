/**
 * creation date: Jun 28, 2016
 * first author: marco
 * maintained by: marco
 * <p>
 * (C) Copyright abas Software AG, Karlsruhe, Germany; 2016
 */
package de.abasgmbh.brill.controller;

import de.abasgmbh.brill.config.Waage;
import de.brill.heartbeat.HeartBeat;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Provider;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WaagenController {

    Logger log = Logger.getLogger(WaagenController.class);

    private WaageConnection wconn;
    private WaageReader wreader;

    public void start(Waage waage) {
        this.wconn = new WaageConnection(waage);
        try {
            this.wconn.connect();
            this.wreader = new WaageReader(waage, wconn);
            this.wreader.start();

        } catch (Exception e) {
            log.error("can not connect to waage: " + waage.getName(), e);
        }
    }


}
