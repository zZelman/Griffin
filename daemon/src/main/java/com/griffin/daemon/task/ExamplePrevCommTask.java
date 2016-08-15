package com.griffin.daemon.task;

import java.io.*;

import com.griffin.core.*;
import com.griffin.core.output.*;
import com.griffin.core.task.*;

public class ExamplePrevCommTask extends Task {
    public ExamplePrevCommTask() {
        super("prev comm",
              "(example) does previous communication",
              "prev comm: success",
              "prev comm: failure");
    }
    
    public Output doAction(Communication prevComm) {
        Output output = new StartingOutput(this.command);
        
        output.addOutput(new StringOutput("[ExamplePrevCommTask]"));
        
        try {
            prevComm.send("~~ communication from the actual Task");
        } catch (IOException e) {
            output.addOutput(new StringOutput(e.toString()));
            output.addOutput(new FailureOutput(this.failure));
            return output;
        }
        
        output.addOutput(new SuccessOutput(this.success));
        return output;
    }
}
