/**
 * creation date: Jun 28, 2016
 * first author: marco
 * maintained by: marco
 * <p>
 * (C) Copyright abas Software AG, Karlsruhe, Germany; 2016
 */
package de.abasgmbh.brill.heartbeat;

import de.abasgmbh.brill.controller.WaageConnection;
import de.abasgmbh.brill.controller.WaagenController;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class HeartBeat {

    Logger log = Logger.getLogger(HeartBeat.class);
    private boolean run = true;

    @Value("${heartbeat.delay}")
    private long heartBeatDelay;

    @Async
    public void start(WaageConnection wconn, WaagenController wctrl) {
        sleep();
        try {
            while(this.run) {
                Logger.getLogger(HeartBeat.class).info("ping to waage " + wconn.getWaage().getName());
                wconn.writeString(getPingCmd());
                sleep();
            }
        } catch (IOException e) {
            log.error("connection unterbrochen zu waage " + wconn.getWaage().getName());
            wctrl.stop();
        }
    }

    public void stop() {
        this.run = false;
    }

    private void sleep() {
        try {
            Thread.sleep(this.heartBeatDelay);
        } catch (InterruptedException e) {
            // ignore it
        }
    }
    private String getPingCmd() {
        return "ping";
    }
}
