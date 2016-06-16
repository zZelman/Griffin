package com.griffin.core.task;

import java.util.*;

import com.griffin.core.*;

/**
    This is a dummy class (does not have any execution) because in order
    to close the server, it needs to be done on the main thread,
    not a client thread

    This class only exists to be apart of griffin's task list for availability
    of the command and info fields

    See the daemon class (where the server starts) for actual
    implementation of stoping the server
*/
public class StopServerTask extends Task {
    public StopServerTask() {
        super("stop server",
              "stops the server's execution (must be the only command)",
              "stop server: success",
              "stop server: failure");
    }
    
    public Output doAction(Communication prevComm) {
        Output output = new Output();
        
        output.addExecutionMessage("the server has not stopped");
        output.addExecutionMessage("\"stop server\" must be the only command");
        
        return output.addReturnMessage(this.failure);
    }
}
