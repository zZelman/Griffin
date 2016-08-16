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
    public void testJava() throws Exception {
        String input = "desktop hello world android thing thing thing android thing desktop ddd";

        String[] names = new String[]{"desktop", "android"};

        String[] s = input.split("(?=(android|desktop))");
        ArrayList<String> list = new ArrayList<String>();
        for (String str : s) {
            list.add(str.trim());
        }

        System.out.println(list);
    }
}
