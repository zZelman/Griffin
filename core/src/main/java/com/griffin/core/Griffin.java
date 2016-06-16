package com.griffin.core;

import java.util.*;

import com.griffin.core.*;

public class Griffin {
    private final String noExistErrorMsg = "rawInput does not exist";
    private final String startCommandMsg = "running command: ";
    private final String endCommdMsg = " has ended";
    private final String commandStuffLeftOver = "there was parts of the command that were not used: ";
    
    private Output output;
    private List<Task> tasks;
    
    public Griffin(TaskFactory taskFactory) {
        this.output = new Output();
        
        // common tasks
        this.tasks = new ConcreteTaskFactory(this).getAll();
        
        // given tasks
        this.tasks.addAll(taskFactory.getAll());
    }
    
    public String printTasks() {
        StringBuffer sb = new StringBuffer();
        
        sb.append("Commands (in order):\n");
        for (Task t : this.tasks) {
            sb.append("    " + t.getCommand() + " - " + t.getInfo() + "\n");
        }
        
        return sb.toString();
    }
    
    public List<Task> getTasks() {
        return this.tasks;
    }
    
    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
    
    public String doCommand(String rawInput, Communication comm) {
        Output output = new Output();
        
        boolean oneCommandExecuted;
        String canUseOutput;
        Output taskOutput;
        do {
            oneCommandExecuted = false;
            
            for (Task t : this.tasks) {
                // effectivly remove parts of the rawInput successivly
                canUseOutput = t.canUse(rawInput);
                
                // a section of rawInput was removed (a task recognized it)
                if (canUseOutput != null) {
                    // this is what allows the same command to be executed multiple times
                    oneCommandExecuted = true;
                    
                    // remove the double space caused by removing a substring (if it exists)
                    canUseOutput = canUseOutput.replace("  ", " ").trim();
                    
                    // update rawInput because something has changed
                    rawInput = canUseOutput;
                    
                    // say what task you are about to start
                    output.addMessage(this.startCommandMsg + "\"" + t.getCommand() + "\"");
                    
                    taskOutput = t.doAction(comm);
                    
                    // say the return value of the task
                    output.addOutput(taskOutput);
                    taskOutput.clear();
                    
                    output.addMessage("\"" + t.getCommand() + "\"" + this.endCommdMsg);
                }
            }
        } while (oneCommandExecuted);
        
        // add to output if there is stuff not used
        if (rawInput != null && !rawInput.isEmpty()) {
            output.addMessage(this.commandStuffLeftOver + rawInput);
        }
        
        return output.getMessages();
    }
}
