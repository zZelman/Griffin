package com.griffin.desktop.daemon;

import java.net.*;
import java.io.*;
import java.lang.*;

import com.griffin.core.*;

import java.lang.*;

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
            ServerSocket serverSocket = new ServerSocket(info.getPort());
            Socket clientSocket = serverSocket.accept();
            TextCommunication comm = new TextCommunication(clientSocket);
            
            String inputLine;
            while ((inputLine = comm.receive()) != null) {
                comm.send(inputLine);
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                               + info.getPort() + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}
