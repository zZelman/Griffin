package com.griffin.core.output;

public class StringOutput extends NewOutput {
    protected String data;
    
    public StringOutput(String data) {
        this.data = data;
    }
    
    public String getData() {
        return this.data;
    }
    
    public String toString() {
        return "StringOutput::data=[" + data + "]";
    }

    public boolean equals(StringOutput other) {
        return this.data.equals(other.data);
    }
}
