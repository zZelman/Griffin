package com.griffin.core.output;

public class UnusedRawInputOutput extends StringOutput {
    public UnusedRawInputOutput(String data) {
        super("there were parts of the command that were not used: [" + data + "]");
    }
}
