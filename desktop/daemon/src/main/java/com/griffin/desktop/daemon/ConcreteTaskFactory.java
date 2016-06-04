package com.griffin.desktop.daemon;

import java.util.*;

import com.griffin.core.*;
import com.griffin.desktop.daemon.task.*;

public class ConcreteTaskFactory extends TaskFactory {
    public List<Task> getAll(Output output) {
        List<Task> tasks = new LinkedList<Task>();

        tasks.add(new ExampleParameterizedTask(output));
        tasks.add(new ExampleHelloWorldTask(output));

        return tasks;
    }
} 
