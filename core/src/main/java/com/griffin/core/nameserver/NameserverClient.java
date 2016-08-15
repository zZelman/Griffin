package com.griffin.core.nameserver;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import com.griffin.core.*;
import com.griffin.core.output.*;
import com.griffin.core.server.*;

public class NameserverClient {
    private final ServerInfo nameserver;
    
    public NameserverClient(ServerInfo info) {
        this.nameserver = info;
    }
    
    public void ping(ServerInfo info) throws UnknownHostException, IOException {
        NameserverAction action = new NameserverAction(info);
        
        Communication nextComm = null;
        try {
            nextComm = new Communication(this.nameserver.getHostName(), this.nameserver.getPort());
            nextComm.send(action);
        } finally {
            if (nextComm != null) {
                nextComm.close();
            }
        }
    }
    
    public ServerInfo get(String target) throws UnknownHostException, ClassNotFoundException, IOException {
        NameserverAction action = new NameserverAction(target);
        
        Communication nextComm = null;
        ServerInfo info = null;
        try {
            nextComm = new Communication(this.nameserver.getHostName(), this.nameserver.getPort());
            nextComm.send(action);
            
            Object ret;
            while (!Thread.currentThread().isInterrupted()) {
                ret = nextComm.receive();
                if (ret instanceof StopCommunication || ret == null) {
                    break;
                }
                
                if (ret instanceof NameserverNoEntry) {
                    info = null;
                }
                
                if (ret instanceof ServerInfo) {
                    info = (ServerInfo) ret;
                }
            }
        } finally {
            if (nextComm != null) {
                nextComm.close();
            }
        }
        
        return info;
    }
    
    public ConcurrentLinkedQueue<ServerInfo> list() throws UnknownHostException, ClassNotFoundException, IOException {
        NameserverAction action = new NameserverAction(NameserverAction.Action.LIST);
        
        Communication nextComm = null;
        ConcurrentLinkedQueue<ServerInfo> serverList = null;
        try {
            nextComm = new Communication(this.nameserver.getHostName(), this.nameserver.getPort());
            nextComm.send(action);
            
            Object ret;
            while (!Thread.currentThread().isInterrupted()) {
                ret = nextComm.receive();
                if (ret instanceof StopCommunication || ret == null) {
                    break;
                }
                
                if (ret instanceof ConcurrentLinkedQueue<?>) {
                    serverList = (ConcurrentLinkedQueue<ServerInfo>) ret;
                }
            }
        } finally {
            if (nextComm != null) {
                nextComm.close();
            }
        }
        
        return serverList;
    }

    public StringOutput help() throws UnknownHostException, ClassNotFoundException, IOException {
        NameserverAction action = new NameserverAction(NameserverAction.Action.HELP);
        
        Communication nextComm = null;
        StringOutput output = null;
        try {
            nextComm = new Communication(this.nameserver.getHostName(), this.nameserver.getPort());
            nextComm.send(action);
            
            Object ret;
            while (!Thread.currentThread().isInterrupted()) {
                ret = nextComm.receive();
                if (ret instanceof StopCommunication || ret == null) {
                    break;
                }

                if (ret instanceof StringOutput) {
                    output = (StringOutput) ret;
                }
            }
        } finally {
            if (nextComm != null) {
                nextComm.close();
            }
        }
        
        return output;
    }

    @Override
    public String toString() {
        return "NameserverClient:[nameserver:" + this.nameserver.toString() + "]";
    }

    @Override
    public boolean equals(Object o) {
        boolean ret = false;
        if (o instanceof NameserverClient) {
            NameserverClient other = (NameserverClient) o;
            
            ret = this.nameserver.equals(other.nameserver);
        }
        return ret;
    }
}
