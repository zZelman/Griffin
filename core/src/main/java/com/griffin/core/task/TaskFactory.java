package com.griffin.core.task;

import java.util.*;

import com.griffin.core.task.specific.*;

public class TaskFactory {
    public List<Task> getAll() {
        List<Task> tasks = new LinkedList();

        tasks.add(new HelloWorldTask());

        return tasks;
    }
}
