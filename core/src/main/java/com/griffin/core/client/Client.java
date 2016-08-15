package com.griffin.core.client;

import java.net.*;
import java.io.*;
import java.lang.*;
import java.util.*;

import com.griffin.core.*;
import com.griffin.core.nameserver.*;
import com.griffin.core.server.*;

public class Client implements Startable {
    private final ClientCallBack callBack;
    private final ServerInfoParser infoParser;
    private final String target;
    private final Serializable command;
    
    public Client(ClientCallBack callBack, ServerInfoParser infoParser, String target, Serializable command) {
        this.callBack = callBack;
        this.infoParser = infoParser;
        this.target = target;
        this.command = command;
    }
    
    @Override
    public boolean start() {
        Communication nextComm = null;
        try {
            NameserverClient nameserverClient = new NameserverClient(infoParser.getNameserverInfo());
            ServerInfo info = nameserverClient.get(this.target);
            if (info == null) {
                this.callBack.dealWithBadTarget(this.target);
                return false;
            }
            
            nextComm = new Communication(info.getHostName(), info.getPort());
            nextComm.send(command);
            
            Object ret;
            while (!Thread.currentThread().isInterrupted()) {
                ret = nextComm.receive();
                if (ret instanceof StopCommunication || ret == null) {
                    break;
                }
                
                this.callBack.recieved(ret);
            }
        } catch (ServerInfoException e) {
            this.callBack.dealWith(e);
            return false;
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
