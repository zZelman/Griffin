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
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        
        String[] commandTokens = ArrayUtils.subarray(args, 2, args.length);
        Serializable command = StringUtils.join(commandTokens, " ");
        
        try {
            Socket socket = new Socket(info.getHostName(), info.getPort());
            Communication nextComm = new Communication(socket);
            
            nextComm.send(command);
            
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
