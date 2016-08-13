package com.griffin.core.task;

import java.io.*;
import java.util.regex.*;

import com.griffin.core.*;
import com.griffin.core.output.*;
import com.griffin.core.recurring.*;

public class RemoveRecurringTask extends Task {
    private RecurringManager recurringManager;
    
    private String name;
    
    public RemoveRecurringTask(RecurringManager recurringManager) {
        super("remove recurring [name]",
              "executes the 'command' every 'sec' on this server",
              "remove recurring [name]: success",
              "remove recurring [name]: failure");
              
        this.recurringManager = recurringManager;
    }
    
    @Override
    public String canUse(String rawInput) {
        String space = "( )";
        
        String prefix = "(remove recurring)";
        String name = "([^ ]+)";
        
        Pattern p = Pattern.compile(prefix + space + name, Pattern.DOTALL);
        Matcher m = p.matcher(rawInput);
        
        if (m.find()) {
            this.name = m.group(3);
            
            return rawInput.replaceFirst(prefix + space + name, "");
        }
        return null;
    }
    
    @Override
    public Output doAction(Communication prevComm) {
        String cmd = "remove recurring " + this.name;
        Output output = new StartingOutput(cmd);
        
        boolean isSuccess = this.recurringManager.remove(this.name);
        
        if (isSuccess) {
            output.addOutput(new SuccessOutput(cmd + ": success"));
        } else {
            output.addOutput(new FailureOutput(cmd + ": failure"));
        }
        
        return output;
    }
    
    @Override
    public void clear() {
        this.name = "";
    }
}
