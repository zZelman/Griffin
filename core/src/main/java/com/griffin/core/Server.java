package com.griffin.core;

import java.net.*;
import java.io.*;
import java.util.*;

import com.griffin.core.*;
import com.griffin.core.output.*;

public class Server implements Runnable {
    private final ServerCallBack callBack;
    private final ServerInfo info;
    private final ServerSocket serverSocket;
    private final Griffin griffin;
    
    private final String STOP_SERVER_COMMAND = "stop server";
    private final String SERVER_STOPPING = "server has recieved the stop command, and is ending";
    private final String BAD_COMMAND = "first communication must be in String form";
    
    public Server(ServerCallBack callBack, ServerInfo info, TaskFactory taskFactory) throws IOException {
        this.callBack = callBack;
        this.info = info;
        
        this.griffin = new Griffin(taskFactory);
        this.serverSocket = new ServerSocket(info.getPort());
        
        this.callBack.startedServerSocket(this.serverSocket);
    }
    
    @Override
    public void run() {
        this.callBack.serverInfo(this.info);
        this.callBack.taskList(this.griffin.printTasks());
        
        try {
            Socket clientSocket;
            Communication prevComm;
            Object firstInput;
            String possibleStopCommand;
            while (!Thread.currentThread().isInterrupted()) {
                clientSocket = this.serverSocket.accept();
                this.callBack.startedConnection();
                
                prevComm = new Communication(clientSocket);
                firstInput = prevComm.receive();
                
                // always check the first communication for an instance of the stop command
                if (firstInput instanceof String) {
                    possibleStopCommand = (String) firstInput;
                    if (possibleStopCommand.equals(STOP_SERVER_COMMAND)) {
                        prevComm.send(new StringOutput(SERVER_STOPPING));
                        prevComm.send(new StopCommunication());
                        break;
                    }
                }
                
                // the thread deals with prevComm closing
                // the thread deals with checking firstInput
                new Thread(new CommunicationThread(this.callBack, this.griffin, prevComm, firstInput)).start();
            }
        } catch (ClassNotFoundException e) {
            this.callBack.dealWith(e);
        } catch (IOException e) {
            this.callBack.dealWith(e);
        } finally {
            try {
                this.serverSocket.close();
            } catch (IOException e) {
                this.callBack.dealWith(e);
            }
        }
        
        this.callBack.serverEnding(SERVER_STOPPING);
    }
    
    class CommunicationThread implements Runnable {
        private final ServerCallBack callBack;
        private final Griffin griffin;
        private final Communication prevComm;
        private final Object firstInput;
        
        public CommunicationThread(ServerCallBack callBack, Griffin griffin, Communication prevComm, Object firstInput) {
            this.callBack = callBack;
            this.griffin = griffin;
            this.prevComm = prevComm;
            this.firstInput = firstInput;
        }
        
        @Override
        public void run() {
            try {
                if (this.firstInput instanceof String) {
                    String command = (String) this.firstInput;
                    this.callBack.commandRecieved(command);
                    
                    LinkedList<Output> outputs = this.griffin.doCommand(command, prevComm);
                    this.prevComm.send(outputs);
                } else {
                    this.prevComm.send(new ErrorOutput(BAD_COMMAND));
                }
                
                this.prevComm.send(new StopCommunication());
            } catch (IOException e) {
                this.callBack.dealWith(e);
            } finally {
                try {
                    this.prevComm.close();
                } catch (IOException e) {
                    this.callBack.dealWith(e);
                }
            }
        }
    }
}
