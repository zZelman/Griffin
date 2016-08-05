package com.griffin.core;

import java.io.*;

import com.griffin.core.*;

public class ServerInfo implements Serializable {
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
        return "Info:\n" +
               "    name - " + this.name + "\n" +
               "    hostName - " + this.hostName + "\n" +
               "    port - " + this.port;
    }
}
