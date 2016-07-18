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
        
        NewOutput curr = so0;
        while (curr.hasNext()) {
            System.out.println(curr);
            curr = curr.next();
        }
        System.out.println(curr);
    }
}
