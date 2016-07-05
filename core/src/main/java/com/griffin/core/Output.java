package com.griffin.core;

import java.lang.*;
import java.util.*;
import java.io.*;

public class Output implements Serializable {
    private final String newLine = "\n";
    private final String indent = "    ";
    
    private String startMessage;
    private List<String> executionMessages = new LinkedList<String>();
    private String returnMessage;
    private String endMessage;
    private List<String> otherMessages = new LinkedList<String>();
    
    public void setStartMessage(String message) {
        this.startMessage = message;
    }
    
    public void addExecutionMessage(String message) {
        this.executionMessages.add(message);
    }
    
    public void setReturnMessage(String message) {
        this.returnMessage = message;
    }
    
    public void setEndMessage(String message) {
        this.endMessage = message;
    }
    
    public void addOtherMessage(String message) {
        this.otherMessages.add(message);
    }
    
    public void clear() {
        this.startMessage = "";
        this.executionMessages.clear();
        this.returnMessage = "";
        this.endMessage = "";
        this.otherMessages.clear();
    }
    
    public void addOutput(Output other) {
        if (other.startMessage != null) {
            this.addExecutionMessage(other.startMessage);
        }
        
        if (!other.executionMessages.isEmpty()) {
            for (String msg : other.executionMessages) {
                this.addExecutionMessage(this.indent + msg);
            }
        }
        
        if (other.returnMessage != null) {
            this.addExecutionMessage(other.returnMessage);
        }
        
        if (other.endMessage != null) {
            this.addExecutionMessage(other.endMessage);
        }
        
        if (!other.otherMessages.isEmpty()) {
            for (String msg : other.otherMessages) {
                this.addExecutionMessage(msg);
            }
        }
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        
        if (this.startMessage != null) {
            sb.append(this.startMessage + this.newLine);
        }
        
        if (!this.executionMessages.isEmpty()) {
            for (String msg : this.executionMessages) {
                sb.append(msg + this.newLine);
            }
        }
        
        if (this.returnMessage != null) {
            sb.append(this.returnMessage + this.newLine);
        }
        
        if (this.endMessage != null) {
            sb.append(this.endMessage + this.newLine);
        }
        
        if (!this.otherMessages.isEmpty()) {
            for (String msg : this.otherMessages) {
                sb.append(msg + this.newLine);
            }
        }
        
        return sb.toString();
    }
}
