package com.griffin.core.task;

import java.util.*;

import com.griffin.core.*;

public class PrintHelpTask extends Task {
    private Griffin griffin;
    
    public PrintHelpTask(Output output, Griffin griffin) {
        super(output,
              "help",
              "help: success",
              "help: failure");

        this.griffin = griffin;
    }
    
    public String doAction() {
        List<Task> tasks = this.griffin.getTasks();
        for (Task t : tasks) {
            this.output.addExecutionMessage(t.getCommand());
        }

        return this.success;
    }
}
