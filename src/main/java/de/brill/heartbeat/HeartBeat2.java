/**
 * creation date: Jul 03, 2016
 * first author: marco
 * maintained by: marco
 * <p>
 * (C) Copyright abas Software AG, Karlsruhe, Germany; 2016
 */
package de.brill.heartbeat;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

public class HeartBeat2 {

    private ScheduledExecutorService scheduledExecutorService = null;

    public void start(int heartBeats) {
        this.scheduledExecutorService = taskExecutor();
        for (int i = 0; i < heartBeats; i++) {
            this.scheduledExecutorService.scheduleWithFixedDelay(new HeartBeat(), 100, 1000, TimeUnit.MILLISECONDS);
        }

    }

    public void stop() {
        if (this.scheduledExecutorService != null) {
            this.scheduledExecutorService.shutdownNow();
        }

    }

    private ScheduledExecutorService taskExecutor() {
        final ScheduledExecutorService executor = Executors.newScheduledThreadPool(10, withMyThreadFactory());
        return executor;
    }

    private ThreadFactory withMyThreadFactory() {
        return new CustomizableThreadFactory("heartbeat2-");
    }

    class HeartBeat implements Runnable {

        @Override
        public void run() {
            Logger.getLogger(HeartBeat.class).info("ping");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // ignore it
            }
            Logger.getLogger(HeartBeat.class).info("pong");
        }
    }

}
