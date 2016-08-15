package com.griffin.daemon.task;

import java.io.*;
import java.util.regex.*;

import com.griffin.core.*;
import com.griffin.core.output.*;
import com.griffin.core.task.*;

public class ExampleParameterizedTask extends Task {
    private String word1;
    private String word2;
    
    public ExampleParameterizedTask() {
        // TODO: maybe some formal replacement syntax for commands who are parameterized
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
        
        Pattern p = Pattern.compile(print + space + word1 + space + word2, Pattern.DOTALL);
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
        Output output = new StartingOutput("print " + this.word1 + " " + this.word2);
        
        System.out.println("[ExampleParameterizedTask] word1=" + this.word1 + ", word2=" + this.word2);
        
        output.addOutput(new SuccessOutput("print " + this.word1 + " " + this.word2 + ": success"));
        return output;
    }
    
    @Override
    public void clear() {
        this.word1 = "";
        this.word2 = "";
    }
}
