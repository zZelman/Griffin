package com.griffin.desktop.daemon.task;

import java.io.*;
import java.util.regex.*;

import com.griffin.core.*;

public class ExampleParameterizedTask extends Task {
    private String word1;
    private String word2;
    
    public ExampleParameterizedTask(Output output) {
        super(output,
              "print %s %s",
              "print on the server the two strings in the %s position",
              "print %s %s: success",
              "print %s %s: failure");
    }
    
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
    
    public String doAction(Communication prevComm) {
        System.out.println("[ExampleParameterizedTask::doAction] word1=" + this.word1 + ", word2=" + this.word2);
        
        return this.success;
    }
}
