/**
 * creation date: Jun 28, 2016
 * first author: marco
 * maintained by: marco
 * <p>
 * (C) Copyright abas Software AG, Karlsruhe, Germany; 2016
 */
package de.abasgmbh.brill.controller;

import de.abasgmbh.brill.config.Waage;
import de.abasgmbh.brill.registration.WaageRegistrationService;
import de.brill.heartbeat.HeartBeat;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Provider;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WaagenController {

    Logger log = Logger.getLogger(WaagenController.class);

    @Autowired
    WaageRegistrationService registrationService;

    @Autowired
    private Provider<HeartBeat> heartBeatPtototyp;

    @Autowired
    private Provider<WaageReader> waageReaderPtototyp;

    private AtomicBoolean stopped = new AtomicBoolean(true);
    private Waage waage;
    private WaageConnection wconn;
    private WaageReader wreader;
    private HeartBeat heartBeat;

    public void start(Waage waage) {
        if (!this.stopped.compareAndSet(true, false)) {
            // bereits gestartet
            return;
        }
        this.waage = waage;
        this.registrationService.register(waage.getName());
        this.wconn = new WaageConnection(waage);
        try {
            this.wconn.connect();
        } catch (Exception e) {
            log.error("can not connect to waage: " + waage.getName(), e);
            this.registrationService.unRegister(waage.getName());
            return;
        }

        this.wreader = this.waageReaderPtototyp.get();
        this.wreader.start(waage, wconn, this);

        this.heartBeat = this.heartBeatPtototyp.get();
        this.heartBeat.start(this.wconn, this);

    }

    public void stop() {
        if (this.stopped.compareAndSet(false, true)) {
            log.info("stop connection to waage: " + waage.getName());
            this.heartBeat.stop();
            this.wconn.disconnect();
            this.registrationService.unRegister(waage.getName());
        }
    }

}
