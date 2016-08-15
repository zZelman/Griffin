package com.griffin.core.task;

import com.griffin.core.*;
import com.griffin.core.output.*;

public abstract class Task {
    private final String command;
    private final String description;
    protected String success;
    protected String failure;
    
    private String runtimeCommand;
    
    public Task(String command, String description) {
        this.command = command;
        this.description = description;

        this.resetRuntimeCommand();
    }
    
    public String getCommand() {
        return this.command;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public String getRuntimeCommand() {
        return this.runtimeCommand;
    }
    
    protected void setRuntimeCommand(String cmd) {
        this.runtimeCommand = cmd;
        this.success = cmd + ": success";
        this.failure = cmd + ": failure";
    }

    protected void resetRuntimeCommand() {
        this.setRuntimeCommand(this.command);
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
                NOTE: this method MUST set runTimeCommand
    */
    public String canUse(String rawInput) {
        if (rawInput.matches(".*\\b" + this.command + "\\b.*")) {
            this.resetRuntimeCommand();
            return rawInput.replaceFirst(this.command, "");
        }
        this.resetRuntimeCommand();
        return null;
    }
    
    /**
        @arg comm is the direct connection to the caller of this task
                  It could be a View, or another Task
                  Do not use this for command output printing, use this.output
        @see output
    */
    abstract public Output doAction(Communication prevComm);
    
    /**
        Remove any state that doAction or canUse had created to grantee thread safety across multiple runs
    
        Not abstract because not all tasks create state, it is ment to be overriden by those who do
    */
    public void clear() {
        this.resetRuntimeCommand();
    }
}
