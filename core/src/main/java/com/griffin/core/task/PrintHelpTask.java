package com.griffin.core.task;

import java.util.*;

import com.griffin.core.*;
import com.griffin.core.output.*;

public class PrintHelpTask extends Task {
    private Griffin griffin;
    
    public PrintHelpTask(Griffin griffin) {
        super("help",
              "prints all commands and their descriptions");
              
        this.griffin = griffin;
    }

    @Override
    public Output doAction(Communication prevComm) {
        Output output = new StartingOutput(this.getRuntimeCommand());
        
        LoadedTasks loadedTasks = this.griffin.getLoadedTasks();
        
        output.addOutput(new FormatedStringOutput("Commands (in order):"));
        if (!loadedTasks.getOpenEndedTasks().isEmpty()) {
            this.helper(output, "Open ended", loadedTasks.getOpenEndedTasks());
        }
        
        if (!loadedTasks.getParameterizedTasks().isEmpty()) {
            this.helper(output, "Parameterized", loadedTasks.getParameterizedTasks());
        }
        
        if (!loadedTasks.getSimpleTasks().isEmpty()) {
            this.helper(output, "Simple", loadedTasks.getSimpleTasks());
        }
        
        output.addOutput(new SuccessOutput(this.success));
        return output;
    }
    
    private void helper(Output output, String section, List<Task> tasks) {
        output.addOutput(new FormatedStringOutput("    " + section));
        for (Task t : tasks) {
            output.addOutput(new FormatedStringOutput("        " + t.getCommand() + " - " + t.getDescription()));
        }
    }
}
