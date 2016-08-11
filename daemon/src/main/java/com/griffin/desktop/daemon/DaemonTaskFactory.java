package com.griffin.desktop.daemon;

import java.util.*;

import com.griffin.core.*;
import com.griffin.desktop.daemon.task.*;

public class DaemonTaskFactory extends TaskFactory {
    private ServerInfoParser infoParser;
    
    public DaemonTaskFactory(ServerInfoParser infoParser) {
        this.infoParser = infoParser;
    }
    
    public LoadedTasks getTasks() {
        LoadedTasks tasks = new LoadedTasks();
        
        // open ended
        tasks.addOpenEndedTask(new ExamplePrefixTask(this.griffin));
        
        // parameterized
        tasks.addParameterizedTask(new ExampleParameterizedTask());
        
        // simple
        tasks.addSimpleTask(new ExampleHelloWorldTask());
        tasks.addSimpleTask(new ExamplePrevCommTask());
        tasks.addSimpleTask(new ExampleChainTask(this.infoParser));
        tasks.addSimpleTask(new ExampleNetworking1Task(this.infoParser));
        tasks.addSimpleTask(new ExampleNetworking2Task(this.infoParser));
        
        return tasks;
    }
}
