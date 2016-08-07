package com.griffin.nameserver;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import com.griffin.core.*;
import com.griffin.core.output.*;

public class Nameserver implements Runnable, Startable {
    private String fileName;
    private String target;
    
    private static boolean isRunning = false;
    
    private ServerSocket serverSocket;
    private Thread serverThread;
    
    private ConcurrentLinkedQueue<ServerInfo> serverList;
    
    private final String SERVER_STOPPING = "server has recieved the stop command, and is ending";
    private final String BAD_COMMAND = "first communication must be in NameserverAction form";
    
    private final String BAD_ACTION = "action inside of given NamserverAction is incorrect";
    private final String HELP_MESSAGE = "TODO: help message";
    
    public Nameserver(String fileName, String target) {
        this.fileName = fileName;
        this.target = target;
        
        this.serverList = new ConcurrentLinkedQueue<ServerInfo>();
    }
    
    public Thread getThread() {
        return this.serverThread;
    }
    
    public void println(String s) {
        System.out.println(s);
    }
    
    public void println(Exception e) {
        System.out.println("server thread:");
        e.printStackTrace();
    }
    
    @Override
    public boolean start() {
        if (Nameserver.isRunning == true) {
            this.println("nameserver is already running");
            return false;
        }
        
        Nameserver.isRunning = true;
        
        try {
            InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(this.fileName);
            ServerInfoParser infoParser = new ServerInfoParser(inputStream);

            ServerInfo info = infoParser.getServerInfo(this.target);
            this.serverSocket = new ServerSocket(info.getPort());

            this.println(info.toFormatedString());
            
            this.serverThread = new Thread(this);
            this.serverThread.start();
        } catch (FileNotFoundException e) {
            Nameserver.isRunning = false;
            this.println(e);
        } catch (IOException e) {
            Nameserver.isRunning = false;
            this.println(e);
        } catch (ServerInfoException e) {
            Nameserver.isRunning = false;
            this.println(e);
        }
        
        return Nameserver.isRunning;
    }
    
    @Override
    public boolean stop() {
        if (Nameserver.isRunning == false) {
            this.println("nameserver is not running");
            return false;
        }
        
        Nameserver.isRunning = false;
        
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            this.println(e);
        }
        
        return true;
    }
    
    @Override
    public void run() {
        try {
            Socket clientSocket;
            Communication prevComm;
            Object firstInput;
            NameserverAction action;
            while (!Thread.currentThread().isInterrupted()) {
                clientSocket = this.serverSocket.accept();
                
                prevComm = new Communication(clientSocket);
                firstInput = prevComm.receive();
                
                if (firstInput instanceof NameserverAction) {
                    action = (NameserverAction) firstInput;
                    
                    // always check the first communication for an instance of the stop command
                    if (action.getAction() == NameserverAction.Action.STOP) {
                        prevComm.send(new StringOutput(SERVER_STOPPING));
                        prevComm.send(new StopCommunication());
                        break;
                    }
                    
                    // the thread deals with prevComm closing
                    new Thread(new CommunicationThread(prevComm, action)).start();
                } else {
                    prevComm.send(new StringOutput(BAD_COMMAND));
                    prevComm.send(new StopCommunication());
                }
            }
        } catch (ClassNotFoundException e) {
            this.println(e);
        } catch (IOException e) {
            this.println(e);
        } finally {
            try {
                this.serverSocket.close();
            } catch (IOException e) {
                this.println(e);
            }
        }
        
        this.println(SERVER_STOPPING);
    }
    
    class CommunicationThread implements Runnable {
        private final Communication prevComm;
        private final NameserverAction action;
        
        public CommunicationThread(Communication prevComm, NameserverAction action) {
            this.prevComm = prevComm;
            this.action = action;
        }
        
        public void println(String s) {
            System.out.println("[" + prevComm.getRemoteAddr() + "] " + s);
        }
        
        public void println(Exception e) {
            System.out.println("comm thread:");
            e.printStackTrace();
        }
        
        @Override
        public void run() {
            try {
                switch (this.action.getAction()) {
                    case PING:
                        this.doPing();
                        break;
                    case GET:
                        this.doGet();
                        break;
                    case DUMP:
                        this.doDump();
                        break;
                    case HELP:
                        this.doHelp();
                        break;
                    default:
                        // TODO: this might break clients that do not use NameserverClient, but needs to be here
                        prevComm.send(new StringOutput(BAD_ACTION));
                        this.prevComm.send(new StopCommunication());
                        break;
                }
            } catch (IOException e) {
                this.println(e);
            } finally {
                try {
                    this.prevComm.close();
                } catch (IOException e) {
                    this.println(e);
                }
            }
        }
        
        private void doPing() throws IOException {
            ServerInfo info = this.action.getInfo();
            this.println("ping: " + info.toString());
            
            serverList.remove(info); // remove first instance if exists
            serverList.add(info);
        }
        
        private void doGet() throws IOException {
            String target = this.action.getTarget();
            this.println("get: " + target);
            
            boolean sent = false;
            for (ServerInfo info : serverList) {
                if (info.getName().equals(target)) {
                    this.prevComm.send(info);
                    sent = true;
                    break;
                }
            }
            
            if (sent == false) {
                this.prevComm.send(new NameserverNoEntry());
            }
            this.prevComm.send(new StopCommunication());
        }
        
        private void doDump() throws IOException {
            this.println("dump");
            
            for (ServerInfo info : serverList) {
                this.println("    " + info.toString());
            }
            
            this.prevComm.send(serverList);
            this.prevComm.send(new StopCommunication());
        }
        
        private void doHelp() throws IOException {
            this.println("help");
            
            this.prevComm.send(new StringOutput(HELP_MESSAGE));
            this.prevComm.send(new StopCommunication());
        }
    }

    public static void usage() {
        System.out.println("error in command line paramiters");
        System.out.println("    usage: [server_list_filename] [target]");
        System.exit(1);
    }
    
    public static void main(String[] args) {
        if (args.length != 2) {
            Nameserver.usage();
        }
        
        Nameserver nameserver = new Nameserver(args[0], args[1]);
        
        if (nameserver.start()) {
            try {
                nameserver.getThread().join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
