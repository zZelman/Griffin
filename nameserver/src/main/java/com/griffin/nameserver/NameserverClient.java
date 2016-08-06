package com.griffin.nameserver;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

import com.griffin.core.*;
import com.griffin.core.output.*;

public class NameserverClient {
    public static void main(String[] args) {
        ServerInfo info = null;
        {
            String name = "nameserver";
            String hostName = "10.0.0.31";
            int port = 6001;
            info = new ServerInfo(name, hostName, port);
        }
        
        NameserverAction action = null;
        {
            String name = "daemon";
            String hostName = "10.0.0.32";
            int port = 6000;
            // action = new NameserverAction(new ServerInfo(name, hostName, port)); // ping
            // action = new NameserverAction(name); // get
            // action = new NameserverAction(NameserverAction.Action.DUMP); // dump
            action = new NameserverAction(NameserverAction.Action.HELP); // help
        }
        
        Socket socket = null;
        Communication nextComm = null;
        try {
            socket = new Socket(info.getHostName(), info.getPort());
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
                if (ret instanceof LinkedList<?>) {
                    LinkedList<ServerInfo> enteries = (LinkedList<ServerInfo>) ret;
                    for (ServerInfo serverInfo : enteries) {
                        System.out.println(serverInfo);
                    }
                }

                // dump
                if (ret instanceof ConcurrentHashMap<?, ?>) {
                    ConcurrentHashMap<String, LinkedList<ServerInfo>> servers = (ConcurrentHashMap<String, LinkedList<ServerInfo>>) ret;
                    for (Map.Entry<String, LinkedList<ServerInfo>> entry : servers.entrySet()) {
                        String name = entry.getKey();
                        LinkedList<ServerInfo> enteries = entry.getValue();
                        
                        System.out.println(name);
                        for (ServerInfo serverInfo : enteries) {
                            System.out.println(serverInfo);
                        }
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
