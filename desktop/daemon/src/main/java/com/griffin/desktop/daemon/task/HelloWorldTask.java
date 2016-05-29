package com.griffin.desktop.daemon.task;

import com.griffin.core.*;

public class HelloWorldTask extends Task {
    public HelloWorldTask() {
        super("hello world",
              "hello world: success",
              "hello world: failure");
    }
    
    public String doAction() {
        System.out.println("doAction: hello world");
        return this.success;
    }
}
