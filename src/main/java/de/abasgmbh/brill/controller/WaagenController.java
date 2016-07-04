/**
 * creation date: Jun 28, 2016
 * first author: marco
 * maintained by: marco
 * <p>
 * (C) Copyright abas Software AG, Karlsruhe, Germany; 2016
 */
package de.abasgmbh.brill.controller;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.brill.heartbeat.HeartBeat;
import de.brill.heartbeat.HeartBeat2;

@Component
public class WaagenController {

    @Autowired
    private Provider<HeartBeat> heartBeatProvider;
//    private HeartBeat2 hb2;

    @PostConstruct
    public void startWaagenHeartbeat() {
//        HeartBeat hb = this.heartBeatProvider.get();
//        hb = this.heartBeatProvider.get();
//        hb = this.heartBeatProvider.get();
//
//        this.hb2 = new HeartBeat2();
//        this.hb2.start(3);
    }

//    @PreDestroy
//    public void shutdown() {
//        if (this.hb2 != null) {
//            this.hb2.stop();
//        }
//    }
}
