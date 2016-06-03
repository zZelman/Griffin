package com.griffin.desktop.cli;

import java.net.*;
import java.io.*;
import java.lang.*;

import com.griffin.core.*;

public class Main {
    public static void main(String[] args) {
        // get the server's info
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
            // connect to the server
            Socket socket = new Socket(info.getHostName(), info.getPort());
            Communication comm = new Communication(socket);
            
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            String userInput = stdIn.readLine();

            comm.send(userInput);
            Object ret = comm.receive();
            
            System.out.println(ret);
            
            comm.close();
            socket.close();
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
