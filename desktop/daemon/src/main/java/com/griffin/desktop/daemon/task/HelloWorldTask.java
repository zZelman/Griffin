package com.griffin.desktop.daemon.task;

import com.griffin.core.*;

public class HelloWorldTask extends Task {
    public HelloWorldTask(Output output) {
        super(output,
              "hello world",
              "hello world: success",
              "hello world: failure");
    }
    
    public String doAction() {
        this.output.addExecutionMessage("doAction: hello world");
        return this.success;
    }
}
