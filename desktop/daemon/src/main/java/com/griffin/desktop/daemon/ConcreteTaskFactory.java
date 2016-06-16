package com.griffin.desktop.daemon;

import java.util.*;

import com.griffin.core.*;
import com.griffin.desktop.daemon.task.*;

public class ConcreteTaskFactory extends TaskFactory {
    public List<Task> getAll() {
        List<Task> tasks = new LinkedList<Task>();

        // parameterized
        tasks.add(new ExampleParameterizedTask());

        // not parameterized
        tasks.add(new ExampleHelloWorldTask());
        tasks.add(new ExamplePrevCommTask());
        tasks.add(new ExampleChainTask());

        return tasks;
    }
} 
