package com.griffin.core;

import java.util.*;

import com.griffin.core.*;

abstract public class TaskFactory {
    /**
       NOTE: the ordering of this list effectivly deffines the order of operations
             in order to have paramatarized tasks, you MUST add the first
    */
    abstract public List<Task> getAll(Output output);
}
