package com.griffin.core;

import java.util.*;

import com.griffin.core.*;

public class Griffin {
    private final String noExistErrorMsg = "command does not exist";
    private final String startingMsg = "starting message";
    private final String commandStartMsg = "starting command: ";
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
    
    public void debugPrintTasks() {
        System.out.println("Commands:");
        for (Task t : this.tasks) {
            System.out.println("    " + t.getCommand() + " - " + t.getInfo());
        }
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
    
    public String doCommand(String command, Communication comm) {
        // say command parsing  is about to start
        this.output.addMessage(this.startingMsg);
        this.output.addDelimiter();
        
        String taskOutput;
        for (Task t : this.tasks) {
            if (t.canUse(command)) {
                // say what task you are about to start
                this.output.addMessage(this.commandStartMsg + t.getCommand());
                
                taskOutput = t.doAction(comm);
                
                // say the return value of the task
                this.output.addMessage(taskOutput);
                this.output.addDelimiter();
            }
        }
        
        // say all tasks have been completed
        this.output.addMessage(this.endingMsg);
        
        // output needs to be cleared after to comply with thread-safty
        String ret = this.output.getMessages();
        this.output.clear();
        
        return ret;
    }
}
