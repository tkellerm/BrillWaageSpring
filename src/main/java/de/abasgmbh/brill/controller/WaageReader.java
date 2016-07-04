/**
 * creation date: Jul 04, 2016
 * first author: marco
 * maintained by: marco
 * <p>
 * (C) Copyright abas Software AG, Karlsruhe, Germany; 2016
 */
package de.abasgmbh.brill.controller;

import de.abasgmbh.brill.config.Waage;
import org.apache.log4j.Logger;

import java.io.IOException;

public class WaageReader {

    Logger log = Logger.getLogger(WaagenController.class);

    private final Waage waage;
    private final WaageConnection wconn;

    public WaageReader(Waage waage, WaageConnection wconn) {
        this.waage = waage;
        this.wconn = wconn;
    }

    public void start() {

        try {
            String line = wconn.readline();


        } catch (IOException e) {
            log.error(e);
        }

    }
}
