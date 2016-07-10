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
                
                Output taskOutput = this.griffin.doCommand(command, prevComm);
                prevComm.send(taskOutput);
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
    
    public static void usage() {
        System.out.println("error in command line paramiters");
        System.out.println("    usage: [server_info_filename] [target]");
        System.exit(1);
    }
    
    public static void main(String[] args) {
        if (args.length < 2) {
            Main.usage();
        }

        ServerInfoParser infoParser = null;
        ServerInfo info = null;
        try {
            InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(args[0]);
            infoParser = new ServerInfoParser(inputStream);
            info = infoParser.getServerInfo(args[1]);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        
        // setup a shared Griffin instance between all threads
        TaskFactory taskFactory = new ConcreteTaskFactory(infoParser);
        Griffin griffin = new Griffin(taskFactory);
        String commandsAvailable = griffin.printTasks();
        
        System.out.println(info);
        System.out.println();
        System.out.println(commandsAvailable);
        
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
                        Output output = new Output();
                        output.setReturnMessage(Main.SERVER_STOPPING);
                        prevComm.send(output);
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
