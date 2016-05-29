package com.griffin.core;

import com.griffin.core.*;

public class ServerInfo {
    private String name;
    private String ip;
    private String port;
    
    public ServerInfo(String name, String ip, String port) {
        this.name = name;
        this.ip = ip;
        this.port = port;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getIp() {
        return this.ip;
    }
    
    public String getPort() {
        return this.port;
    }
    
    public String toString() {
        return this.name + " " + this.ip + " " + this.port;
    }
}
