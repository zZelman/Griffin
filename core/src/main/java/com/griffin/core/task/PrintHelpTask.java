package com.griffin.core.task;

import java.util.*;

import com.griffin.core.*;

public class PrintHelpTask extends Task {
    private Griffin griffin;
    
    public PrintHelpTask(Griffin griffin) {
        super("help",
              "prints all commands and their descriptions",
              "help: success",
              "help: failure");
              
        this.griffin = griffin;
    }
    
    public Output doAction(Communication prevComm) {
        Output output = new Output();
        
        List<Task> tasks = this.griffin.getTasks();

        output.addExecutionMessage("Commands (in order):");
        for (Task t : tasks) {
            output.addExecutionMessage("    " + t.getCommand() + " - " + t.getInfo());
        }

        return output.addReturnMessage(this.success);
    }
}
