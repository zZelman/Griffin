package com.griffin.core.output;

public class UnusedRawInputOutput extends StringOutput {
    private final String commandStuffLeftOver = "there were parts of the command that were not used: ";
    
    public UnusedRawInputOutput(String data) {
        super(data);
    }
    
    public String getMessage() {
        return this.commandStuffLeftOver + "\"" + this.getString() + "\"";
    }
}
