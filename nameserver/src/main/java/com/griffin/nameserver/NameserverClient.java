package com.griffin.nameserver;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import com.griffin.core.*;
import com.griffin.core.output.*;

public class NameserverClient {
    private final ServerInfo nameserver;
    
    public NameserverClient(ServerInfo info) {
        this.nameserver = info;
    }
    
    public void ping(ServerInfo info) throws UnknownHostException, IOException {
        NameserverAction action = new NameserverAction(info);
        
        Socket socket = null;
        Communication nextComm = null;
        try {
            socket = new Socket(this.nameserver.getHostName(), this.nameserver.getPort());
            nextComm = new Communication(socket);
            
            nextComm.send(action);
        } finally {
            if (nextComm != null) {
                nextComm.close();
            }
        }
    }
    
    public ServerInfo get(String target) throws UnknownHostException, ClassNotFoundException, IOException {
        NameserverAction action = new NameserverAction(target);
        
        Socket socket = null;
        Communication nextComm = null;
        ServerInfo info = null;
        try {
            socket = new Socket(this.nameserver.getHostName(), this.nameserver.getPort());
            nextComm = new Communication(socket);
            
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
    
    public ConcurrentLinkedQueue<ServerInfo> dump() throws UnknownHostException, ClassNotFoundException, IOException {
        NameserverAction action = new NameserverAction(NameserverAction.Action.DUMP);
        
        Socket socket = null;
        Communication nextComm = null;
        ConcurrentLinkedQueue<ServerInfo> serverList = null;
        try {
            socket = new Socket(this.nameserver.getHostName(), this.nameserver.getPort());
            nextComm = new Communication(socket);
            
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
        
        Socket socket = null;
        Communication nextComm = null;
        StringOutput output = null;
        try {
            socket = new Socket(this.nameserver.getHostName(), this.nameserver.getPort());
            nextComm = new Communication(socket);
            
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
    
    public static void main(String[] args) throws Exception {
        String name;
        String hostName;
        int port;
        
        name = "nameserver";
        hostName = "10.0.0.31";
        port = 6001;
        NameserverClient nameserverClient = new NameserverClient(new ServerInfo(name, hostName, port));

        // ping
        name = "nameserver";
        hostName = "10.0.0.31";
        port = 6001;
        nameserverClient.ping(new ServerInfo(name, hostName, port));

        // get
        name = "nameserver";
        ServerInfo info = nameserverClient.get(name);
        System.out.println(info.toFormatedString());

        // dump
        ConcurrentLinkedQueue<ServerInfo> serverList = nameserverClient.dump();
        for (ServerInfo server : serverList) {
            System.out.println(server.toFormatedString());
        }

        // help
        StringOutput output = nameserverClient.help();
        System.out.println(output.getString());
    }
}
