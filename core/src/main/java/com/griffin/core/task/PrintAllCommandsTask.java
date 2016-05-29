package com.griffin.core.task;

import java.util.*;

import com.griffin.core.*;

public class PrintAllCommandsTask extends Task {
    private Griffin griffin;
    
    public PrintAllCommandsTask(Output output, Griffin griffin) {
        super(output,
              "print commands",
              "print commands: success",
              "print commands: failure");

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
