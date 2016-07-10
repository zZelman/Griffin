package com.griffin.android.app;

import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import java.net.*;
import java.io.*;
import java.lang.*;

import org.apache.commons.lang3.*;

import android.test.*;
import android.util.*;
import android.net.*;
import android.content.*;

import com.griffin.android.app.*;

import com.griffin.core.*;

public class ExploringInstrumentation extends InstrumentationTestCase {
    @Before
    public void startUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void testAndroid() {
        
    }
    
    // @Test
    public void testNetworkState() {
        ConnectivityManager cm = (ConnectivityManager) getInstrumentation().getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        Boolean isConnected = activeNetwork != null &&
                              activeNetwork.isConnectedOrConnecting();
                              
        Log.d(App.TAG, "testNetworkState: " + isConnected.toString());
        
        assertTrue(isConnected);
    }
}

