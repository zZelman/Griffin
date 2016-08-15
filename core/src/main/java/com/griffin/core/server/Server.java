package com.griffin.core.server;

import java.net.*;
import java.io.*;
import java.util.*;

import com.griffin.core.*;
import com.griffin.core.output.*;
import com.griffin.core.nameserver.*;
import com.griffin.core.task.*;

public class Server implements Runnable {
    private ServerCallBack serverCallBack;
    private ServerInfo serverInfo;

    private NameserverPinger nameserverPinger;
    private Griffin griffin;
    private ServerSocket serverSocket;
    
    private final String STOP_SERVER_COMMAND = "stop server";
    private final String SERVER_STOPPING = "server has recieved the stop command, and is ending";
    private final String BAD_COMMAND = "first communication must be in String form";
    
    public Server(ServerCallBack serverCallBack, NameserverCallBack nameserverCallBack,
                  ServerInfoParser infoParser, String target,
                  TaskFactory taskFactory) throws ServerInfoException, IOException {
        this.serverCallBack = serverCallBack;
        this.serverInfo = infoParser.getServerInfo(target);

        ServerInfo nameserverInfo = infoParser.getNameserverInfo();
        
        this.nameserverPinger = new NameserverPinger(nameserverCallBack,
                nameserverInfo,
                this.serverInfo);
        
        this.griffin = new Griffin(taskFactory, nameserverInfo, this.serverInfo);
        
        this.serverSocket = new ServerSocket(this.serverInfo.getPort());
        this.serverCallBack.startedServerSocket(this.serverSocket);
    }
    
    @Override
    public void run() {
        this.serverCallBack.serverInfo(this.serverInfo);
        this.serverCallBack.taskList(this.griffin.printTasks());

        this.nameserverPinger.start();
        
        try {
            Socket clientSocket;
            Communication prevComm;
            Object firstInput;
            String possibleStopCommand;
            while (!Thread.currentThread().isInterrupted()) {
                clientSocket = this.serverSocket.accept();
                
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
                new Thread(new CommunicationThread(this.serverCallBack, this.griffin, prevComm, firstInput)).start();
            }
        } catch (ClassNotFoundException e) {
            this.serverCallBack.dealWith(e);
        } catch (IOException e) {
            this.serverCallBack.dealWith(e);
        } finally {
            try {
                this.serverSocket.close();
            } catch (IOException e) {
                this.serverCallBack.dealWith(e);
            }
        }

        this.nameserverPinger.stop();
        
        this.serverCallBack.serverEnding(SERVER_STOPPING);
    }
    
    private class CommunicationThread implements Runnable {
        private final ServerCallBack serverCallBack;
        private final Griffin griffin;
        private final Communication prevComm;
        private final Object firstInput;
        
        public CommunicationThread(ServerCallBack serverCallBack, Griffin griffin, Communication prevComm, Object firstInput) {
            this.serverCallBack = serverCallBack;
            this.griffin = griffin;
            this.prevComm = prevComm;
            this.firstInput = firstInput;
        }
        
        @Override
        public void run() {
            try {
                if (this.firstInput instanceof String) {
                    String command = (String) this.firstInput;
                    this.serverCallBack.commandRecieved(this.prevComm.getRemoteAddr(), this.prevComm.getLocalAddr(), command);
                    
                    Output output = this.griffin.doCommand(command, prevComm);
                    this.prevComm.send(output);
                } else {
                    this.prevComm.send(new ErrorOutput(BAD_COMMAND));
                }
                
                this.prevComm.send(new StopCommunication());
            } catch (IOException e) {
                this.serverCallBack.dealWith(e);
            } finally {
                try {
                    this.prevComm.close();
                } catch (IOException e) {
                    this.serverCallBack.dealWith(e);
                }
            }
        }
    }
}
