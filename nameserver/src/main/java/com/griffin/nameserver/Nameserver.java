package com.griffin.nameserver;

import java.net.*;
import java.io.*;

import com.griffin.core.*;
import com.griffin.core.output.*;

public class Nameserver implements Runnable, Startable {
    private final ServerInfo info;
    private final ServerSocket serverSocket;
    
    private static boolean isRunning = false;
    
    private Thread serverThread;
    
    private final String SERVER_STOPPING = "server has recieved the stop command, and is ending";
    private final String BAD_COMMAND = "first communication must be in String form";
    
    public Nameserver(ServerInfo info) throws IOException {
        this.info = info;
        
        this.serverSocket = new ServerSocket(info.getPort());
    }
    
    public Thread getThread() {
        return this.serverThread;
    }
    
    public void println(String s) {
        System.out.println("server thread: " + s);
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
        
        this.serverThread = new Thread(this);
        this.serverThread.start();
        
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
                    // the thread deals with checking firstInput
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
            System.out.println("comm thread: " + s);
        }
        
        public void println(Exception e) {
            System.out.println("comm thread:");
            e.printStackTrace();
        }
        
        @Override
        public void run() {
            try {
                System.out.println(this.action.toString());
                
                this.prevComm.send(new StopCommunication());
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
    }
    
    public static void main(String[] args) {
        String name = "nameserver";
        String hostName = "10.0.0.31";
        int port = 6001;
        
        Nameserver nameserver = null;
        try {
            nameserver = new Nameserver(new ServerInfo(name, hostName, port));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        
        if (nameserver != null && nameserver.start()) {
            try {
                nameserver.getThread().join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
