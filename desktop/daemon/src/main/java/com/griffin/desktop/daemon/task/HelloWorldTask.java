package com.griffin.desktop.daemon.task;

import java.io.*;

import com.griffin.core.*;

public class HelloWorldTask extends Task {
    public HelloWorldTask(Output output) {
        super(output,
              "hello world",
              "prints hello world",
              "hello world: success",
              "hello world: failure");
    }
    
    public boolean canUse(String rawInput) {
        return rawInput.contains(this.getCommand());
    }
    
    public String doAction(Communication prevComm) {
        this.output.addExecutionMessage("[HelloWorldTask::doAction] client communication");
        System.out.println("[HelloWorldTask::doAction] server execution");
        
        try {
            prevComm.send("~~ communication from the actual Task");
        } catch (IOException e) {
            this.output.addExecutionMessage("IOException trying to direct communication");
            e.printStackTrace();
        }
        
        return this.success;
    }
}
