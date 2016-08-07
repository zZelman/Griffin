package com.griffin.nameserver;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

import com.griffin.core.*;
import com.griffin.core.output.*;

public class NameserverClient {
    public static void main(String[] args) {
        ServerInfo serverInfo = null;
        {
            String name = "nameserver";
            String hostName = "10.0.0.31";
            int port = 6001;
            serverInfo = new ServerInfo(name, hostName, port);
        }
        
        NameserverAction action = null;
        {
            String name = "daemon1";
            String hostName = "10.0.0.31";
            int port = 6000;
            // action = new NameserverAction(new ServerInfo(name, hostName, port)); // ping
            // action = new NameserverAction(name); // get
            // action = new NameserverAction(NameserverAction.Action.DUMP); // dump
            action = new NameserverAction(NameserverAction.Action.HELP); // help
        }
        
        Socket socket = null;
        Communication nextComm = null;
        try {
            socket = new Socket(serverInfo.getHostName(), serverInfo.getPort());
            nextComm = new Communication(socket);
            
            nextComm.send(action);
            
            Object ret;
            while (!Thread.currentThread().isInterrupted()) {
                ret = nextComm.receive();
                if (ret instanceof StopCommunication || ret == null) {
                    break;
                }
                
                if (ret instanceof NameserverNoEntry) {
                    continue;
                }
                
                // get
                if (ret instanceof ServerInfo) {
                    ServerInfo info = (ServerInfo) ret;
                    System.out.println(info.toFormatedString());
                }
                
                // dump
                if (ret instanceof ConcurrentLinkedQueue<?>) {
                    ConcurrentLinkedQueue<ServerInfo> serverList = (ConcurrentLinkedQueue<ServerInfo>) ret;
                    for (ServerInfo info : serverList) {
                        System.out.println(info.toFormatedString());
                    }
                }
                
                // help
                if (ret instanceof StringOutput) {
                    StringOutput out = (StringOutput) ret;
                    System.out.println(out.getString());
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                nextComm.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
