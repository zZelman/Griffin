package com.griffin.desktop.daemon;

import java.util.*;

import com.griffin.core.*;
import com.griffin.desktop.daemon.task.*;

public class ConcreteTaskFactory extends TaskFactory {
    private ServerInfoParser infoParser;
    
    public ConcreteTaskFactory(ServerInfoParser infoParser) {
        this.infoParser = infoParser;
    }
    
    public List<Task> getAll() {
        List<Task> tasks = new LinkedList<Task>();
        
        // open ended
        tasks.add(new ExamplePrefixTask(this.infoParser));
        
        // parameterized
        tasks.add(new ExampleParameterizedTask());
        
        // not parameterized
        tasks.add(new ExampleHelloWorldTask());
        tasks.add(new ExamplePrevCommTask());
        tasks.add(new ExampleChainTask(this.infoParser));
        
        return tasks;
    }
}
