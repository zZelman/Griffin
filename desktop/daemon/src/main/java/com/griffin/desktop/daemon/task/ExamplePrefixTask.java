package com.griffin.desktop.daemon.task;

import java.util.*;
import java.net.*;
import java.io.*;
import java.util.regex.*;

import com.griffin.core.*;

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
        Output output = new Output();

        // only because this is an example task
        String targetName = "desktop";
        ServerInfo info = null;
        try {
            info = this.infoParser.getServerInfo(targetName);
        } catch (URISyntaxException | IOException e) {
            output.addExecutionMessage(e.toString());
            return output.addReturnMessage(this.failure);
        } catch (Exception e) {
            output.addExecutionMessage(e.toString());
            return output.addReturnMessage(this.failure);
        }
        
        try {
            Socket socket = new Socket(info.getHostName(), info.getPort());
            Communication nextComm = new Communication(socket);
            
            nextComm.send(this.command);
            
            Object ret;
            while (true) {
                ret = nextComm.receive();
                if (ret instanceof StopCommunication) {
                    break;
                }
                
                // guaranteed to be string
                // do not use addExecutionMessage b/c the output is Output.getMessages which is already formatted
                output.addMessage((String) ret);
            }
            
            nextComm.close();
        } catch (UnknownHostException e) {
            output.addExecutionMessage(e.toString());
            return output.addReturnMessage(this.failure);
        } catch (ClassNotFoundException e) {
            output.addExecutionMessage(e.toString());
            return output.addReturnMessage(this.failure);
        } catch (IOException e) {
            output.addExecutionMessage(e.toString());
            return output.addReturnMessage(this.failure);
        }
        
        return output.addReturnMessage(this.success);
    }
}
