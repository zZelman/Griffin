package com.griffin.core.recurring;

import java.io.*;
import java.util.concurrent.*;

import com.griffin.core.*;
import com.griffin.core.output.*;
import com.griffin.core.server.*;

public class RecurringManager {
    private Griffin griffin;
    private ServerCallBack serverCallBack;
    
    private ConcurrentLinkedQueue<RecurringJob> jobs;
    
    public RecurringManager(Griffin griffin, ServerCallBack serverCallBack) {
        this.griffin = griffin;
        this.serverCallBack = serverCallBack;
        
        this.jobs = new ConcurrentLinkedQueue<RecurringJob>();
    }
    
    public boolean add(String name, int period, String command) throws IOException {
        if (this.find(name) != null) {
            return false;
        }
        
        RecurringJob job = new RecurringJob(this.griffin, this.serverCallBack, name, period, command);
        if (job.start()) {
            this.jobs.add(job);
            return true;
        }
        
        return false;
    }
    
    public boolean remove(String name) {
        RecurringJob job = this.find(name);
        if (job == null) {
            return false;
        }
        
        job.stop();
        jobs.remove(job);
        
        return true;
    }
    
    public Output list() {
        if (this.jobs.isEmpty()) {
            return new StringOutput("no recurring tasks");
        }
        
        Output output = new Output();
        for (RecurringJob job : this.jobs) {
            output.addOutput(new StringOutput("name: " + job.getName() + ", period: " + job.getPeriod() + ", command: " + job.getCommand()));
        }
        return output;
    }
    
    private RecurringJob find(String name) {
        for (RecurringJob job : this.jobs) {
            if (name.equals(job.getName())) {
                return job;
            }
        }
        return null;
    }
}
