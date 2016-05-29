package com.griffin.core;

import com.griffin.core.*;

public abstract class Task {
    protected Output output;
    protected final String command;
    protected final String success;
    protected final String failure;
    
    public Task(Output output, String command, String success, String failure) {
        this.output = output;
        this.command = command;
        this.success = success;
        this.failure = failure;
    }
    
    public String getCommand() {
        return this.command;
    }
    
    abstract public String doAction();
}
