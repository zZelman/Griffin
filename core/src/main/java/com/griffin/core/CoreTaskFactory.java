package com.griffin.core;

import java.util.*;

import com.griffin.core.*;
import com.griffin.core.task.*;

public class CoreTaskFactory extends TaskFactory {
    public LoadedTasks getTasks() {
        LoadedTasks tasks = new LoadedTasks();

        // parameterized
        // NOTE: it might not be a good idea to have
        //       parameterized tasks in the common domain

        // simple
        tasks.addSimpleTask(new StopServerTask());
        tasks.addSimpleTask(new PrintHelpTask(this.griffin));
        
        return tasks;
    }
}
