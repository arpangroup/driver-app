package com.pureeats.driverapp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.pureeats.driverapp.commons.Actions;
import com.pureeats.driverapp.services.EndlessService;

public class RestartBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "RestartBroadcast";
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intentFetchService = new Intent(context, EndlessService.class);
        intentFetchService.setAction(Actions.START.name());
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            Log.d(TAG, "Starting the service in >=26 Mode from a BroadcastReceiver");
            context.startForegroundService(intentFetchService);
            return;
        }
        Log.d(TAG, "Starting the service in < 26 Mode from a BroadcastReceiver");
        context.startService(intentFetchService);
    }
}
