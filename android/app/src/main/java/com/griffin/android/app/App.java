package com.griffin.android.app;

import android.app.*;
import android.os.*;
import android.widget.*;
import android.view.*;
import android.view.View.*;
import android.view.inputmethod.*;
import android.content.*;
import android.util.*;

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

    private final String TAG = "App";
    
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
        
        String[] args = {
            "server_info.ini",
            "desktop",
            "help"
        };
        
        ServerInfoParser infoParser = new ServerInfoParser(args[0]);
        ServerInfo info = null;
        try {
            info = infoParser.getServerInfo(args[1]);
        } catch (URISyntaxException | IOException e) {
             Log.d(this.TAG, e.toString());
            System.exit(1);
        } catch (Exception e) {
            Log.d(this.TAG, e.toString());
            System.exit(1);
        }
        
        try {
            Socket socket = new Socket(info.getHostName(), info.getPort());
            Communication nextComm = new Communication(socket);
            
            String[] command = ArrayUtils.subarray(args, 2, args.length);
            Serializable userInput = StringUtils.join(command, " ");
            nextComm.send(userInput);
            
            Object ret;
            while (true) {
                ret = nextComm.receive();
                if (ret instanceof StopCommunication || ret == null) {
                    break;
                }
                
                Log.d(this.TAG, ret.toString());
            }
            
            nextComm.close();
        } catch (UnknownHostException e) {
            Log.d(this.TAG, e.toString());
            System.exit(1);
        } catch (ClassNotFoundException e) {
            Log.d(this.TAG, e.toString());
            System.exit(1);
        } catch (IOException e) {
            Log.d(this.TAG, e.toString());
            System.exit(1);
        }
        
        // hide keyboard
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
    }
    
    private void vibrate() {
        this.vibrator.vibrate(this.VIBRATE_LENGTH);
    }
}
