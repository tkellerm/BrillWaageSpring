/**
 * creation date: Jul 03, 2016
 * first author: marco
 * maintained by: marco
 * <p>
 * (C) Copyright abas Software AG, Karlsruhe, Germany; 2016
 */
package de.brill;

import org.junit.Test;

import de.abasgmbh.brill.heartbeat.HeartBeat2;

public class SimpleTestWithoutContext {

    @Test
    public void testHeartBeat2() throws Exception {
        HeartBeat2 hb2 = new HeartBeat2();
        hb2.start(3);
        Thread.sleep(3000);
        hb2.stop();
    }

}
