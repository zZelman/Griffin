package com.griffin.android.app;

import android.app.*;
import android.os.*;
import android.widget.*;

public class App extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        EditText commandText = (EditText) findViewById(R.id.commandText);
        commandText.setEnabled(false);
        commandText.setEnabled(true);
        
        TextView isServiceRunning = (TextView) findViewById(R.id.isServiceRunning);
        isServiceRunning.setText("STOPPED");
        
        TextView commandOutput = (TextView) findViewById(R.id.commandOutput);
        
        String s = "";
        for (int i = 0; i < 100; i++) {
            s += "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\n";
        }
        commandOutput.setText(s);
    }
}
