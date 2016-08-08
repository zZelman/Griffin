package com.griffin.nameserver;

import java.net.*;
import java.io.*;

import com.griffin.core.*;

public class NameserverPinger implements Startable {
    private PingThread pingThread;
    
    public NameserverPinger(NameserverCallback callback, ServerInfo nameserverInfo, ServerInfo info) {
        this.pingThread = new PingThread(callback, nameserverInfo, info);
    }
    
    @Override
    public boolean start() {
        new Thread(this.pingThread).start();
        return true;
    }
    
    @Override
    public boolean stop() {
        this.pingThread.terminate();
        return true;
    }
    
    public class PingThread implements Runnable {
        private NameserverCallback callBack;
        private NameserverClient nameserverClient;
        private ServerInfo info;
        
        private volatile boolean isRunning;
        
        private final int INTERVAL = 5; // mins
        private long sleepTime;

        public PingThread(NameserverCallback callBack, ServerInfo nameserverInfo, ServerInfo info) {
            this.callBack = callBack;
            this.nameserverClient = new NameserverClient(nameserverInfo);
            this.info = info;
            
            this.sleepTime = INTERVAL * 1000 * 60;
        }
        
        public void terminate() {
            this.isRunning = false;
        }
        
        @Override
        public void run() {
            this.isRunning = true;
            while (this.isRunning) {
                try {
                    this.nameserverClient.ping(this.info);
                    Thread.sleep(this.sleepTime);
                } catch (UnknownHostException e) {
                    this.callBack.nameserverException(e);
                } catch (IOException e) {
                    this.callBack.nameserverException(e);
                } catch (InterruptedException e) { }
            }
        }
    }
}
