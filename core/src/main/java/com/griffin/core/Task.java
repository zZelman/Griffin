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
        This is where the task does it's parsing of the raw (whole) command string
        
        It allows the task to extract information from the raw (whole) command string if needed
    
        Override this method to change its behavior (ie parameters exist)

        It is recommended to use str.replaceFirst when removing the command from rawInput
        when the task is allowed to run multiple times in rawInput, use str.replace if not
    
        @arg rawInput the raw (whole) command string
        @return null if this task can not use the raw input, !null if the task can
                NOTE: this method MUST remove the command from the rawInput and return that new string
    */
    public String canUse(String rawInput) {
        if (rawInput.contains(this.getCommand())) {
            return rawInput.replaceFirst(this.getCommand(), "");
        }
        return null;
    }
    
    /**
        @arg comm is the direct connection to the caller of this task
                  It could be a View, or another Task
                  Do not use this for command output printing, use this.output
        @see output
    */
    abstract public String doAction(Communication prevComm);
}
