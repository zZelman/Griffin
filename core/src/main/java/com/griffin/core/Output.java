package com.griffin.core;

import java.lang.*;

public class Output {
    private final StringBuffer messages = new StringBuffer();
    private final String delimiter = "\n";
    
    public void addMessage(String message) {
        this.messages.append(message + this.delimiter);
    }
    
    public void addExecutionMessage(String message) {
        this.addMessage("    " + message);
    }
    
    public String getMessages() {
        return this.messages.toString();
    }
    
    public void clear() {
        this.messages.setLength(0);
    }
    
    public void addDelimiter() {
        // message appends a delimiter to the end of every string
        this.addMessage("");
    }
}
