package com.griffin.core.server;

import java.io.*;

import com.griffin.core.*;

public class ServerInfo implements Serializable {
    private String name;
    private String hostname;
    private int port;
    
    public ServerInfo(String name, String hostname, int port) {
        this.name = name;
        this.hostname = hostname;
        this.port = port;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getHostName() {
        return this.hostname;
    }
    
    public int getPort() {
        return this.port;
    }

    public String toFormatedString() {
        return "ServerInfo:\n" +
               "    name - " + this.name + "\n" +
               "    hostname - " + this.hostname + "\n" +
               "    port - " + this.port;
    }
    
    @Override
    public String toString() {
        return "ServerInfo:[" +
               "name:" + this.name + ", " +
               "hostname:" + this.hostname + ", " +
               "port:" + this.port + "]";
    }
    
    @Override
    public boolean equals(Object o) {
        boolean ret = false;
        if (o instanceof ServerInfo) {
            ServerInfo other = (ServerInfo) o;

            // IP+Port first then if false Name, if nothing false
            if (this.hostname.equals(other.hostname) &&
                this.port == other.port) {
                ret = true;
            } else if (this.name.equals(other.name)) {
                ret = true;
            }

        }
        return ret;
    }
}
