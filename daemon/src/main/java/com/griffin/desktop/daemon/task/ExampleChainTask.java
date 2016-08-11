package com.griffin.desktop.daemon.task;

import java.util.*;
import java.net.*;
import java.io.*;

import com.griffin.core.*;
import com.griffin.core.output.*;

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
        Output output = new StartingOutput(this.command);
        
        // if (new Random().nextFloat() < 0.30f) {
        //     output.addExecutionMessage(this.command + ": last node");
        //     output.setReturnMessage(this.success);
        //     return output;
        // }
        
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

        Communication nextComm = null;
        try {
            // TODO: implement ClientCallBack instead of a custom Client
            
            nextComm = new Communication(info.getHostName(), info.getPort());
            
            Serializable command = "help";
            nextComm.send(command);
            
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
        } finally {
            try {
                if (nextComm != null) {
                    nextComm.close();
                }
            } catch (IOException e) {
                output.addOutput(new StringOutput(e.toString()));
                output.addOutput(new FailureOutput(this.failure));
                return output;
            }
        }
        
        output.addOutput(new SuccessOutput(this.success));
        return output;
    }
}
