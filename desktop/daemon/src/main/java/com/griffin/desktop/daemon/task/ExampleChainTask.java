package com.griffin.desktop.daemon.task;

import java.util.*;
import java.net.*;
import java.io.*;

import com.griffin.core.*;

public class ExampleChainTask extends Task {
    private ServerInfoParser infoParser;
    
    public ExampleChainTask(ServerInfoParser infoParser) {
        super("chain",
              "(example) executes the other command 'chain' 70/30 yes/no",
              "chain: success",
              "chain: failure");
              
        this.infoParser = infoParser;
    }
    
    public Output doAction(Communication prevComm) {
        Output output = new Output();
        
        if (new Random().nextFloat() < 0.30f) {
            output.addExecutionMessage(this.command + ": last node");
            output.setReturnMessage(this.success);
            return output;
        }

        // only because this is an example task
        String targetName = "desktop";
        ServerInfo info = null;
        try {
            info = this.infoParser.getServerInfo(targetName);
        } catch (URISyntaxException | IOException e) {
            output.addExecutionMessage(e.toString());
            output.setReturnMessage(this.failure);
            return output;
        } catch (Exception e) {
            output.addExecutionMessage(e.toString());
            output.setReturnMessage(this.failure);
            return output;
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
                
                // guaranteed to be Output
                output.addOutput((Output) ret);
            }
            
            nextComm.close();
        } catch (UnknownHostException e) {
            output.addExecutionMessage(e.toString());
            output.setReturnMessage(this.failure);
            return output;
        } catch (ClassNotFoundException e) {
            output.addExecutionMessage(e.toString());
            output.setReturnMessage(this.failure);
            return output;
        } catch (IOException e) {
            output.addExecutionMessage(e.toString());
            output.setReturnMessage(this.failure);
            return output;
        }

        output.setReturnMessage(this.success);
        return output;
    }
}
