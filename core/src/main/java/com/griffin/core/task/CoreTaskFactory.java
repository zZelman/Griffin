package com.griffin.core.task;

import java.util.*;

import com.griffin.core.*;
import com.griffin.core.task.*;
import com.griffin.core.recurring.*;
import com.griffin.core.server.*;

public class CoreTaskFactory extends TaskFactory {
    private RecurringManager recurringManager;
    private ServerInfo nameserverInfo;
    private ServerInfo serverInfo;
    
    public CoreTaskFactory(RecurringManager recurringManager, ServerInfo nameserverInfo, ServerInfo serverInfo) {
        this.recurringManager = recurringManager;
        this.nameserverInfo = nameserverInfo;
        this.serverInfo = serverInfo;
    }
    
    public LoadedTasks getTasks() {
        LoadedTasks tasks = new LoadedTasks();
        
        // open ended
        tasks.addOpenEndedTask(new AddRecurringTask(this.recurringManager));
        tasks.addOpenEndedTask(new ShellTask());
        
        // parameterized
        tasks.addParameterizedTask(new RemoveRecurringTask(this.recurringManager));
        
        // simple
        tasks.addSimpleTask(new NameserverListTask(this.nameserverInfo));
        tasks.addSimpleTask(new NameserverPingTask(this.nameserverInfo, this.serverInfo));
        tasks.addSimpleTask(new ListRecurringTask(this.recurringManager));
        tasks.addSimpleTask(new StopServerTask());
        tasks.addSimpleTask(new PrintHelpTask(this.griffin));
        
        return tasks;
    }
}
