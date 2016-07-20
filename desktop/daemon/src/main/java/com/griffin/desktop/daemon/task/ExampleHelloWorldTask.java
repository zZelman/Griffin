package com.griffin.desktop.daemon.task;

import java.io.*;

import com.griffin.core.*;
import com.griffin.core.output.*;

public class ExampleHelloWorldTask extends Task {
    public ExampleHelloWorldTask() {
        super("hello world",
              "(example) basic example of what things do",
              "hello world: success",
              "hello world: failure");
    }
    
    public Output doAction(Communication prevComm) {
        Output output = new StartingOutput(this.command);

        System.out.println("server execution");
        
        output.addOutput(new StringOutput("client communication"));
        
        output.addOutput(new SuccessOutput(this.success));
        return output;
    }
}
