package com.griffin.core;

import java.util.*;

import com.griffin.core.*;

abstract public class TaskFactory {
    protected Griffin griffin;

    public void setGriffin(Griffin griffin) {
        this.griffin = griffin;
    }
    
    abstract public LoadedTasks getTasks();
}
