package com.griffin.core.task;

import java.io.*;
import java.util.regex.*;

import com.griffin.core.*;
import com.griffin.core.output.*;
import com.griffin.core.recurring.*;

public class AddRecurringTask extends Task {
    private RecurringManager recurringManager;
    
    private String name;
    private int period;
    private String nextCommand;
    
    public AddRecurringTask(RecurringManager recurringManager) {
        super("add recurring [name] [sec] [command...]",
              "executes the 'command' every 'sec' on this server named 'name'");

        this.recurringManager = recurringManager;
    }
    
    @Override
    public String canUse(String rawInput) {
        String space = "( )";
        
        String prefix = "(add recurring)";
        String name = "([^ ]+)";
        String period = "([0-9]+)";
        String nextCommand = "(.+)";
        
        Pattern p = Pattern.compile(prefix + space + name + space + period + space + nextCommand, Pattern.DOTALL);
        Matcher m = p.matcher(rawInput);
        
        if (m.find()) {
            this.name = m.group(3);
            try {
                this.period = Integer.parseInt(m.group(5));
            } catch (NumberFormatException e) {
                return null;
            }
            this.nextCommand = m.group(7);

            this.setRuntimeCommand("add recurring " + this.name + " " + this.period + " " + this.nextCommand);
            return rawInput.replaceFirst(prefix + space + name + space + period + space + nextCommand, "");
        }
        return null;
    }
    
    @Override
    public Output doAction(Communication prevComm) {
        Output output = new StartingOutput(this.getRuntimeCommand());
        
        boolean isSuccess = false;
        try {
            isSuccess = this.recurringManager.add(this.name, this.period, this.nextCommand);
        } catch (IOException e) {
            output.addOutput(new ErrorOutput(e.toString()));
            isSuccess = false;
        }
        
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
        this.period = 0;
        this.nextCommand = "";
    }
}
