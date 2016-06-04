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
    public StopServerTask(Output output) {
        super(output,
              "stop server",
              "stops the server's execution (must be the only command)",
              "stop server: success",
              "stop server: failure");
    }
    
    public String doAction(Communication prevComm) {
        return this.success;
    }
}
