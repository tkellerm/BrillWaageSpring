/**
 * creation date: Jun 28, 2016
 * first author: marco
 * maintained by: marco
 * <p>
 * (C) Copyright abas Software AG, Karlsruhe, Germany; 2016
 */
package de.brill.heartbeat;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class HeartBeat {

    public HeartBeat() {
        Logger.getLogger(HeartBeat.class).info("created");
    }

    @Scheduled(initialDelay = 1, fixedDelay=1000)
    private void pingpong() {
        Logger.getLogger(HeartBeat.class).info("ping");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // ignore it
        }
        Logger.getLogger(HeartBeat.class).info("pong");
    }

}
