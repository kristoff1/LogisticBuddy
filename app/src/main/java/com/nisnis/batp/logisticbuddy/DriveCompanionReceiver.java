package com.nisnis.batp.logisticbuddy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by nisie on 9/4/16.
 */
public class DriveCompanionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean screenOff = false;
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            screenOff = true;
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            screenOff = false;
        }
        Intent i = new Intent(context, DriveCompanionService.class);
        i.putExtra("screen_is_off", screenOff);
        context.startService(i);
    }
}
