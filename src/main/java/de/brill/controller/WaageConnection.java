/**
 * creation date: Jul 03, 2016
 * first author: marco
 * maintained by: marco
 * <p>
 * (C) Copyright abas Software AG, Karlsruhe, Germany; 2016
 */
package de.brill.controller;

import java.net.InetSocketAddress;
import java.net.Socket;

public class WaageConnection {

    final private String host = "host";
    final private int port = 20000;
    private Socket socket;

    public void start() {
        try {
            this.socket = new Socket();
            this.socket.setTcpNoDelay(true);
            this.socket.connect(sockAddr());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            this.socket.shutdownOutput();
            this.socket.shutdownInput();
            this.socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.socket = null;
    }

    private InetSocketAddress sockAddr() {
        return new InetSocketAddress(this.host, this.port);
    }

    public boolean isConnected() {
        return this.socket != null && this.socket.isConnected() && !this.socket.isOutputShutdown() && !this.socket.isClosed();
    }

}
