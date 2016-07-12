package com.griffin.android.app;

import android.app.*;
import android.content.*;
import android.os.*;
import android.widget.*;
import android.support.v4.app.*;

import java.io.*;
import java.net.*;

import com.griffin.android.app.*;

import com.griffin.core.*;

public class AppService extends Service implements ServerCallBack {
    public static final int ID = 1234;
    private static boolean isRunning = false;
    private ServerSocket serverSocket;
    private final String TARGET = "android";
    
    public static boolean isRunning() {
        return AppService.isRunning;
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.start();
        return START_STICKY;
    }
    
    @Override
    public void onDestroy() {
        this.stop();
    }
    
    @Override
    public void startedServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }
    
    @Override
    public void serverInfo(ServerInfo info) {
        this.showToast(info.toString());
    }
    
    @Override
    public void taskList(String s) {
        this.showToast(s);
    }
    
    @Override
    public void startedConnection() {
        this.showToast("connection");
    }
    
    @Override
    public void commandRecieved(String s) {
        this.showToast("input: [" + s + "]");
    }
    
    @Override
    public void serverEnding(String s) {
        this.showToast(s);
    }
    
    @Override
    public void dealWith(ClassNotFoundException e) {
        this.showToast(e.getMessage().toLowerCase());
    }
    
    @Override
    public void dealWith(IOException e) {
        this.showToast(e.getMessage().toLowerCase());
    }
    
    private void showToast(final String s) {
        Handler h = new Handler(AppService.this.getMainLooper());
        h.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(AppService.this, s, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void start() {
        if (AppService.isRunning == true) {
            return;
        }
        
        AppService.isRunning = true;
        Toast.makeText(this, "service started", Toast.LENGTH_SHORT).show();
        
        try {
            InputStream inputStream = this.getResources().openRawResource(R.raw.server_list);
            ServerInfoParser infoParser = new ServerInfoParser(inputStream);
            
            TaskFactory taskFactory = new ConcreteTaskFactory();
            
            Server server = new Server(infoParser.getServerInfo(TARGET), taskFactory, this);
            this.serverSocket = server.getServerSocket();
            
            new Thread(server).start();
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "server info file not found", Toast.LENGTH_SHORT).show();
            AppService.isRunning = false;
            return;
        } catch (IOException e) {
            Toast.makeText(this, "io error", Toast.LENGTH_SHORT).show();
            AppService.isRunning = false;
            return;
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage().toLowerCase(), Toast.LENGTH_SHORT).show();
            AppService.isRunning = false;
            return;
        }
        
        Intent intent = new Intent(this, App.class);
        PendingIntent returnToApp =
            PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            );
            
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.ic_notification)
        .setContentTitle(getString(R.string.notification_title))
        .setContentText(getString(R.string.notification_text))
        .setContentIntent(returnToApp);
        
        startForeground(AppService.ID, notificationBuilder.build());
    }
    
    private void stop() {
        if (AppService.isRunning == false) {
            return;
        }
        
        AppService.isRunning = false;
        Toast.makeText(this, "service stopped", Toast.LENGTH_SHORT).show();
        
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            this.dealWith(e);
        }
        
        stopForeground(true);
        stopSelf();
    }
}
