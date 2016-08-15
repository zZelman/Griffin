package com.griffin.core.nameserver;

import java.net.*;
import java.io.*;

import com.griffin.core.*;
import com.griffin.core.server.*;

public class NameserverPinger implements Startable {
    private Pinger pinger;
    private Thread pingThread;
    
    public NameserverPinger(NameserverCallBack callback, ServerInfo nameserverInfo, ServerInfo info) {
        this.pinger = new Pinger(callback, nameserverInfo, info);
    }
    
    @Override
    public boolean start() {
        this.pingThread = new Thread(this.pinger);
        this.pingThread.start();
        return true;
    }
    
    @Override
    public boolean stop() {
        this.pingThread.interrupt();
        return true;
    }
    
    private class Pinger implements Runnable {
        private NameserverCallBack callBack;
        private NameserverClient nameserverClient;
        private ServerInfo info;
        
        private final int INTERVAL = 5;
        private long sleepTime;
        
        public Pinger(NameserverCallBack callBack, ServerInfo nameserverInfo, ServerInfo info) {
            this.callBack = callBack;
            this.nameserverClient = new NameserverClient(nameserverInfo);
            this.info = info;
            
            this.sleepTime = INTERVAL * 1000 * 60; // mins
            // this.sleepTime = INTERVAL * 1000; // sec
        }
        
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    this.nameserverClient.ping(this.info);
                } catch (UnknownHostException e) {
                    this.callBack.nameserverException(e);
                } catch (IOException e) {
                    this.callBack.nameserverException(e);
                }
                
                try {
                    Thread.sleep(this.sleepTime);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }
}
