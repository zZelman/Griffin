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

public class AppService extends Service implements ServerCallBack, Startable {
    public static final int ID = 1234;
    private static boolean isRunning = false;
    private ServerSocket serverSocket;
    
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
        this.showToast(info.toFormatedString());
    }
    
    @Override
    public void taskList(String s) {
        this.showToast(s);
    }
    
    @Override
    public void startedConnection(String remoteAddr, String localAddr) {
        this.showToast("connection from: [" + remoteAddr + "]");
    }
    
    @Override
    public void commandRecieved(String s) {
        this.showToast("input: [" + s + "]");
    }
    
    @Override
    public void serverEnding(String s) {
        this.showToast(s);

        // this must be here, otherwise the command will be
        // ran on a bad thread & cause a crash
        Handler h = new Handler(AppService.this.getMainLooper());
        h.post(new Runnable() {
            @Override
            public void run() {
                AppService.this.stop();
            }
        });
    }
    
    @Override
    public void dealWith(ClassNotFoundException e) {
        this.showToast(e.getMessage().toLowerCase());
    }
    
    @Override
    public void dealWith(IOException e) {
        this.showToast(e.getMessage().toLowerCase());
    }
    
    @Override
    public boolean start() {
        if (AppService.isRunning == true) {
            return false;
        }
        
        AppService.isRunning = true;
        Toast.makeText(this, "service started", Toast.LENGTH_SHORT).show();
        
        try {
            InputStream inputStream = this.getResources().openRawResource(R.raw.server_list);
            ServerInfoParser infoParser = new ServerInfoParser(inputStream);
            
            TaskFactory taskFactory = new AppTaskFactory();
            Server server = new Server(this, infoParser.getServerInfo(getString(R.string.target)), taskFactory);
            
            new Thread(server).start();
        } catch (FileNotFoundException e) {
            AppService.isRunning = false;
            this.showToast("server info file not found");
            return false;
        } catch (IOException e) {
            AppService.isRunning = false;
            this.showToast("io error");
            return false;
        } catch (ServerInfoException e) {
            AppService.isRunning = false;
            this.showToast(e.getMessage().toLowerCase());
            return false;
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

        return true;
    }
    
    @Override
    public boolean stop() {
        if (AppService.isRunning == false) {
            return false;
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

        return true;
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
}
