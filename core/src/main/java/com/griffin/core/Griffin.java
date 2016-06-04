package com.griffin.core;

import java.util.*;

import com.griffin.core.*;

public class Griffin {
    private final String noExistErrorMsg = "rawInput does not exist";
    private final String startingMsg = "starting message";
    private final String startCommandMsg = "starting command: ";
    private final String commandStuffLeftOver = "there was parts of the command that were not used: ";
    private final String endingMsg = "ending message";
    
    private Output output;
    private List<Task> tasks;
    
    public Griffin(TaskFactory taskFactory) {
        this.output = new Output();
        
        // common tasks
        this.tasks = new ConcreteTaskFactory(this).getAll(output);
        
        // given tasks
        this.tasks.addAll(taskFactory.getAll(output));
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
    
    public Output getOutput() {
        return this.output;
    }
    
    public String doCommand(String rawInput, Communication comm) {
        // say rawInput parsing is about to start
        this.output.addMessage(this.startingMsg);
        this.output.addDelimiter();
        
        String canUseOutput;
        String taskOutput;
        for (Task t : this.tasks) {
            // effectivly remove sections of the rawInput successivly
            canUseOutput = t.canUse(rawInput);
            
            // a section of rawInput was removed (a task recognized it)
            if (canUseOutput != null) {
                // remove the double space caused by removing a substring (if it exists)
                canUseOutput = canUseOutput.replace("  ", " ").trim();
                
                // update rawInput because something has changed
                rawInput = canUseOutput;
                
                // say what task you are about to start
                this.output.addMessage(this.startCommandMsg + t.getCommand());
                
                taskOutput = t.doAction(comm);
                
                // say the return value of the task
                this.output.addMessage(taskOutput);
                this.output.addDelimiter();
            }
        }

        // add to output if there is stuff not used
        if (rawInput != null && !rawInput.isEmpty()) {
            this.output.addMessage(this.commandStuffLeftOver + rawInput);
        }
        
        // say all tasks have been completed
        this.output.addMessage(this.endingMsg);
        
        // output needs to be cleared after to comply with thread-safty
        String ret = this.output.getMessages();
        this.output.clear();
        
        return ret;
    }
}
