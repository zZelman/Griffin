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

    public String toFormatedString() {
        return "ServerInfo:\n" +
               "    name - " + this.name + "\n" +
               "    hostName - " + this.hostName + "\n" +
               "    port - " + this.port;
    }
    
    @Override
    public String toString() {
        return "ServerInfo:[" +
               "name:" + this.name + ", " +
               "hostName:" + this.hostName + ", " +
               "port:" + this.port + "]";
    }
    
    @Override
    public boolean equals(Object o) {
        boolean ret = false;
        if (o instanceof ServerInfo) {
            ServerInfo other = (ServerInfo) o;

            // IP+Port first then if false Name, if nothing false
            if (this.hostName.equals(other.hostName) &&
                this.port == other.port) {
                ret = true;
            } else if (this.name.equals(other.name)) {
                ret = true;
            }

        }
        return ret;
    }
}
