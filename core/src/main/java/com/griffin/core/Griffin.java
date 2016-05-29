package com.griffin.core;

import java.util.*;

import com.griffin.core.*;
import com.griffin.core.task.*;

public class Griffin {
    private final String noExistErrorMsg;
    private final String startingMsg;
    private final String endingMsg;
    
    private Output output;
    private List<Task> tasks;
    
    public Griffin() {
        this.noExistErrorMsg = "command does not exist";
        this.startingMsg = "starting message";
        this.endingMsg = "ending message";
        
        this.output = new Output();
        this.tasks = new TaskFactory().getAll();
    }
    
    public String doCommand(String command) {
        // check for command
        boolean exists = this.doesCommandExist(command);
        if (!exists) {
            this.output.addMessage(this.noExistErrorMsg);
            this.output.addMessage(command);
            return this.output.getMessages();
        }
        
        // say the command is about to start
        String startingTaskMsg = this.getStartingMsg();
        this.output.addMessage(startingTaskMsg);
        
        // execute the task
        String returnMsg = this.doTask(command);
        
        // say the return value of the task
        this.output.addMessage(returnMsg);
        
        // say all tasks have been completed
        String endingMsg = this.getEndingMsg();
        this.output.addMessage(endingMsg);

        return this.output.getMessages();
    }
    
    private boolean doesCommandExist(String command) {
        for (Task t : this.tasks) {
            if (command.contains(t.getCommand())) {
                return true;
            }
        }
        return false;
    }
    
    private String getNoExistErrorMsg() {
        return this.noExistErrorMsg;
    }
    
    private String getStartingMsg() {
        return this.startingMsg;
    }
    
    private String doTask(String command) {
        for (Task t : this.tasks) {
            if (command.contains(t.getCommand())) {
                return t.doAction();
            }
        }
        return null;
    }
    
    private String getEndingMsg() {
        return this.endingMsg;
    }
}
