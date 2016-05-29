package com.griffin.desktop.cli;

import java.net.*;
import java.io.*;
import java.lang.*;

import com.griffin.core.*;

public class Main {
    public static void main(String[] args) {
        ServerInfoParser parser = new ServerInfoParser("server_list.ini");
        ServerInfo info = null;
        
        try {
            info = parser.getServerInfo("desktop");
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        
        try {
            Socket socket = new Socket(info.getHostName(), info.getPort());
            TextCommunication comm = new TextCommunication(socket);
            
            String userInput;
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            while ((userInput = stdIn.readLine()) != null) {
                comm.send(userInput);
                System.out.println("echo: " + comm.receive());
            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + info.getHostName());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                               info.getHostName());
            System.exit(1);
        }
    }
}
