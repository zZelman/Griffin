package com.griffin.core;

import java.util.*;

import com.griffin.core.*;
import com.griffin.core.task.*;
import com.griffin.core.recurring.*;

public class CoreTaskFactory extends TaskFactory {
    private ServerInfo nameserverInfo;
    private RecurringManager recurringManager;
    
    public CoreTaskFactory(ServerInfo nameserverInfo, RecurringManager recurringManager) {
        this.nameserverInfo = nameserverInfo;
        this.recurringManager = recurringManager;
    }
    
    public LoadedTasks getTasks() {
        LoadedTasks tasks = new LoadedTasks();
        
        // open ended
        tasks.addOpenEndedTask(new AddRecurringTask(this.recurringManager));
        
        // parameterized
        tasks.addParameterizedTask(new RemoveRecurringTask(this.recurringManager));
        
        // simple
        tasks.addSimpleTask(new NameserverListTask(this.nameserverInfo));
        tasks.addSimpleTask(new ListRecurringTask(this.recurringManager));
        tasks.addSimpleTask(new StopServerTask());
        tasks.addSimpleTask(new PrintHelpTask(this.griffin));
        
        return tasks;
    }
}
