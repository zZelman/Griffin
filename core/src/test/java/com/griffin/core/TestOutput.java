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
    
    // @Test
    public void testClassName() {
        Output so = new StringOutput("string");
        Output urio = new UnusedRawInputOutput("unused");
        
        System.out.println(so);
        System.out.println(urio);
    }
    
    // @Test
    public void testNormal() {
        StringOutput so0 = new StringOutput("0");
        StringOutput so1 = new StringOutput("1");
        StringOutput so2 = new StringOutput("2");
        StringOutput so3 = new StringOutput("3");
        StringOutput so4 = new StringOutput("4");
        
        so0.addOutput(so1);
        so0.addOutput(so2);
        so0.addOutput(so3);
        so0.addOutput(so4);
        
        this.println(0, so0);
    }
    
    // @Test
    public void testPrepend() {
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
    
    // @Test
    public void testPostpend() {
        StringOutput so0 = new StringOutput("0");
        StringOutput so1 = new StringOutput("1");
        StringOutput so2 = new StringOutput("2");
        StringOutput so3 = new StringOutput("3");
        StringOutput so4 = new StringOutput("4");
        StringOutput so5 = new StringOutput("5");
        
        so0.addOutput(so1);
        
        so2.addOutput(so3);
        so2.addOutput(so4);
        
        so0.addOutput(so2);
        so0.addOutput(so5);
        
        this.println(0, so0);
    }
    
    // @Test
    public void testSubtask() {
        StringOutput so0 = new StringOutput("0");
        StringOutput so1 = new StringOutput("1");
        StringOutput so2 = new StringOutput("2");
        StringOutput so3 = new StringOutput("3");
        StringOutput so4 = new StringOutput("4");
        
        try {
            so0.setSubtaskOutput(so1);
            so1.setSubtaskOutput(so2);
            so2.setSubtaskOutput(so3);
            so3.setSubtaskOutput(so4);
        } catch (SubtaskOutputException e) {
            e.printStackTrace();
        }
        
        this.println(0, so0);
    }
    
    @Test
    public void testSubtaskPlusTask() {
        StringOutput so0 = new StringOutput("0");
        StringOutput so1 = new StringOutput("1");
        StringOutput so2 = new StringOutput("2");
        StringOutput so3 = new StringOutput("3");
        StringOutput so4 = new StringOutput("4");
        
        try {
            // subtask
            so1.addOutput(so2);
            so1.addOutput(so3);

            so0.setSubtaskOutput(so1);
            so0.addOutput(so4);
        } catch (SubtaskOutputException e) {
            e.printStackTrace();
        }
        
        this.println(0, so0);
    }
    
    private void println(int indentLevel, Output curr) {
        String indentStr = "";
        for (int i = 0; i < indentLevel; ++i) {
            indentStr += "|   ";
        }
        
        // deal with given
        System.out.println(indentStr + curr);
        if (curr.hasSubtaskOutput()) {
            this.println(++indentLevel, curr.getSubtaskOutput());
        }
        
        while (curr.hasNext()) {
            curr = curr.next();
            System.out.println(indentStr + curr);
            if (curr.hasSubtaskOutput()) {
                this.println(indentLevel + 1, curr.getSubtaskOutput());
            }
        }
    }
}
