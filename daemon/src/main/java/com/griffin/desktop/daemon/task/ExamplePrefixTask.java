package com.griffin.desktop.daemon.task;

import java.util.*;
import java.net.*;
import java.io.*;
import java.util.regex.*;

import com.griffin.core.*;
import com.griffin.core.output.*;
import com.griffin.core.task.*;

public class ExamplePrefixTask extends Task {
    private final Griffin griffin;
    private String nextCommand;
    
    public ExamplePrefixTask(Griffin griffin) {
        super("prefix [command...]",
              "(example) runs the [command...] and mutates the output",
              "prefix [command...]: success",
              "prefix [command...]: failure");
              
        this.griffin = griffin;
    }
    
    @Override
    public String canUse(String rawInput) {
        String space = "( )";
        
        String prefix = "(prefix)";
        String nextCommand = "(.+)";
        
        Pattern p = Pattern.compile(prefix + space + nextCommand, Pattern.DOTALL);
        Matcher m = p.matcher(rawInput);
        
        if (m.find()) {
            this.nextCommand = m.group(3);
            
            return rawInput.replaceFirst(prefix + space + nextCommand, "");
        }
        return null;
    }
    
    @Override
    public Output doAction(Communication prevComm) {
        Output output = new StartingOutput(this.command);
        
        Output ret = this.griffin.doCommand(nextCommand, prevComm);
        try {
            output.setSubtaskOutput(ret);
        } catch (SubtaskOutputException e) {
            output.addOutput(new ErrorOutput(e.toString()));
        }
        
        if (output.containsError()) {
            output.addOutput(new FailureOutput(this.failure));
        } else {
            output.addOutput(new SuccessOutput(this.success));
        }
        
        return output;
    }
    
    @Override
    public void clear() {
        this.nextCommand = "";
    }
}
