package com.griffin.core.task;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

import com.griffin.core.*;
import com.griffin.core.output.*;
import com.griffin.core.nameserver.*;
import com.griffin.core.server.*;

public class ListNameserverTask extends Task {
    private NameserverClient client;
    
    public ListNameserverTask(ServerInfo nameserverInfo) {
        super("list nameserver",
              "prints what the nameserver knows about servers");
              
        this.client = new NameserverClient(nameserverInfo);
    }
    
    @Override
    public Output doAction(Communication prevComm) {
        Output output = new StartingOutput(this.getRuntimeCommand());
        
        ConcurrentLinkedQueue<ServerInfo> infos = null;
        try {
            infos = this.client.list();
        } catch (UnknownHostException e) {
            output.addOutput(new ErrorOutput(e.toString()));
        } catch (ClassNotFoundException e) {
            output.addOutput(new ErrorOutput(e.toString()));
        } catch (IOException e) {
            output.addOutput(new ErrorOutput(e.toString()));
        }
        
        if (output.containsError()) {
            output.addOutput(new FailureOutput(this.failure));
            return output;
        }
        
        if (infos.isEmpty()) {
            output.addOutput(new StringOutput("no server infos avalable"));
        }
        
        for (ServerInfo info : infos) {
            output.addOutput(new StringOutput("name: " + info.getName() + ", hostName: " + info.getHostName() + ", port: " + info.getPort()));
        }
        
        output.addOutput(new SuccessOutput(this.success));
        return output;
    }
}
