package com.griffin.android.app;

import java.util.*;

import com.griffin.core.*;

public class ConcreteTaskFactory extends TaskFactory {
    public ConcreteTaskFactory() {
    }
    
    public LoadedTasks getTasks() {
        LoadedTasks tasks = new LoadedTasks();

        return tasks;
    }

}
