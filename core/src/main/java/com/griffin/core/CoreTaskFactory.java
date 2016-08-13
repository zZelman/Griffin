package com.griffin.core;

import java.util.*;

import com.griffin.core.*;
import com.griffin.core.task.*;
import com.griffin.core.recurring.*;

public class CoreTaskFactory extends TaskFactory {
    private RecurringManager recurringManager;
    
    public CoreTaskFactory(RecurringManager recurringManager) {
        this.recurringManager = recurringManager;
    }
    
    public LoadedTasks getTasks() {
        LoadedTasks tasks = new LoadedTasks();
        
        // open ended
        tasks.addOpenEndedTask(new AddRecurringTask(this.recurringManager));
        tasks.addOpenEndedTask(new RemoveRecurringTask(this.recurringManager));
        tasks.addOpenEndedTask(new ListRecurringTask(this.recurringManager));
        
        // parameterized
        
        // simple
        tasks.addSimpleTask(new StopServerTask());
        tasks.addSimpleTask(new PrintHelpTask(this.griffin));
        
        return tasks;
    }
}
