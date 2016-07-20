package com.griffin.desktop.daemon;

import java.net.*;
import java.io.*;

import com.griffin.core.*;

public class Daemon implements ServerCallBack, Startable {
    private String fileName;
    private String target;
    
    private static boolean isRunning = false;
    
    private ServerSocket serverSocket;
    private Thread serverThread;
    
    public Daemon(String fileName, String target) {
        this.fileName = fileName;
        this.target = target;
    }

    public Thread getThread() {
        return this.serverThread;
    }
    
    @Override
    public void startedServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }
    
    @Override
    public void serverInfo(ServerInfo info) {
        this.println(info.toString());
    }
    
    @Override
    public void taskList(String s) {
        this.println(s);
    }
    
    @Override
    public void startedConnection() {
        this.println("connection");
    }
    
    @Override
    public void commandRecieved(String s) {
        this.println("input: [" + s + "]");
    }
    
    @Override
    public void serverEnding(String s) {
        this.println(s);
    }
    
    @Override
    public void dealWith(ClassNotFoundException e) {
        this.println(e.getMessage().toLowerCase());
    }
    
    @Override
    public void dealWith(IOException e) {
        this.println(e.getMessage().toLowerCase());
    }
    
    @Override
    public boolean start() {
        if (Daemon.isRunning == true) {
            this.println("daemon is already running");
            return false;
        }
        
        Daemon.isRunning = true;
        this.println("daemon started");
        
        try {
            InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(this.fileName);
            ServerInfoParser infoParser = new ServerInfoParser(inputStream);
            
            TaskFactory taskFactory = new DaemonTaskFactory(infoParser);
            Server server = new Server(this, infoParser.getServerInfo(this.target), taskFactory);
            
            this.serverThread = new Thread(server);
            this.serverThread.start();
        } catch (FileNotFoundException e) {
            Daemon.isRunning = false;
            this.println(e);
            return false;
        } catch (IOException e) {
            Daemon.isRunning = false;
            this.println(e);
            return false;
        } catch (ServerInfoException e) {
            Daemon.isRunning = false;
            this.println(e);
            return false;
        }

        return true;
    }
    
    @Override
    public boolean stop() {
        if (Daemon.isRunning == false) {
            this.println("daemon is not running");
            return false;
        }
        
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            this.dealWith(e);
        }

        return true;
    }
    
    private void println(String s) {
        System.out.println(s);
    }
    
    private void println(Exception e) {
        e.printStackTrace();
    }
    
    public static void usage() {
        System.out.println("error in command line paramiters");
        System.out.println("    usage: [server_info_filename] [target]");
        System.exit(1);
    }
    
    public static void main(String[] args) {
        if (args.length < 2) {
            Daemon.usage();
        }
        
        Daemon d = new Daemon(args[0], args[1]);
        
        if (d.start()) {
            try {
                d.getThread().join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
