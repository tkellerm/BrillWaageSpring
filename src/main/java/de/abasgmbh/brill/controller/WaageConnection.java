/**
 * creation date: Jul 03, 2016
 * first author: marco
 * maintained by: marco
 * <p>
 * (C) Copyright abas Software AG, Karlsruhe, Germany; 2016
 */
package de.abasgmbh.brill.controller;

import de.abasgmbh.brill.config.Waage;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

public class WaageConnection {

    Logger log = Logger.getLogger(WaageConnection.class);

    private Waage waage;
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private WaageWriteMutex mutex = new WaageWriteMutex();

    public WaageConnection(Waage waage) {
        this.waage = waage;
    }

    public String readline() throws IOException {
        if (reader == null) {
            throw new IOException("reader allready closed");
        }
        String s = this.reader.readLine();
        log.info(this.waage.getName() + " <- " + s);
        return s;
    }

    public void writeString(String s) throws IOException {
        if (this.writer == null) {
            return;
        }
        synchronized (this.mutex) {
            this.writer.append(s);
            this.writer.newLine();
            this.writer.flush();
            log.info(this.waage.getName() + " -> " + s);
        }
    }

    public void connect() throws Exception {
        this.socket = new Socket();
        this.socket.setTcpNoDelay(true);
        this.socket.connect(sockAddr());
        this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        this.writer = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
    }

    public void disconnect() {
        try {
            this.socket.shutdownOutput();
        } catch (Exception e) {
            log.warn("error on shutdownOutput ", e);
        }
        try {
            this.socket.shutdownInput();
        } catch (Exception e) {
            log.warn("error on shutdownInput ", e);
        }
        try {
            this.socket.close();
        } catch (Exception e) {
            log.warn("error on close ", e);
        }
        this.socket = null;
        try {
            this.reader.close();
        } catch (IOException e) {
            log.warn(e);
        }
        this.reader = null;
        try {
            this.writer.close();
        } catch (IOException e) {
            log.warn(e);
        }
        this.writer = null;
    }

    private InetSocketAddress sockAddr() {
        return new InetSocketAddress(this.waage.getIpadress(), this.waage.getPort());
    }

    public boolean isConnected() {
        return this.socket != null && this.socket.isConnected() && !this.socket.isOutputShutdown() && !this.socket.isClosed();
    }

    public Waage getWaage() {
        return this.waage;
    }

//    Nur zum Synchronisieren 
    class WaageWriteMutex {
    }
}
