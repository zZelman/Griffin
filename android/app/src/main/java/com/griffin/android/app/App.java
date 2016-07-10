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
import java.util.*;

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
            this.showTost("no network connection");
            return;
        }
        
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED) == false) {
            this.showTost("cannot access server info file");
            return;
        }
        
        //// this comment is for when the file is in the home directory (not yet, still in dev)
        // String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        // baseDir + File.separator + "server_list.ini",
        
        // source: http://stackoverflow.com/a/9378468/961312
        String[] userInput = this.commandText.getText().toString().split(" ", 2);
        
        if (userInput.length != 2) {
            this.showTost("please enter a command");
            return;
        }
        
        String target = userInput[0];
        String command = userInput[1];

        // String target = "desktop";
        // String command = "prev comm";
        
        ServerInfo info = null;
        try {
            InputStream inputStream = this.getResources().openRawResource(R.raw.server_list);
            ServerInfoParser infoParser = new ServerInfoParser(inputStream);
            info = infoParser.getServerInfo(target);
        } catch (FileNotFoundException e) {
            this.showTost("server info file not found");
            return;
        } catch (URISyntaxException | IOException e) {
            this.showTost("io error");
            return;
        } catch (Exception e) {
            this.showTost(e.getMessage());
            return;
        }
        
        new Networking(info, command).execute();
        
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
    
    private void showTost(CharSequence text) {
        Context context = this.getApplicationContext();
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.show();
    }
    
    class Networking extends AsyncTask<Void, Void, Void> {
        private final ServerInfo info;
        private final String command;
        
        private List<String> outputs;
        
        public Networking(ServerInfo info, String command) {
            this.info = info;
            this.command = command;
        }
        
        @Override
        protected void onPreExecute() {
            this.outputs = new LinkedList<String>();
            
            TextView commandOutput = (TextView) findViewById(R.id.commandOutput);
            commandOutput.setText("working...");
        }
        
        @Override
        protected Void doInBackground(Void... params) {
            TextView commandOutput = (TextView) findViewById(R.id.commandOutput);
            commandOutput.setText("");
            
            try {
                Socket socket = new Socket(info.getHostName(), info.getPort());
                Communication nextComm = new Communication(socket);
                
                nextComm.send(command);
                
                Object ret;
                while (true) {
                    ret = nextComm.receive();
                    if (ret instanceof StopCommunication || ret == null) {
                        break;
                    }
                    
                    outputs.add(ret.toString() + "\n");
                }
                
                nextComm.close();
            } catch (UnknownHostException e) {
                outputs.add(e.toString() + "\n");
            } catch (ClassNotFoundException e) {
                outputs.add(e.toString() + "\n");
            } catch (IOException e) {
                outputs.add(e.toString() + "\n");
            }
            
            return null;
        }
        
        @Override
        protected void onProgressUpdate(Void... values) {}
        
        @Override
        protected void onPostExecute(Void params) {
            TextView commandOutput = (TextView) findViewById(R.id.commandOutput);
            for (String s : this.outputs) {
                commandOutput.append(s);
            }
        }
    }
}


