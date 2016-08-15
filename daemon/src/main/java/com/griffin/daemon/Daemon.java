package com.griffin.daemon;

import java.net.*;
import java.io.*;

import com.griffin.core.*;
import com.griffin.core.nameserver.*;
import com.griffin.core.server.*;
import com.griffin.daemon.task.*;

public class Daemon implements NameserverCallBack, ServerCallBack, Startable {
    private String fileName;
    private String target;
    
    private boolean isRunning;
    
    private ServerSocket serverSocket;
    private Thread serverThread;
    
    private NameserverPinger nameserverPinger;
    
    public Daemon(String fileName, String target) {
        this.fileName = fileName;
        this.target = target;

        this.isRunning = false;
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
    public void taskList(String taskList) {
        this.println(taskList);
    }
    
    @Override
    public void commandRecieved(String remoteAddr, String localAddr, String command) {
        this.println("[" + remoteAddr + "] " + command);
    }
    
    @Override
    public void serverEnding(String msg) {
        this.println(msg);
    }
    
    @Override
    public void dealWith(ClassNotFoundException e) {
        this.println(e.getMessage().toLowerCase());
    }
    
    @Override
    public void nameserverException(UnknownHostException e) {
        // caused by the NameserverPinger & if cant reach the nameserver, this server is dead in the water
        this.println("lost connection to the nameserver!!");
    }
    
    @Override
    public void nameserverException(IOException e) {
        // caused by the NameserverPinger & if cant reach the nameserver, this server is dead in the water
        this.println("lost connection to the nameserver!!");
    }
    
    @Override
    public void dealWith(IOException e) {
        this.println(e.getMessage().toLowerCase());
    }
    
    @Override
    public boolean start() {
        if (this.isRunning == true) {
            this.println("daemon is already running");
            return false;
        }
        
        this.isRunning = true;
        
        try {
            InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(this.fileName);
            ServerInfoParser infoParser = new ServerInfoParser(inputStream);
            
            Server server = new Server(this, this,
                                       infoParser, this.target,
                                       new DaemonTaskFactory(infoParser));
            
            this.serverThread = new Thread(server);
            this.serverThread.start();
        } catch (FileNotFoundException e) {
            this.isRunning = false;
            this.println(e);
        } catch (IOException e) {
            this.isRunning = false;
            this.println(e);
        } catch (ServerInfoException e) {
            this.isRunning = false;
            this.println(e);
        }
        
        return this.isRunning;
    }
    
    @Override
    public boolean stop() {
        if (this.isRunning == false) {
            this.println("daemon is not running");
            return false;
        }
        
        this.isRunning = false;
        
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            this.dealWith(e);
        }
        
        this.nameserverPinger.stop();
        
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
        System.out.println("    usage: [server_info_filename]");
        System.exit(1);
    }
    
    public static void main(String[] args) {
        if (args.length != 1) {
            Daemon.usage();
        }

        String target = "desktop";
        
        Daemon daemon = new Daemon(args[0], target);
        
        if (daemon.start()) {
            try {
                daemon.getThread().join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
