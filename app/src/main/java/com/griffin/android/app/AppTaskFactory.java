package com.griffin.android.app;

import java.util.*;

import com.griffin.core.*;

public class AppTaskFactory extends TaskFactory {
    public AppTaskFactory() {
    }
    
    public LoadedTasks getTasks() {
        LoadedTasks tasks = new LoadedTasks();

        return tasks;
    }
}
