package com.griffin.desktop.cli;

import java.net.*;
import java.io.*;
import java.lang.*;
import java.util.*;

import org.apache.commons.lang3.*;

import com.griffin.core.*;

public class Cli {
    public static void usage() {
        System.out.println("error in command line paramiters");
        System.out.println("    usage: [server_info_filename] [target] [command...]");
        System.exit(1);
    }
    
    public static void main(String[] args) {
        if (args.length < 2) {
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
        
        System.out.println(command);
        
        try {
            Socket socket = new Socket(info.getHostName(), info.getPort());
            
            System.out.println(info);
            
            Communication nextComm = new Communication(socket);
            
            System.out.println("before send");
            
            nextComm.send(command);
            
            System.out.println("sent");
            
            Object ret;
            while (true) {
                ret = nextComm.receive();
                if (ret instanceof StopCommunication || ret == null) {
                    break;
                }
                
                System.out.println(ret.toString());
            }
            
            nextComm.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
