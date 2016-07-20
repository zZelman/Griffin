package com.griffin.core;

import org.junit.*;
import static org.junit.Assert.*;

import java.util.*;

import com.griffin.core.task.*;
import com.griffin.core.output.*;

public class TestTasks {
    @Before
    public void startUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void testPrintHelpTask() {
        Griffin g = new Griffin(null);
        Task t = new PrintHelpTask(g);
        Output o = t.doAction(null);
        
        this.println(0, o);
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
                this.println(++indentLevel, curr.getSubtaskOutput());
            }
        }
    }
}
