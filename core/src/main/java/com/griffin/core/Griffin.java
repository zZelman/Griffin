package com.griffin.core;

import java.util.*;

import com.griffin.core.task.*;

public class Griffin {
    private final String noExistErrorMsg;
    private final String startingMsg;
    private final String endingMsg;
    
    private List<Task> tasks;
    
    public Griffin() {
        this.noExistErrorMsg = "command does not exist. it was: ";
        this.startingMsg = "starting message";
        this.endingMsg = "ending message";
        
        this.tasks = new TaskFactory().getAll();
    }
    
    public boolean doesCommandExist(String command) {
        for (Task t : this.tasks) {
            if (command.contains(t.getCommand())) {
                return true;
            }
        }
        return false;
    }
    
    public String getNoExistErrorMsg() {
        return this.noExistErrorMsg;
    }
    
    public String getStartingMsg() {
        return this.startingMsg;
    }
    
    public String doTask(String command) {
        for (Task t : this.tasks) {
            if (command.contains(t.getCommand())) {
                return t.doAction();
            }
        }
        return null;
    }
    
    public String getEndingMsg() {
        return this.endingMsg;
    }
}
