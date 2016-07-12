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
    
    private Thread serverThread;
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
    
    private void start() {
        if (AppService.isRunning == true) {
            return;
        }
        
        AppService.isRunning = true;
        Toast.makeText(this, "service started", Toast.LENGTH_SHORT).show();
        
        this.serverThread = new Thread(new ServerThread());
        this.serverThread.start();
        
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
            e.printStackTrace();
        }
        
        stopForeground(true);
        stopSelf();
    }
    
    class ServerThread implements Runnable {
        public void run() {
            try {
                serverSocket = new ServerSocket(6000);
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            Socket clientSocket;
            Communication prevComm;
            while (!Thread.currentThread().isInterrupted()) {
                try {
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
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
