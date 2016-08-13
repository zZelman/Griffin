package com.griffin.core.task;

import java.io.*;
import java.net.*;

import com.griffin.core.*;
import com.griffin.core.output.*;
import com.griffin.core.nameserver.*;

public class NameserverPingTask extends Task {
    private NameserverClient client;
    private ServerInfo serverInfo;
    
    public NameserverPingTask(ServerInfo nameserverInfo, ServerInfo serverInfo) {
        super("nameserver ping",
              "tells this server to issue a ping to the nameserver",
              "nameserver ping: success",
              "nameserver ping: failure");
              
        this.client = new NameserverClient(nameserverInfo);
        this.serverInfo = serverInfo;
    }
    
    @Override
    public Output doAction(Communication prevComm) {
        Output output = new StartingOutput(this.command);
        
        try {
            this.client.ping(this.serverInfo);
        } catch (UnknownHostException e) {
            output.addOutput(new ErrorOutput(e.toString()));
        } catch (IOException e) {
            output.addOutput(new ErrorOutput(e.toString()));
        }
        
        if (output.containsError()) {
            output.addOutput(new FailureOutput(this.failure));
            return output;
        }
        
        output.addOutput(new SuccessOutput(this.success));
        return output;
    }
}
