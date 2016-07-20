package com.griffin.core.output;

public class StartingOutput extends StringOutput {
    public StartingOutput(String data) {
        super("starting command: \"" + data + "\"");
    }
}
