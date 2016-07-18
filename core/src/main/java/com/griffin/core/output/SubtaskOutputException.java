package com.griffin.core.output;

public class SubtaskOutputException extends Exception {
    public SubtaskOutputException() {
    }
    
    public SubtaskOutputException(String message) {
        super(message);
    }
    
    public SubtaskOutputException(Throwable cause) {
        super(cause);
    }
    
    public SubtaskOutputException(String message, Throwable cause) {
        super(message, cause);
    }
}
