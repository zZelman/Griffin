package com.griffin.desktop.daemon.task;

import java.io.*;
import java.util.regex.*;

import com.griffin.core.*;
import com.griffin.core.output.*;

public class ExampleParameterizedTask extends Task {
    private String word1;
    private String word2;
    
    public ExampleParameterizedTask() {
        super("print [str] [str]",
              "(example) print on the server the two strings in the [str] position",
              "print [str] [str]: success",
              "print [str] [str]: failure");
    }

    @Override
    public String canUse(String rawInput) {
        String space = "( )";
        
        String print = "(print)";
        String word1 = "([^ ]+)";
        String word2 = "([^ ]+)";
        
        Pattern p = Pattern.compile(print + space + word1 + space + word2, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher m = p.matcher(rawInput);
        
        if (m.find()) {
            this.word1 = m.group(3);
            this.word2 = m.group(5);
            
            return rawInput.replaceFirst(print + space + word1 + space + word2, "");
        }
        return null;
    }

    @Override
    public Output doAction(Communication prevComm) {
        // TODO: acutally have word1 and word2 in the StartingOutput
        Output output = new StartingOutput(this.command);
        
        System.out.println("[ExampleParameterizedTask::doAction] word1=" + this.word1 + ", word2=" + this.word2);
        
        output.addOutput(new SuccessOutput(this.success));
        return output;
    }

    @Override
    public void clear() {
        this.word1 = "";
        this.word2 = "";
    }
}
