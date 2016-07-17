package com.griffin.desktop.cli;

import java.net.*;
import java.io.*;
import java.lang.*;
import java.util.*;

import org.apache.commons.lang3.*;

import com.griffin.core.*;

public class Cli implements ClientCallBack, Startable {
    private ServerInfo info;
    private Serializable command;
    private Client client;
    
    public Cli(ServerInfo info, Serializable command) {
        this.info = info;
        this.command = command;
        this.client = new Client(this, this.info, this.command);
    }
    
    @Override
    public void recieved(Object o) {
        System.out.println(o.toString());
    }
    
    @Override
    public void dealWith(UnknownHostException e) {
        e.printStackTrace();
    }
    
    @Override
    public void dealWith(ClassNotFoundException e) {
        e.printStackTrace();
    }
    
    @Override
    public void dealWith(IOException e) {
        e.printStackTrace();
    }
    
    @Override
    public boolean start() {
        return this.client.start();
    }
    
    @Override
    public boolean stop() {
        return this.client.stop();
    }
    
    public static void usage() {
        System.out.println("error in command line paramiters");
        System.out.println("    usage: [server_info_filename] [target] [command...]");
        System.exit(1);
    }
    
    public static void main(String[] args) {
        if (args.length < 3) {
            Cli.usage();
        }
        
        ServerInfo info = null;
        try {
            InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(args[0]);
            ServerInfoParser infoParser = new ServerInfoParser(inputStream);
            info = infoParser.getServerInfo(args[1]);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (ServerInfoException e) {
            e.printStackTrace();
            System.exit(1);
        }
        
        String[] commandTokens = ArrayUtils.subarray(args, 2, args.length);
        Serializable command = StringUtils.join(commandTokens, " ");
        
        Cli cli = new Cli(info, command);
        cli.start();
        cli.stop();
    }
}
