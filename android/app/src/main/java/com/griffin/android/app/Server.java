package com.griffin.android.app;

import java.net.*;
import java.io.*;

import com.griffin.core.*;
import com.griffin.android.*;

public class Server implements Runnable {
    private ServerInfo info;
    private ServerCallBack callBack;
    private ServerSocket serverSocket;
    
    public Server(ServerInfo info, ServerCallBack callBack) throws IOException {
        this.info = info;
        this.callBack = callBack;
        
        this.serverSocket = new ServerSocket(info.getPort());
    }
    
    public ServerSocket getServerSocket() {
        return this.serverSocket;
    }
    
    @Override
    public void run() {
        try {
            Socket clientSocket;
            Communication prevComm;
            while (!Thread.currentThread().isInterrupted()) {
                clientSocket = serverSocket.accept();
                
                this.callBack.dealWith("connection");
                
                prevComm = new Communication(clientSocket);
                new Thread(new CommunicationThread(prevComm)).start();
            }
            
            serverSocket.close();
        } catch (IOException e) {
            this.callBack.dealWith(e);
        }
    }
    
    class CommunicationThread implements Runnable {
        private Communication prevComm;
        
        public CommunicationThread(Communication prevComm) {
            this.prevComm = prevComm;
        }

        @Override
        public void run() {
            try {
                prevComm.send("FROM ANDROID");
                prevComm.send(new StopCommunication());
            } catch (IOException e) {
                this.callBack.dealWith(e);
            }
        }
    }
}
