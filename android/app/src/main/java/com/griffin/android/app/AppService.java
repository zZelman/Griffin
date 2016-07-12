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

public class AppService extends Service {
    public static final int ID = 1234;
    
    private static boolean isRunning = false;
    
    private ServerInfoParser infoParser;
    private ServerSocket serverSocket;
    
    public static boolean isRunning() {
        return AppService.isRunning;
    }
    
    @Override
    public void onCreate() {
        try {
            InputStream inputStream = this.getResources().openRawResource(R.raw.server_list);
            this.infoParser = new ServerInfoParser(inputStream);
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "server info file not found", Toast.LENGTH_SHORT).show();
            return;
        } catch (IOException e) {
            Toast.makeText(this, "io error", Toast.LENGTH_SHORT).show();
            return;
        }
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
    
    private void start() {
        if (AppService.isRunning == true) {
            return;
        }
        
        AppService.isRunning = true;
        Toast.makeText(this, "service started", Toast.LENGTH_SHORT).show();
        
        new Thread(new ServerThread(this.infoParser)).start();
        
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
        } catch (IOException e) {}
        
        stopForeground(true);
        stopSelf();
    }
    
    class ServerThread implements Runnable {
        private final ServerInfoParser infoParser;
        private final String TARGET = "android";
        
        public ServerThread(ServerInfoParser infoParser) {
            this.infoParser = infoParser;
        }
        
        public void run() {
            ServerInfo info = null;
            try {
                info = this.infoParser.getServerInfo(TARGET);
            } catch (Exception e) {
                final String message = e.getMessage();
                
                Handler h = new Handler(AppService.this.getMainLooper());
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AppService.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }
            
            try {
                serverSocket = new ServerSocket(info.getPort());
                
                Socket clientSocket;
                Communication prevComm;
                while (!Thread.currentThread().isInterrupted()) {
                    clientSocket = serverSocket.accept();
                    
                    Handler h = new Handler(AppService.this.getMainLooper());
                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AppService.this, "connection", Toast.LENGTH_SHORT).show();
                        }
                    });
                    
                    prevComm = new Communication(clientSocket);
                    new Thread(new CommunicationThread(prevComm)).start();
                }

                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    class CommunicationThread implements Runnable {
        private Communication prevComm;
        
        public CommunicationThread(Communication prevComm) {
            this.prevComm = prevComm;
        }
        
        public void run() {
            try {
                prevComm.send("FROM ANDROID");
                prevComm.send(new StopCommunication());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
