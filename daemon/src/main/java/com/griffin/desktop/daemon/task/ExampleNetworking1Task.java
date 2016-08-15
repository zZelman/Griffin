package com.griffin.desktop.daemon.task;

import java.util.*;
import java.net.*;
import java.io.*;

import com.griffin.core.*;
import com.griffin.core.output.*;
import com.griffin.core.client.*;
import com.griffin.core.server.*;
import com.griffin.core.task.*;

public class ExampleNetworking1Task extends Task implements ClientCallBack {
    private ServerInfoParser infoParser;
    
    private final String target = "desktop";
    private final String nextCommand = "net2";
    
    private Output output;
    // // DO NOT REMOVE THESE COMMENTS they show a very cool feature of prevComm<--nextComm data transfer
    // // (you can un-comment them to see though)
    // private Communication prevComm;
    
    public ExampleNetworking1Task(ServerInfoParser infoParser) {
        super("net1",
              "(example) executes the other command 'net2' on target 'desktop'",
              "net1: success",
              "net1: failure");
              
        this.infoParser = infoParser;
        this.output = new StartingOutput(this.command);
        // this.prevComm = null;
    }
    
    @Override
    public Output doAction(Communication prevComm) {
        Client client = new Client(this, this.infoParser, this.target, this.nextCommand);
        
        // this.prevComm = prevComm;
        
        client.start();
        client.stop();
        
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
        // this.prevComm = null;
    }
    
    @Override
    public void recieved(Object o) {
        try {
            if (o instanceof Output) {
                this.output.setSubtaskOutput((Output) o);
            }
            // // Uncommenting this and communication from the next goes through to the prev
            // // right now the recieved STOPs here
            // else if (o instanceof Serializable) {
            //     this.prevComm.send((Serializable) o);
            // }
            else {
                this.output.addOutput(new StringOutput(o.toString()));
            }
        } catch (SubtaskOutputException e) {
            this.helper(e);
        }
        // catch (IOException e) {
        //     this.helper(e);
        // }
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
