package com.griffin.desktop.daemon.task;

import java.util.*;
import java.net.*;
import java.io.*;

import com.griffin.core.*;

public class ExampleChainTask extends Task {
    private ServerInfoParser infoParser;
    
    public ExampleChainTask(ServerInfoParser infoParser) {
        super("chain",
              "executes the other command 'hello world'",
              "chain: success",
              "chain: failure");
              
        this.infoParser = infoParser;
    }
    
    public Output doAction(Communication prevComm) {
        Output output = new Output();
        
        if (new Random().nextFloat() < 0.50f) {
            output.addExecutionMessage(this.command + ": last node");
            return output.addReturnMessage(this.success);
        }

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
            
            Serializable command = "chain";
            nextComm.send(command);
            
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
            e.printStackTrace();
            System.exit(1);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        
        return output.addReturnMessage(this.success);
    }
}
