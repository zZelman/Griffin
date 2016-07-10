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

import java.lang.reflect.*;

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
            Context context = this.getApplicationContext();
            CharSequence text = "no network connection";
            Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            toast.show();
            
            return;
        }
        
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED) == false) {
            Context context = this.getApplicationContext();
            CharSequence text = "cannot access server info file";
            Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            toast.show();
            
            return;
        }
        
        // this comment is for when the file is in the home directory (not yet, still in dev)
        // String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        // baseDir + File.separator + "server_list.ini",
        
        String[] args = {
            "server_list", // accessed through R, a change here means nothing
            "desktop",
            "help"
        };
        
        ServerInfo info = null;
        try {
            InputStream inputStream = this.getResources().openRawResource(R.raw.server_list);
            ServerInfoParser infoParser = new ServerInfoParser(inputStream);
            info = infoParser.getServerInfo(args[1]);
        } catch (FileNotFoundException e) {
            Log.d(App.TAG, e.toString());
            return;
        } catch (URISyntaxException | IOException e) {
            Log.d(App.TAG, e.toString());
            return;
        } catch (Exception e) {
            Log.d(App.TAG, e.toString());
            return;
        }
        
        new Thread(new NetworkThread(info)).start();
        
        // hide keyboard
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
    }
    
    private void vibrate() {
        this.vibrator.vibrate(this.VIBRATE_LENGTH);
    }
    
    private boolean checkNetworkConnection() {
        ConnectivityManager cm = (ConnectivityManager) this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
               activeNetwork.isConnectedOrConnecting();
    }
    
    class NetworkThread implements Runnable {
        private final ServerInfo info;
        
        public NetworkThread(ServerInfo info) {
            this.info = info;
        }
        
        public void run() {
            // this comment is for when the file is in the home directory (not yet, still in dev)
            // String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
            // baseDir + File.separator + "server_list.ini",
            
            String[] args = {
                "server_list", // accessed through R, a change here means nothing
                "desktop",
                "help"
            };
            
            try {
                Socket socket = new Socket(this.info.getHostName(), this.info.getPort());
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
                    
                    Log.d(App.TAG, ret.toString());
                }
                
                nextComm.close();
            } catch (UnknownHostException e) {
                Log.d(App.TAG, e.toString());
                return;
            } catch (ClassNotFoundException e) {
                Log.d(App.TAG, e.toString());
                return;
            } catch (IOException e) {
                Log.d(App.TAG, e.toString());
                return;
            }
        }
    }
}


