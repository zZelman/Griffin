package com.griffin.core.output;

public class StringOutput extends Output {
    private String data;
    
    public StringOutput(String data) {
        this.data = data;
    }
    
    public String getString() {
        return this.data;
    }
    
    public String toString() {
        return this.getClass().getSimpleName() + "::data=[" + this.getString() + "]";
    }

    public boolean equals(StringOutput other) {
        return this.data.equals(other.data);
    }
}
