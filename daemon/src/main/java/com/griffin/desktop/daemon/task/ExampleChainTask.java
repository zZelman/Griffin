package com.griffin.desktop.daemon.task;

import java.util.*;
import java.net.*;
import java.io.*;

import com.griffin.core.*;
import com.griffin.core.output.*;

public class ExampleChainTask extends Task implements ClientCallBack {
    private final ServerInfoParser infoParser;
    private final String target = "desktop";
    private final String nextCommand = "help";
    
    private Client client;
    private Output output;
    private boolean isSuccess;
    
    public ExampleChainTask(ServerInfoParser infoParser) {
        super("chain",
              "(example) executes the other command 'help' on target 'desktop'",
              "chain: success",
              "chain: failure");
              
        this.infoParser = infoParser;
        
        this.client = new Client(this, this.infoParser, this.target, this.nextCommand);
        this.output = new StartingOutput(this.command);
        this.isSuccess = false;
    }
    
    @Override
    public Output doAction(Communication prevComm) {
        this.isSuccess = true;
        
        this.client.start();
        this.client.stop();
        
        if (this.isSuccess) {
            this.output.addOutput(new SuccessOutput(this.success));
            return this.output;
        }
        
        this.output.addOutput(new FailureOutput(this.failure));
        return this.output;
    }
    
    @Override
    public void clear() {
        this.client = new Client(this, this.infoParser, this.target, this.command);
        this.output = new StartingOutput(this.command);
        this.isSuccess = false;
    }
    
    @Override
    public void recieved(Object o) {
        try {
            if (o instanceof Output) {
                this.output.setSubtaskOutput((Output) o);
            } else {
                // a catch-all for unexpected output (like "prev comm"'s string)
                this.output.addOutput(new StringOutput(o.toString()));
            }
        } catch (SubtaskOutputException e) {
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
        this.output.addOutput(new StringOutput("nameserver did not have an entery for: " + target));
        this.isSuccess = false;
    }
    
    private void helper(Exception e) {
        this.output.addOutput(new StringOutput(e.toString()));
        this.isSuccess = false;
    }
}
