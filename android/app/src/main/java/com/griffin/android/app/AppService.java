package com.griffin.android.app;

import android.app.*;
import android.content.*;
import android.os.*;
import android.widget.*;

import com.griffin.android.app.*;

public class AppService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service started", Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }
    
    @Override
    public void onDestroy() {
        Toast.makeText(this, "service stopped", Toast.LENGTH_SHORT).show();
    }
}
