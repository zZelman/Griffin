package com.griffin.core.output;

import java.util.*;

import com.griffin.core.output.*;

public class NewOutput implements Iterator<NewOutput> {
    private NewOutput subtask = null;
    private NewOutput next = null;
    private NewOutput end = null;
    private boolean doNextFirst = true;

    private NewOutput iterCurr = null;
    
    public void addOutput(NewOutput o) {
        if (this.next == null) {
            this.next = o;
            this.end = o;
            return;
        }
        
        if (this.end == null) {
            this.end = this.findEnd();
            this.end.next = o;
        } else {
            this.end.next = o;
            this.end = o;
        }
    }
    
    public void setSubtaskOutput(NewOutput o) throws SubtaskOutputException {
        if (this.end.subtask != null) {
            throw new SubtaskOutputException("subtask already set, loss of data will occor!");
        }
        
        this.end.subtask = o;
    }
    
    public void setOrdering(boolean doNextFirst) {
        this.doNextFirst = doNextFirst;
    }
    
    public boolean getOrdering() {
        return this.doNextFirst;
    }
    
    @Override
    public boolean hasNext() {
        return this.next != null;
    }
    
    @Override
    public NewOutput next() {
        return this.next;
    }
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
    
    private NewOutput findEnd() {
        NewOutput curr = this;
        while (curr.next != null) {
            curr = curr.next;
        }
        
        return curr;
    }
}
