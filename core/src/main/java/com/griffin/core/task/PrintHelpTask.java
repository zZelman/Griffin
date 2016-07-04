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
        
        output.addMessage(this.griffin.getLoadedTasks().toString());

        return output.addReturnMessage(this.success);
    }
}
