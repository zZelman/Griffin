package com.griffin.core;

import com.griffin.core.*;

public abstract class Task {
    /**
        The textual output of this command
        It is returned to the whoever called this Task (either View or Task)
    */
    protected Output output;
    
    protected final String command;
    protected final String info;
    protected final String success;
    protected final String failure;
    
    public Task(Output output, String command, String info, String success, String failure) {
        this.output = output;
        
        this.command = command;
        this.info = info;
        this.success = success;
        this.failure = failure;
    }
    
    public String getCommand() {
        return this.command;
    }
    
    public String getInfo() {
        return this.info;
    }
    
    /**
        @arg comm is the direct connection to the caller of this task
                 It could be a View, or another Task
                 Do not use this for command output printing, use this.output
        @see output
    */
    abstract public String doAction(Communication prevComm);
}
