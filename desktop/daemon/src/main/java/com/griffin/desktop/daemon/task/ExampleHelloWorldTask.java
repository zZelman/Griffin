package com.griffin.desktop.daemon.task;

import java.io.*;

import com.griffin.core.*;

public class ExampleHelloWorldTask extends Task {
    public ExampleHelloWorldTask() {
        super("hello world",
              "basic example of what things do",
              "hello world: success",
              "hello world: failure");
    }
    
    public Output doAction(Communication prevComm) {
        Output output = new Output();
        
        System.out.println("server execution");
        
        output.addExecutionMessage("client communication");
        
        return output.addReturnMessage(this.success);
    }
}
