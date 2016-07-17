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
        
        LoadedTasks loadedTasks = this.griffin.getLoadedTasks();
        
        output.addExecutionMessage("Commands (in order):");
        if (!loadedTasks.getOpenEndedTasks().isEmpty()) {
            this.helper(output, "Open ended", loadedTasks.getOpenEndedTasks());
        }
        
        if (!loadedTasks.getParameterizedTasks().isEmpty()) {
            this.helper(output, "Parameterized", loadedTasks.getParameterizedTasks());
        }
        
        if (!loadedTasks.getSimpleTasks().isEmpty()) {
            this.helper(output, "Simple", loadedTasks.getSimpleTasks());
        }
        
        output.setReturnMessage(this.success);
        return output;
    }
    
    private void helper(Output output, String section, List<Task> tasks) {
        output.addExecutionMessage("    " + section);
        for (Task t : tasks) {
            output.addExecutionMessage("        " + t.getCommand() + " - " + t.getInfo());
        }
    }
}
