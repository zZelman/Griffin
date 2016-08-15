package com.griffin.core;

import java.util.*;

import com.griffin.core.*;
import com.griffin.core.task.*;
import com.griffin.core.output.*;
import com.griffin.core.recurring.*;
import com.griffin.core.server.*;

public class Griffin {
    private final String emptyRawInput = "the given input is empty: ";
    
    private LoadedTasks loadedTasks;
    private ServerCallBack serverCallBack;
    
    public Griffin(TaskFactory taskFactory, ServerCallBack serverCallBack, ServerInfo nameserverInfo, ServerInfo serverInfo) throws ServerInfoException {
        this.loadedTasks = new LoadedTasks();
        this.serverCallBack = serverCallBack;
        
        // NOTE: given before common to allow given to have priority over keywords in common (ie help)
        
        // given tasks
        if (taskFactory != null) {
            taskFactory.setGriffin(this);
            this.loadedTasks.addAll(taskFactory.getTasks());
        }
        
        // common tasks
        CoreTaskFactory core = new CoreTaskFactory(new RecurringManager(this, serverCallBack), nameserverInfo, serverInfo);
        core.setGriffin(this);
        this.loadedTasks.addAll(core.getTasks());
    }
    
    public String printTasks() {
        return this.loadedTasks.toString();
    }
    
    public LoadedTasks getLoadedTasks() {
        return this.loadedTasks;
    }
    
    public void setLoadedTasks(LoadedTasks loadedTasks) {
        this.loadedTasks = loadedTasks;
    }
    
    public Output doCommand(String rawInput, Communication comm) {
        Output output = new Output();
        
        if ("".equals(rawInput)) {
            output.addOutput(new ErrorOutput(emptyRawInput + "[" + rawInput + "]"));
            return output;
        }
        
        // print rawInput if not recurring (recurring takes care of its own printing)
        if (!comm.isHeadless()) {
            this.serverCallBack.commandRecieved(comm.getRemoteAddr(), comm.getLocalAddr(), rawInput);
        }
        
        boolean oneCommandExecuted;
        String canUseOutput;
        Output taskOutput;
        List<Task> tasks = this.loadedTasks.flatten();
        do {
            oneCommandExecuted = false;
            
            for (Task t : tasks) {
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
                    
                    // do the task
                    taskOutput = t.doAction(comm);
                    
                    // tell the task to remove any state that it created during execution
                    t.clear();
                    
                    // save the output of the task
                    output.addOutput(taskOutput);
                }
            }
        } while (oneCommandExecuted);
        
        // add to output if there is stuff not used
        if (!"".equals(rawInput)) {
            output.addOutput(new UnusedRawInputOutput(rawInput));
        }
        
        return output;
    }
}
