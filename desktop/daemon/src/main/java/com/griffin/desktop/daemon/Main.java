package com.griffin.desktop.daemon;

import java.net.*;
import java.io.*;
import java.lang.*;

import com.griffin.core.*;

public class Main implements Runnable {
    private final Griffin griffin;
    private final Socket clientSocket;
    private final String BAD_COMMAND = "first communication must be in String form";
    
    public Main(Griffin griffin, Socket clientSocket) {
        this.griffin = griffin;
        this.clientSocket = clientSocket;
    }
    
    public void run() {
        try {
            Communication comm = new Communication(this.clientSocket);
            
            Object input;
            String command;
            String taskResult;
            
            input = comm.receive();
            if (input instanceof String) {
                command = (String) input;
                System.out.println("input: [" + command + "]");
                
                taskResult = this.griffin.doCommand(command, comm);
                comm.send(taskResult);
            } else {
                comm.send(BAD_COMMAND);
            }
            
            comm.send(new StopCommunication());
            
            comm.close();
            this.clientSocket.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    
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
        
        // setup a shared Griffin instance between all threads
        TaskFactory taskFactory = new ConcreteTaskFactory();
        Griffin griffin = new Griffin(taskFactory);
        griffin.debugPrintTasks();
        
        try {
            ServerSocket serverSocket = new ServerSocket(info.getPort());
            
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
