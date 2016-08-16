package com.griffin.core.task;

import java.util.*;
import java.net.*;
import java.io.*;
import java.util.regex.*;
import java.util.prefs.*;

import com.griffin.core.*;
import com.griffin.core.output.*;
import com.griffin.core.task.*;
import com.griffin.core.server.*;
import com.griffin.core.client.*;

public class RunTask extends Task implements ClientCallBack {
    private ServerInfoParser infoParser;
    private String input;
    private Output output;
    
    public RunTask(ServerInfoParser infoParser) {
        super("run [[target [command...]] [target [command...]]...]",
              "runs the command on the target server NOTE: not recursive and server names are reserved in this context");
              
        this.infoParser = infoParser;
        this.resetRuntimeCommand();
    }
    
    @Override
    public String canUse(String rawInput) {
        String space = "( )";
        
        String run = "(run)";
        String input = "(.+)";
        
        Pattern p = Pattern.compile(run + space + input, Pattern.DOTALL);
        Matcher m = p.matcher(rawInput);
        
        if (m.find()) {
            this.input = m.group(3);
            
            this.setRuntimeCommand("run " + this.input);
            return rawInput.replaceFirst(run + space + input, "");
        }
        return null;
    }
    
    @Override
    public Output doAction(Communication prevComm) {
        this.output = new StartingOutput(this.getRuntimeCommand());
        
        String[] names = null;
        try {
            names = this.infoParser.getAllNames();
        } catch (BackingStoreException e) {
            this.output.addOutput(new ErrorOutput(e.toString()));
            this.output.addOutput(new FailureOutput(this.failure));
            return this.output;
        }
        String formatedNames = String.join("|", names);
        String[] seperatedCommands = input.split("(?=(" + formatedNames + "))");
        
        for (String cmd : seperatedCommands) {
            this.runCommand(cmd.trim());
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
        this.resetRuntimeCommand();
        this.input = "";
    }
    
    public void runCommand(String cmd) {
        String[] userInput = cmd.split(" ", 2);
        
        if (userInput.length != 2) {
            return;
        }
        
        String target = userInput[0];
        String command = userInput[1];

        Client client = new Client(this, this.infoParser, target, command);
        client.start();
        client.stop();
    }
    
    @Override
    public void recieved(Object o) {
        if (o instanceof Output) {
            Output out = (Output) o;
            this.output.addOutput(out);
        } else {
            this.output.addOutput(new StringOutput(o.toString()));
        }
    }
    
    @Override
    public void dealWith(ServerInfoException e) {
        this.exception(e);
    }
    
    @Override
    public void dealWith(ConnectException e) {
        this.exception(e);
    }
    
    @Override
    public void dealWith(UnknownHostException e) {
        this.exception(e);
    }
    
    @Override
    public void dealWith(ClassNotFoundException e) {
        this.exception(e);
    }
    
    @Override
    public void dealWith(IOException e) {
        this.exception(e);
    }
    
    @Override
    public void dealWithBadTarget(String target) {
        this.output.addOutput(new ErrorOutput("nameserver did not have an entery for: " + target));
    }
    
    private void exception(Exception e) {
        this.output.addOutput(new ErrorOutput(e.toString()));
    }
    
}
