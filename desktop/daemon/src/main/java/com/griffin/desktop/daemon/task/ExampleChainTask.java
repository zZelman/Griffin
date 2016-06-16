package com.griffin.desktop.daemon.task;

import java.util.*;
import java.net.*;
import java.io.*;

import com.griffin.core.*;

public class ExampleChainTask extends Task {
    private String word1;
    private String word2;
    
    public ExampleChainTask() {
        super("chain",
              "executes the other command 'hello world'",
              "chain: success",
              "chain: failure");
    }
    
    public Output doAction(Communication prevComm) {
        Output output = new Output();
        
        String hostname = "localhost";
        int port = 6000;
        
        try {
            Socket socket = new Socket(hostname, port);
            Communication nextComm = new Communication(socket);
            
            Serializable command = "hello world";
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
