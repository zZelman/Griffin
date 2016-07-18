package com.griffin.core;

import org.junit.*;
import static org.junit.Assert.*;

import java.util.*;

import com.griffin.core.output.*;

public class TestOutput {
    @Before
    public void startUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void testStringOutput() {
        StringOutput so1 = new StringOutput("1");
        StringOutput so2 = new StringOutput("2");
        StringOutput so3 = new StringOutput("3");
        StringOutput so4 = new StringOutput("4");
        
        so1.addOutput(so2);
        so1.addOutput(so3);
        so1.addOutput(so4);
        
        StringOutput so0 = new StringOutput("0");
        so0.addOutput(so1);
        
        this.println(0, so0);
    }
    
    @Test
    public void testSubtaskOutput() {
        StringOutput so0 = new StringOutput("0");
        StringOutput so1 = new StringOutput("1");
        StringOutput so2 = new StringOutput("2");
        StringOutput so3 = new StringOutput("3");
        StringOutput so4 = new StringOutput("4");
        
        so0.addOutput(so1);

        // "subtask"
        so2.addOutput(so3);
        
        try {
            // adds to end of the datastructure, which is so1 atm,
            // and then prints it before that node
            so0.setSubtaskOutput(so2);
        } catch (SubtaskOutputException e) {
            e.printStackTrace();
        }
        so0.addOutput(so4);
        
        this.println(0, so0);
    }
    
    private void println(int indentLevel, NewOutput curr) {
        String indentStr = "";
        for (int i = 0; i < indentLevel; ++i) {
            indentStr += "|   ";
        }
        
        while (curr.hasNext()) {
            if (curr.hasSubtaskOutput()) {
                this.println(++indentLevel, curr.getSubtaskOutput());
            }
            System.out.println(indentStr + curr);
            curr = curr.next();
        }
        System.out.println(indentStr + curr);
    }
}
