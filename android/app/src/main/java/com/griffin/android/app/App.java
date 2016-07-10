package com.griffin.android.app;

import android.app.*;
import android.os.*;
import android.widget.*;
import android.view.*;
import android.view.View.*;
import android.view.inputmethod.*;
import android.content.*;
import android.util.*;
import android.net.*;

import java.net.*;
import java.io.*;
import java.lang.*;

import org.apache.commons.lang3.*;

import com.griffin.core.*;

public class App extends Activity implements OnClickListener {
    private Button startService;
    private TextView isServiceRunning;
    private Button stopService;
    private EditText commandText;
    private Button sendCommand;
    private TextView commandOutput;
    
    private Vibrator vibrator;
    private final int VIBRATE_LENGTH = 50; // ms
    
    private final String SERVICE_STOPPED = "STOPPED";
    private final String SERVICE_STARTED = "STARTED";
    
    public static final String TAG = "App";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        this.startService = (Button) findViewById(R.id.startService);
        this.startService.setOnClickListener(this);
        
        this.isServiceRunning = (TextView) findViewById(R.id.isServiceRunning);
        this.isServiceRunning.setText(this.SERVICE_STOPPED);
        
        this.stopService = (Button) findViewById(R.id.stopService);
        this.stopService.setOnClickListener(this);
        
        this.commandText = (EditText) findViewById(R.id.commandText);
        
        this.sendCommand = (Button) findViewById(R.id.sendCommand);
        this.sendCommand.setOnClickListener(this);
        
        this.commandOutput = (TextView) findViewById(R.id.commandOutput);
        
        this.vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startService:
                this.startService();
                break;
            case R.id.stopService:
                this.stopService();
                break;
            case R.id.sendCommand:
                this.sendCommand();
                break;
        }
    }
    
    private void startService() {
        this.vibrate();
        
        this.isServiceRunning.setText(this.SERVICE_STARTED);
    }
    
    private void stopService() {
        this.vibrate();
        
        this.isServiceRunning.setText(this.SERVICE_STOPPED);
    }
    
    private void sendCommand() {
        this.vibrate();
        
        if (!this.checkNetworkConnection()) {
            Context context = getApplicationContext();
            CharSequence text = "no network connection";
            Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            toast.show();
            
            return;
        }
        
        // hide keyboard
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
    }
    
    private void vibrate() {
        this.vibrator.vibrate(this.VIBRATE_LENGTH);
    }
    
    private boolean checkNetworkConnection() {
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
               activeNetwork.isConnectedOrConnecting();
    }
}
