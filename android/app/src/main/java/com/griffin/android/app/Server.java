package com.griffin.android.app;

import java.net.*;
import java.io.*;

import com.griffin.core.*;
import com.griffin.android.*;

public class Server implements Runnable {
    private ServerInfo info;
    private ServerCallBack callBack;
    private ServerSocket serverSocket;
    private Griffin griffin;
    
    private final String STOP_SERVER_COMMAND = "stop server";
    private final String SERVER_STOPPING = "server has recieved the stop command, and is ending";
    private final String BAD_COMMAND = "first communication must be in String form";
    
    public Server(ServerInfo info, TaskFactory taskFactory, ServerCallBack callBack) throws IOException {
        this.info = info;
        this.callBack = callBack;
        
        this.griffin = new Griffin(taskFactory);
        this.serverSocket = new ServerSocket(info.getPort());
    }
    
    public ServerSocket getServerSocket() {
        return this.serverSocket;
    }
    
    @Override
    public void run() {
        this.callBack.dealWith(this.info.toString());
        this.callBack.dealWith(this.griffin.printTasks());
        
        try {
            Socket clientSocket;
            Communication prevComm;
            Object firstInput;
            String possibleStopCommand;
            while (!Thread.currentThread().isInterrupted()) {
                clientSocket = serverSocket.accept();
                this.callBack.dealWith("connection");
                
                prevComm = new Communication(clientSocket);
                firstInput = prevComm.receive();
                
                // always check the first communication for an instance of the stop command
                if (firstInput instanceof String) {
                    possibleStopCommand = (String) firstInput;
                    if (possibleStopCommand.equals(STOP_SERVER_COMMAND)) {
                        Output output = new Output();
                        output.setReturnMessage(SERVER_STOPPING);
                        prevComm.send(output);
                        prevComm.send(new StopCommunication());
                        break;
                    }
                }
                
                // the thread deals with prevComm closing
                // the thread deals with checking firstInput
                new Thread(new CommunicationThread(this.callBack, this.griffin, prevComm, firstInput)).start();
            }
            
            serverSocket.close();
        } catch (ClassNotFoundException e) {
            this.callBack.dealWith(e);
        } catch (IOException e) {
            this.callBack.dealWith(e);
        }
        
        this.callBack.dealWith(SERVER_STOPPING);
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
                    this.callBack.dealWith("input: [" + command + "]");
                    
                    Output taskOutput = this.griffin.doCommand(command, prevComm);
                    this.prevComm.send(taskOutput);
                } else {
                    this.prevComm.send(BAD_COMMAND);
                }
                
                this.prevComm.send(new StopCommunication());
                this.prevComm.close();
            } catch (IOException e) {
                this.callBack.dealWith(e);
            }
        }
    }
}
