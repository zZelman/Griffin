package com.griffin.core.output;

import java.util.*;
import java.io.*;

import com.griffin.core.output.*;

public class Output implements Iterator<Output>, Serializable {
    private Output next = null;
    private Output subtask = null;
    private Output end = null;
    
    public void addOutput(Output o) {
        if (o == null) {
            return;
        }
        
        if (this.next == null) {
            this.next = o;
            this.end = o;
            return;
        }
        
        if (this.end == null) {
            this.end = this.findEnd();
            this.end.next = o;
        } else {
            if (this.end.next != null) {
                this.end = this.findEnd();
            }
            
            this.end.next = o;
            this.end = o;
        }
    }
    
    public void setSubtaskOutput(Output o) throws SubtaskOutputException {
        if (this.end == null) {
            this.end = this.findEnd();
        }
        
        if (this.end.subtask != null) {
            throw new SubtaskOutputException("subtask already set, loss of data will occor!");
        }
        
        this.end.subtask = o;
    }
    
    public boolean hasSubtaskOutput() {
        return this.subtask != null;
    }
    
    public Output getSubtaskOutput() {
        return this.subtask;
    }
    
    public boolean containsError() {
        Output curr = this;
        while (curr.hasNext()) {
            curr = curr.next();
            
            if (curr instanceof ErrorOutput) {
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public boolean hasNext() {
        return this.next != null;
    }
    
    @Override
    public Output next() {
        return this.next;
    }
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
    
    private Output findEnd() {
        Output curr = this;
        while (curr.next != null) {
            curr = curr.next;
        }
        
        return curr;
    }
}
