package com.griffin.desktop.daemon;

import java.net.*;
import java.io.*;

import com.griffin.core.*;
import com.griffin.nameserver.*;

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
        this.println(info.toFormatedString());
    }
    
    @Override
    public void taskList(String s) {
        this.println(s);
    }
    
    @Override
    public void startedConnection() {
        // this.println("connection");
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
        
        try {
            InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(this.fileName);
            ServerInfoParser infoParser = new ServerInfoParser(inputStream);

            ServerInfo info = infoParser.getServerInfo(this.target);
            
            NameserverClient nameserverClient = new NameserverClient(infoParser.getNameserverInfo());
            nameserverClient.ping(info);
            
            Server server = new Server(this, info, new DaemonTaskFactory(infoParser));
            
            this.serverThread = new Thread(server);
            this.serverThread.start();
        } catch (FileNotFoundException e) {
            Daemon.isRunning = false;
            this.println(e);
        } catch (IOException e) {
            Daemon.isRunning = false;
            this.println(e);
        } catch (ServerInfoException e) {
            Daemon.isRunning = false;
            this.println(e);
        }

        return Daemon.isRunning;
    }
    
    @Override
    public boolean stop() {
        if (Daemon.isRunning == false) {
            this.println("daemon is not running");
            return false;
        }

        Daemon.isRunning = false;
        
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
        if (args.length != 2) {
            Daemon.usage();
        }
        
        Daemon daemon = new Daemon(args[0], args[1]);
        
        if (daemon.start()) {
            try {
                daemon.getThread().join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
