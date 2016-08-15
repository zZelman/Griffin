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
              "removes 'name' from the reucrring jobs");
              
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

            this.setRuntimeCommand("remove recurring " + this.name);
            return rawInput.replaceFirst(prefix + space + name, "");
        }
        return null;
    }
    
    @Override
    public Output doAction(Communication prevComm) {
        Output output = new StartingOutput(this.getRuntimeCommand());
        
        boolean isSuccess = this.recurringManager.remove(this.name);
        
        if (isSuccess) {
            output.addOutput(new SuccessOutput(this.success));
        } else {
            output.addOutput(new FailureOutput(this.failure));
        }
        
        return output;
    }
    
    @Override
    public void clear() {
        this.resetRuntimeCommand();
        this.name = "";
    }
}
