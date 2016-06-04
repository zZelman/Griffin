package com.griffin.desktop.daemon.task;

import java.io.*;

import com.griffin.core.*;

public class ExampleHelloWorldTask extends Task {
    public ExampleHelloWorldTask(Output output) {
        super(output,
              "hello world",
              "prints hello world",
              "hello world: success",
              "hello world: failure");
    }
    
    public String doAction(Communication prevComm) {
        this.output.addExecutionMessage("[ExampleHelloWorldTask::doAction] client communication");
        System.out.println("[ExampleHelloWorldTask::doAction] server execution");
        
        try {
            prevComm.send("~~ communication from the actual Task");
        } catch (IOException e) {
            this.output.addExecutionMessage("IOException trying to direct communication");
            e.printStackTrace();
        }
        
        return this.success;
    }
}
