package com.griffin.android.app;

import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import android.util.*;

public class ApplicationTest {
    @Before
    public void startUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void test() {
        Log.d("App", "hello from test");
        assertThat(true, is(false));
    }
}

