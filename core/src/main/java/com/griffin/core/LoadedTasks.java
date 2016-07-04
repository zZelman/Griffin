package com.griffin.core;

import java.util.*;

import com.griffin.core.*;

public class LoadedTasks {
    private List<Task> openEndedTasks;
    private List<Task> parameterizedTasks;
    private List<Task> simpleTasks;
    
    public LoadedTasks() {
        this.openEndedTasks = new LinkedList<Task>();
        this.parameterizedTasks = new LinkedList<Task>();
        this.simpleTasks = new LinkedList<Task>();
    }
    
    public void addOpenEndedTask(Task t) {
        this.openEndedTasks.add(t);
    }
    
    public void addParameterizedTask(Task t) {
        this.parameterizedTasks.add(t);
    }
    
    public void addSimpleTask(Task t) {
        this.simpleTasks.add(t);
    }
    
    public List<Task> getOpenEndedTasks() {
        return this.openEndedTasks;
    }
    
    public List<Task> getParameterizedTasks() {
        return this.parameterizedTasks;
    }
    
    public List<Task> getSimpleTasks() {
        return this.simpleTasks;
    }
    
    public List<Task> flatten() {
        LinkedList<Task> tasks = new LinkedList<Task>();
        
        tasks.addAll(this.openEndedTasks);
        tasks.addAll(this.parameterizedTasks);
        tasks.addAll(this.simpleTasks);
        
        return tasks;
    }
    
    public void addAll(LoadedTasks other) {
        this.openEndedTasks.addAll(other.openEndedTasks);
        this.parameterizedTasks.addAll(other.parameterizedTasks);
        this.simpleTasks.addAll(other.simpleTasks);
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        
        sb.append("Commands (in order):\n");
        
        this.toStringHelper(sb, "Open ended", this.openEndedTasks);
        this.toStringHelper(sb, "Parameterized", this.parameterizedTasks);
        this.toStringHelper(sb, "Simple", this.simpleTasks);
        
        return sb.toString();
    }
    
    private void toStringHelper(StringBuffer sb, String section, List<Task> tasks) {
        sb.append("    " + section + "\n");
        for (Task t : tasks) {
            sb.append("        " + t.getCommand() + " - " + t.getInfo() + "\n");
        }
    }
}
