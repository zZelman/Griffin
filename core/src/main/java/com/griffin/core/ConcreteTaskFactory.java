package com.griffin.core;

import java.util.*;

import com.griffin.core.*;
import com.griffin.core.task.*;

public class ConcreteTaskFactory extends TaskFactory {
    private Griffin griffin;
    
    public ConcreteTaskFactory(Griffin griffin) {
        this.griffin = griffin;
    }
    
    public List<Task> getAll(Output output) {
        List<Task> tasks = new LinkedList<Task>();
        
        tasks.add(new PrintAllCommandsTask(output, this.griffin));
        
        return tasks;
    }
}
