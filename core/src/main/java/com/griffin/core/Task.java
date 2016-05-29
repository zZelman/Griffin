package com.griffin.core;

public abstract class Task {
    protected final String command;
    protected final String success;
    protected final String failure;
    
    public Task(String command, String success, String failure) {
        this.command = command;
        this.success = success;
        this.failure = failure;
    }
    
    public String getCommand() {
        return this.command;
    }
    
    abstract public String doAction();
}
