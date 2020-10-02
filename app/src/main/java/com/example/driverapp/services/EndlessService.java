package com.example.driverapp.services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.driverapp.R;
import com.example.driverapp.commons.Actions;
import com.example.driverapp.sharedprefs.ServiceTracker;
import com.example.driverapp.views.App;
import com.example.driverapp.views.MainActivity;

import java.util.Timer;

public class EndlessService extends Service {
    //https://robertohuertas.com/2019/06/29/android_foreground_services/
    private static final String TAG = "EndlessService";

    private PowerManager.WakeLock wakeLock;
    private boolean isServiceStarted = false;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand executed with startId: "+startId);
        if(intent  != null){
            String action = intent.getAction();
            Log.d(TAG, "Using an Intent with action "+action);
            if(action != null){
                if(action.equals(Actions.START.name()))startService();
                else if(action.equals(Actions.STOP.name()))stopService();
                else Log.d(TAG, "This should never happen. No action in the received intent");
            }
        }else{
            Log.d(TAG, "with a null intent. It has been probably restarted by the system.");
        }

        //Notification notification = createNotification();
        //startForeground(1, notification);

        //do heavy work in a background thread
        //stopSelf()

        // by returning this we make sure the service is restarted if the system kills the service
        return START_STICKY;
    }



    private void startService() {
        if(isServiceStarted) return;
        Log.d(TAG, "Starting the foreground service task");
        Toast.makeText(this, "Service starting its task", Toast.LENGTH_SHORT).show();
        isServiceStarted = true;
        ServiceTracker.setServiceState(this, ServiceTracker.ServiceState.STARTED);

        // we need this lock so our service gets not affected by Doze Mode
        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        //wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "whatever");
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "NewOrderFetchService::lock");
        wakeLock.acquire(60 * 1000L /*1 minutes*/);

        // we're starting a loop in a coroutine
        new Thread(() -> {
            while (isServiceStarted){
                //pingFakeServer();
            }
        });
        Log.d(TAG, "End of the loop for the service");
    }
    private void stopService() {
        Log.d(TAG, "Stopping the foreground service");
        Toast.makeText(this, "Service stopping", Toast.LENGTH_SHORT).show();

        try{
            if(wakeLock.isHeld()){
                wakeLock.release();
            }
            stopForeground(true);
            stopSelf();
        }catch (Exception e){
            e.printStackTrace();
            Log.d(TAG, "Service stopped without being started: "+e.getMessage());
        }
        isServiceStarted = false;
        ServiceTracker.setServiceState(this, ServiceTracker.ServiceState.STOPPED);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
        Notification notification = createNotification();

        startForeground(App.NOTIFICATION_ID_NEW_ORDER, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
        Toast.makeText(this, "Service destroyed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Intent restartServiceIntent  = new Intent(getApplicationContext(), EndlessService.class);
        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(this, 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        AlarmManager alarmService =(AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() +1000, restartServicePendingIntent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG,  "Some component want to bind with the service");
        // We don't provide binding, so return null
        return null;
    }

    private Notification createNotification(){
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, App.CHANNEL_ID_NEW_ORDER);
        builder.setContentTitle("Fetching New Order")
                .setContentText("Fetching....")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setChannelId(App.CHANNEL_ID_NEW_ORDER)
                .setOngoing(true);
        Notification notification = builder.build();


        Log.d(TAG, "Opening DialogActivity from Notification......");
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.notify(App.NOTIFICATION_ID_NEW_ORDER, notification);

        return notification;

    }
}
