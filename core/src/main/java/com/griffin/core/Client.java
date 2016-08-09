package com.griffin.core;

import java.net.*;
import java.io.*;
import java.lang.*;
import java.util.*;

import com.griffin.core.*;

public class Client implements Startable {
    private final ClientCallBack callBack;
    private final ServerInfo info;
    private final Serializable command;
    
    public Client(ClientCallBack callBack, ServerInfo info, Serializable command) {
        this.callBack = callBack;
        this.info = info;
        this.command = command;
    }
    
    @Override
    public boolean start() {
        Socket socket = null;
        Communication nextComm = null;
        try {
            socket = new Socket(info.getHostName(), info.getPort());
            nextComm = new Communication(socket);
            
            nextComm.send(command);
            
            Object ret;
            while (!Thread.currentThread().isInterrupted()) {
                ret = nextComm.receive();
                if (ret instanceof StopCommunication || ret == null) {
                    break;
                }
                
                this.callBack.recieved(ret);
            }
        } catch (ConnectException e) {
            this.callBack.dealWith(e);
            return false;
        } catch (UnknownHostException e) {
            this.callBack.dealWith(e);
            return false;
        } catch (ClassNotFoundException e) {
            this.callBack.dealWith(e);
            return false;
        } catch (IOException e) {
            this.callBack.dealWith(e);
            return false;
        } finally {
            try {
                if (nextComm != null) {
                    nextComm.close();
                }
            } catch (IOException e) {
                this.callBack.dealWith(e);
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    public boolean stop() {
        return true;
    }
}
