package com.griffin.core.recurring;

import java.io.*;
import java.util.concurrent.*;

import com.griffin.core.*;
import com.griffin.core.output.*;

public class RecurringManager {
    private Griffin griffin;
    
    private ConcurrentLinkedQueue<RecurringJob> jobs;
    
    public RecurringManager(Griffin griffin) {
        this.griffin = griffin;
        
        this.jobs = new ConcurrentLinkedQueue<RecurringJob>();
    }
    
    public boolean add(String name, int period, String command) throws IOException {
        if (this.find(name) != null) {
            return false;
        }
        
        RecurringJob job = new RecurringJob(this.griffin, name, period, command);
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
        return null;
    }
    
    private RecurringJob find(String name) {
        for (RecurringJob job : jobs) {
            if (name.equals(job.getName())) {
                return job;
            }
        }
        return null;
    }
}
