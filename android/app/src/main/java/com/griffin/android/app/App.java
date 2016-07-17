package com.griffin.android.app;

import android.app.*;
import android.app.ActivityManager.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.view.inputmethod.*;
import android.widget.*;

import java.net.*;
import java.io.*;
import java.lang.*;
import java.util.*;

import org.apache.commons.lang3.*;

import com.griffin.core.*;

import com.griffin.android.app.*;

import java.lang.reflect.*;

public class App extends Activity implements OnClickListener {
    private Button startService;
    private TextView isServiceRunningText;
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
        
        this.isServiceRunningText = (TextView) findViewById(R.id.isServiceRunningText);
        if (AppService.isRunning()) {
            this.isServiceRunningText.setText(SERVICE_STARTED);
        } else {
            this.isServiceRunningText.setText(SERVICE_STOPPED);
        }
        
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
        
        if (AppService.isRunning() == false) {
            startService(new Intent(getBaseContext(), AppService.class));
            this.isServiceRunningText.setText(this.SERVICE_STARTED);
        } else {
            Toast.makeText(this, "service already running", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void stopService() {
        this.vibrate();
        
        if (AppService.isRunning() == true) {
            stopService(new Intent(getBaseContext(), AppService.class));
            this.isServiceRunningText.setText(this.SERVICE_STOPPED);
        } else {
            Toast.makeText(this, "service not running", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void sendCommand() {
        this.vibrate();
        
        if (!this.checkNetworkConnection()) {
            Toast.makeText(this, "no network connection", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED) == false) {
            Toast.makeText(this, "cannot access server info file", Toast.LENGTH_SHORT).show();
            return;
        }
        
        //// this comment is for when the file is in the home directory (not yet, still in dev)
        // String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        // baseDir + File.separator + "server_list.ini",
        
        // source: http://stackoverflow.com/a/9378468/961312
        String[] userInput = this.commandText.getText().toString().split(" ", 2);
        
        if (userInput.length != 2) {
            Toast.makeText(this, "please enter a command", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "server info file not found", Toast.LENGTH_SHORT).show();
            return;
        } catch (IOException e) {
            Toast.makeText(this, "io error", Toast.LENGTH_SHORT).show();
            return;
        } catch (ServerInfoException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }
        
        new Networking(info, command).execute();
        
        this.hideKeyboard();
    }
    
    private void vibrate() {
        this.vibrator.vibrate(VIBRATE_LENGTH);
    }
    
    private void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
    }
    
    private boolean checkNetworkConnection() {
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
               activeNetwork.isConnectedOrConnecting();
    }
    
    class Networking extends AsyncTask<Void, Void, Void> implements ClientCallBack {
        private final ServerInfo info;
        private final Serializable command;
        private Client client;
        
        private List<String> outputs;
        
        public Networking(ServerInfo info, Serializable command) {
            this.info = info;
            this.command = command;
            this.client = new Client(this, this.info, this.command);
            
            this.outputs = new LinkedList<String>();
        }
        
        @Override
        protected void onPreExecute() {
            TextView commandOutput = (TextView) findViewById(R.id.commandOutput);
            commandOutput.setText("working...");
        }
        
        @Override
        protected Void doInBackground(Void... params) {
            client.start();
            return null;
        }
        
        @Override
        protected void onProgressUpdate(Void... params) {
            this.showWhatHave();
        }
        
        @Override
        protected void onPostExecute(Void param) {
            this.showWhatHave();
        }
        
        @Override
        public void recieved(Object o) {
            outputs.add(o.toString() + "\n");
        }
        
        @Override
        public void dealWith(UnknownHostException e) {
            outputs.add(e.toString() + "\n");
        }
        
        @Override
        public void dealWith(ClassNotFoundException e) {
            outputs.add(e.toString() + "\n");
        }
        
        @Override
        public void dealWith(IOException e) {
            outputs.add(e.toString() + "\n");
        }
        
        private void showWhatHave() {
            TextView commandOutput = (TextView) findViewById(R.id.commandOutput);
            commandOutput.setText("");
            for (String s : this.outputs) {
                commandOutput.append(s);
            }
        }
    }
}
