package com.griffin.core.recurring;

import java.net.*;
import java.io.*;

import com.griffin.core.*;
import com.griffin.core.output.*;

public class RecurringJob implements Startable {
    private Griffin griffin;
    private String name;
    private int period;
    private String command;
    
    private Thread thread;
    
    public RecurringJob(Griffin griffin, String name, int period, String command) throws IOException {
        this.griffin = griffin;
        this.name = name;
        this.period = period;
        this.command = command;
        
        this.thread = new Thread(new JobThread());
    }
    
    public String getName() {
        return this.name;
    }
    
    public int getPeriod() {
        return this.period;
    }
    
    public String getCommand() {
        return this.command;
    }
    
    @Override
    public boolean start() {
        this.thread.start();
        return true;
    }
    
    @Override
    public boolean stop() {
        this.thread.interrupt();
        return true;
    }
    
    @Override
    public boolean equals(Object o) {
        boolean ret = false;
        if (o instanceof RecurringJob) {
            RecurringJob other = (RecurringJob) o;
            
            if (this.name.equals(other.name)) {
                ret = true;
            }
        }
        return ret;
    }
    
    @Override
    public String toString() {
        return "RecurringJob:[" +
               "name:" + this.name + ", " +
               "period:" + this.period + ", " +
               "command:" + this.command + "]";
    }
    
    private class JobThread implements Runnable {
        private Communication prevComm;
        private long sleepTime;
        
        public JobThread() throws IOException {
            this.prevComm = new Communication(); // default constructor means input/output are munched
            this.sleepTime = period * 1000; // sec
        }
        
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                // execute the command, and do nothing with it because there is no where to put it
                Output ret = griffin.doCommand(command, this.prevComm);
                
                try {
                    Thread.sleep(this.sleepTime);
                } catch (InterruptedException e) {
                    try {
                        if (this.prevComm != null) {
                            this.prevComm.close();
                        }
                    } catch (IOException ex) {}
                    
                    break;
                }
            }
        }
    }
}
