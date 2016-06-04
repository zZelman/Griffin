package com.griffin.core.task;

import java.util.*;

import com.griffin.core.*;

public class PrintHelpTask extends Task {
    private Griffin griffin;
    
    public PrintHelpTask(Output output, Griffin griffin) {
        super(output,
              "help",
              "prints all commands",
              "help: success",
              "help: failure");
              
        this.griffin = griffin;
    }
    
    public boolean canUse(String rawInput) {
        return rawInput.contains(this.getCommand());
    }
    
    public String doAction(Communication prevComm) {
        List<Task> tasks = this.griffin.getTasks();
        for (Task t : tasks) {
            this.output.addExecutionMessage(t.getCommand() + " - " + t.getInfo());
        }
        
        return this.success;
    }
}