package com.griffin.desktop.daemon.task;

import java.io.*;

import com.griffin.core.*;

public class ExamplePrevCommTask extends Task {
    public ExamplePrevCommTask() {
        super("prev comm",
              "does previous communication",
              "prev comm: success",
              "prev comm: failure");
    }
    
    public Output doAction(Communication prevComm) {
        Output output = new Output();
        
        output.addExecutionMessage("[ExamplePrevCommTask::doAction] client communication");
        
        try {
            prevComm.send("~~ communication from the actual Task");
        } catch (IOException e) {
            output.addExecutionMessage("IOException trying to direct communication");
            e.printStackTrace();
        }
        
        return output.addReturnMessage(this.success);
    }
}
