package com.griffin.desktop.daemon.task;

import java.util.*;
import java.net.*;
import java.io.*;
import java.util.regex.*;

import com.griffin.core.*;
import com.griffin.core.output.*;

public class ExamplePrefixTask extends Task {
    private ServerInfoParser infoParser;
    
    private String command;
    
    public ExamplePrefixTask(ServerInfoParser infoParser) {
        super("prefix [command...]",
              "(example) runs the [command...] and mutates the output",
              "prefix [command...]: success",
              "prefix [command...]: failure");
              
        this.infoParser = infoParser;
    }
    
    public String canUse(String rawInput) {
        String space = "( )";
        
        String prefix = "(prefix)";
        String command = "(.+)";
        
        Pattern p = Pattern.compile(prefix + space + command, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher m = p.matcher(rawInput);
        
        if (m.find()) {
            this.command = m.group(3);
            
            return rawInput.replaceFirst(prefix + space + command, "");
        }
        return null;
    }
    
    public Output doAction(Communication prevComm) {
        Output output = new StartingOutput(this.command);
        
        // only because this is an example task
        String targetName = "desktop";
        ServerInfo info = null;
        try {
            info = this.infoParser.getServerInfo(targetName);
        } catch (ServerInfoException e) {
            output.addOutput(new StringOutput(e.toString()));
            output.addOutput(new FailureOutput(this.failure));
            return output;
        }
        
        try {
            // TODO: implement ClientCallBack instead of a custom Client
            
            Socket socket = new Socket(info.getHostName(), info.getPort());
            Communication nextComm = new Communication(socket);
            
            nextComm.send(this.command);
            
            Object ret;
            while (true) {
                ret = nextComm.receive();
                if (ret instanceof StopCommunication || ret == null) {
                    break;
                }
                
                // guaranteed to be Output
                output.setSubtaskOutput((Output) ret);
            }
            
            nextComm.close();
        } catch (UnknownHostException e) {
            output.addOutput(new StringOutput(e.toString()));
            output.addOutput(new FailureOutput(this.failure));
            return output;
        } catch (ClassNotFoundException e) {
            output.addOutput(new StringOutput(e.toString()));
            output.addOutput(new FailureOutput(this.failure));
            return output;
        } catch (IOException e) {
            output.addOutput(new StringOutput(e.toString()));
            output.addOutput(new FailureOutput(this.failure));
            return output;
        } catch (SubtaskOutputException e) {
            output.addOutput(new StringOutput(e.toString()));
            output.addOutput(new FailureOutput(this.failure));
            return output;
        }
        
        output.addOutput(new SuccessOutput(this.success));
        return output;
    }
}
