package com.griffin.core;

import com.griffin.core.*;

public class ServerInfo {
    private String name;
    private String hostName;
    private int port;
    
    public ServerInfo(String name, String hostName, int port) {
        this.name = name;
        this.hostName = hostName;
        this.port = port;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getHostName() {
        return this.hostName;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public String toString() {
        return this.name + " " + this.hostName + " " + this.port;
    }
}
