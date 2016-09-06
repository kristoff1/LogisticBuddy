package com.nisnis.batp.logisticbuddy;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class DriveCompanionService extends Service {
    public DriveCompanionService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        BroadcastReceiver mReceiver = new DriveCompanionReceiver();
        registerReceiver(mReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean screen_is_off = intent.getBooleanExtra("screen_is_off", false);
        if (!screen_is_off) {
            Log.d("NISINISI", "SCREEN ONNNN");
        } else {
            Log.d("NISINISI", "SCREEN OFFFF");
        }
        return START_STICKY;

    }
}
