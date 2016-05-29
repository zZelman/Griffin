package com.griffin.desktop.daemon;

import java.util.*;

import com.griffin.core.*;
import com.griffin.desktop.daemon.task.*;

public class ConcreteTaskFactory extends TaskFactory {
    public List<Task> getAll() {
        List<Task> tasks = new LinkedList();

        tasks.add(new HelloWorldTask());

        return tasks;
    }
} 
