package com.griffin.desktop.daemon;

import java.net.*;
import java.io.*;
import java.lang.*;

import com.griffin.core.*;

import java.lang.*;

public class Main implements Runnable {
    private final Griffin griffin;
    private final Socket clientSocket;
    
    public Main(Griffin griffin, Socket clientSocket) {
        this.griffin = griffin;
        this.clientSocket = clientSocket;
    }
    
    public void run() {
        try {
            TextCommunication comm = new TextCommunication(this.clientSocket);
            
            String input;
            String taskResult;
            
            while ((input = comm.receive()) != null) {
                System.out.println("input: [" + input + "]");
                
                taskResult = this.griffin.doCommand(input);
                comm.send(taskResult);
            }

            this.clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    
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
        
        // setup a shared Griffin instance between all threads
        TaskFactory taskFactory = new ConcreteTaskFactory();
        Griffin griffin = new Griffin(taskFactory);
        griffin.debugPrintTasks();
        
        try {
            // wait for a client
            ServerSocket serverSocket = new ServerSocket(info.getPort());
            
            // spawn a new thread with the unique socket and the shared griffin
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new Main(griffin, clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
