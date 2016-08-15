package com.griffin.daemon.task;

import java.util.*;
import java.net.*;
import java.io.*;

import com.griffin.core.*;
import com.griffin.core.output.*;
import com.griffin.core.client.*;
import com.griffin.core.server.*;
import com.griffin.core.task.*;

public class ExampleChainTask extends Task implements ClientCallBack {
    private ServerInfoParser infoParser;
    
    private final String target = "desktop";
    private final String nextCommand = "chain";
    
    private Output output;
    private Communication prevComm;
    
    public ExampleChainTask(ServerInfoParser infoParser) {
        super("chain",
              "(example) executes the other command 'chain' on target 'desktop' 70% of the time",
              "chain: success",
              "chain: failure");
              
        this.infoParser = infoParser;
        this.output = new StartingOutput(this.command);
        this.prevComm = null;
    }
    
    @Override
    public Output doAction(Communication prevComm) {
        if (new Random().nextFloat() < 0.30f) {
            this.output.addOutput(new StringOutput("last node"));
        } else {
            Client client = new Client(this, this.infoParser, this.target, this.nextCommand);
            
            this.prevComm = prevComm;
            
            client.start();
            client.stop();
        }
        
        if (this.output.containsError()) {
            this.output.addOutput(new FailureOutput(this.failure));
        } else {
            this.output.addOutput(new SuccessOutput(this.success));
        }
        
        return this.output;
    }
    
    @Override
    public void clear() {
        this.output = new StartingOutput(this.command);
        this.prevComm = null;
    }
    
    @Override
    public void recieved(Object o) {
        try {
            if (o instanceof Output) {
                this.output.setSubtaskOutput((Output) o);
            } else if (o instanceof Serializable) {
                this.prevComm.send((Serializable) o);
            } else {
                this.output.addOutput(new StringOutput(o.toString()));
            }
        } catch (SubtaskOutputException e) {
            this.helper(e);
        } catch (IOException e) {
            this.helper(e);
        }
    }
    
    @Override
    public void dealWith(ServerInfoException e) {
        this.helper(e);
    }
    
    @Override
    public void dealWith(ConnectException e) {
        this.helper(e);
    }
    
    @Override
    public void dealWith(UnknownHostException e) {
        this.helper(e);
    }
    
    @Override
    public void dealWith(ClassNotFoundException e) {
        this.helper(e);
    }
    
    @Override
    public void dealWith(IOException e) {
        this.helper(e);
    }
    
    @Override
    public void dealWithBadTarget(String target) {
        this.output.addOutput(new ErrorOutput("nameserver did not have an entery for: " + target));
    }
    
    private void helper(Exception e) {
        this.output.addOutput(new ErrorOutput(e.toString()));
    }
}
