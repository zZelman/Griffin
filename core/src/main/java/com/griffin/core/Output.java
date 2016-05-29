package com.griffin.core;

import java.lang.*;

public class Output {
    private StringBuffer messages = new StringBuffer();
    private String delimiter = "\n";
    
    public void addMessage(String message) {
        this.messages.append(this.delimiter);
        this.messages.append(message);
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
}
