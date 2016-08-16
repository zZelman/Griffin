package com.griffin.daemon.task;

import java.util.*;

import com.griffin.core.*;
import com.griffin.core.task.*;
import com.griffin.core.server.*;

public class DaemonTaskFactory extends TaskFactory {
    private ServerInfoParser infoParser;
    
    public DaemonTaskFactory(ServerInfoParser infoParser) {
        this.infoParser = infoParser;
    }
    
    public LoadedTasks getTasks() {
        LoadedTasks tasks = new LoadedTasks();
        
        // open ended
        
        // parameterized
        
        // simple
        
        return tasks;
    }
}
