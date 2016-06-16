package com.griffin.desktop.cli;

import java.net.*;
import java.io.*;
import java.lang.*;

import org.apache.commons.lang3.*;

import com.griffin.core.*;

public class Main {
    public static void usage() {
        System.out.println("error in command line paramiters");
        System.out.println("    usage: [server_info_filename] [target] [command...]");
        System.exit(1);
    }
    
    public static void main(String[] args) {
        if (args.length < 2) {
            Main.usage();
        }
        
        ServerInfoParser infoParser = new ServerInfoParser(args[0]);
        ServerInfo info = null;
        try {
            info = infoParser.getServerInfo(args[1]);
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        
        try {
            Socket socket = new Socket(info.getHostName(), info.getPort());
            Communication nextComm = new Communication(socket);
            
            String[] command = ArrayUtils.subarray(args, 2, args.length);
            Serializable userInput = StringUtils.join(command, " ");
            nextComm.send(userInput);
            
            Object ret;
            while (true) {
                ret = nextComm.receive();
                if (ret instanceof StopCommunication) {
                    break;
                }
                
                System.out.println(ret);
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
