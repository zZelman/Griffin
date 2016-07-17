package com.griffin.core;

public class ServerInfoException extends Exception {
    public ServerInfoException() {
    }
    
    public ServerInfoException(String message) {
        super(message);
    }
    
    public ServerInfoException(Throwable cause) {
        super(cause);
    }
    
    public ServerInfoException(String message, Throwable cause) {
        super(message, cause);
    }
}
