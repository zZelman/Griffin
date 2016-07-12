package com.griffin.android.app;

import android.app.*;
import android.content.*;
import android.os.*;
import android.widget.*;
import android.support.v4.app.*;

import com.griffin.android.app.*;

public class AppService extends Service {
    public static final int ID = 1234;
    
    private static boolean isRunning = false;
    
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
        if (AppService.isRunning == false) {
            AppService.isRunning = true;
            
            Toast.makeText(this, "service started", Toast.LENGTH_SHORT).show();
            
            Notification notification = new NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_text))
            .build();
            
            startForeground(AppService.ID, notification);
        }
    }
    
    private void stop() {
        if (AppService.isRunning == true) {
            AppService.isRunning = false;
            
            Toast.makeText(this, "service stopped", Toast.LENGTH_SHORT).show();
            
            stopForeground(true);
            stopSelf();
        }
    }
}
