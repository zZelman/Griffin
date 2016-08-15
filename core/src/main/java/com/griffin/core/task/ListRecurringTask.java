package com.griffin.core.task;

import java.io.*;
import java.util.regex.*;

import com.griffin.core.*;
import com.griffin.core.output.*;
import com.griffin.core.recurring.*;

public class ListRecurringTask extends Task {
    private RecurringManager recurringManager;
    
    public ListRecurringTask(RecurringManager recurringManager) {
        super("list recurring",
              "prints a list of recurring jobs");
              
        this.recurringManager = recurringManager;
    }
    
    @Override
    public Output doAction(Communication prevComm) {
        Output output = new StartingOutput(this.getRuntimeCommand());
        
        output.addOutput(this.recurringManager.list());
        
        output.addOutput(new SuccessOutput(this.success));
        return output;
    }
}
