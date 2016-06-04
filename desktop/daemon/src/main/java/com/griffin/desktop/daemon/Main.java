package com.griffin.desktop.daemon;

import java.net.*;
import java.io.*;
import java.lang.*;
import java.util.concurrent.atomic.*;

import com.griffin.core.*;

public class Main implements Runnable {
    private final Griffin griffin;
    private final Communication prevComm;
    private final Object firstInput;

    private final static String STOP_SERVER_COMMAND = "stop server";
    private final static String SERVER_STOPPING = "server has recieved the stop command, and is ending";
    private final String BAD_COMMAND = "first communication must be in String form";
    
    public Main(Griffin griffin, Communication prevComm, Object firstInput) {
        this.griffin = griffin;
        this.prevComm = prevComm;
        this.firstInput = firstInput;
    }
    
    public void run() {
        try {
            if (this.firstInput instanceof String) {
                String command = (String) this.firstInput;
                System.out.println("input: [" + command + "]");
                
                String taskResult = this.griffin.doCommand(command, prevComm);
                prevComm.send(taskResult);
            } else {
                prevComm.send(BAD_COMMAND);
            }
            
            prevComm.send(new StopCommunication());
            prevComm.close();
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
            
            Socket clientSocket;
            Communication prevComm;
            Object firstInput;
            String possibleStopCommand;
            while (true) {
                clientSocket = serverSocket.accept();
                prevComm = new Communication(clientSocket);

                firstInput = prevComm.receive();

                // always check the first communication for an instance of the stop command
                if (firstInput instanceof String) {
                    possibleStopCommand = (String) firstInput;
                    if (possibleStopCommand.equals(Main.STOP_SERVER_COMMAND)) {
                        System.out.println("input: [" + possibleStopCommand + "]");
                        prevComm.send(Main.SERVER_STOPPING);
                        prevComm.send(new StopCommunication());
                        break;
                    }
                }
                
                // the thread deals with prevComm closing
                new Thread(new Main(griffin, prevComm, firstInput)).start();
            }

            serverSocket.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        
        System.out.println(Main.SERVER_STOPPING);
    }
}
