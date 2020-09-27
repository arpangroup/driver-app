package com.example.driverapp.services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.driverapp.R;
import com.example.driverapp.commons.Actions;
import com.example.driverapp.models.User;
import com.example.driverapp.sharedprefs.ServiceTracker;
import com.example.driverapp.sharedprefs.UserSession;
import com.example.driverapp.views.App;
import com.example.driverapp.views.MainActivity;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewOrderFetchService extends Service {
    private static final String TAG = "NewOrderFetchService";
    public static final String NEW_ORDER_FETCH_SERVICE_MESSAGE = "NEW_ORDER_FETCH_SERVICE_MESSAGE";
    public static final String INTENT_EXTRA_INPUT_LISTED_ORDERS = "INTENT_EXTRA_INPUT_LISTED_ORDERS";
    public static final String INTENT_EXTRA_OUTPUT_NEW_ORDERS   = "INTENT_EXTRA_OUTPUT_NEW_ORDERS";
    List<String> listedOrderIds = new ArrayList<>();
    boolean isLoading = false;
    private static final long ORDER_FETCH_INTERVAL = 1000 * 5;

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

        startService();

//        Notification notification = createNotification();
//        startForeground(1, notification);

        //do heavy work in a background thread
        //stopSelf()

        // by returning this we make sure the service is restarted if the system kills the service
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
        Notification notification = createNotification();

        startForeground(1, notification);
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
        Intent restartServiceIntent  = new Intent(getApplicationContext(), NewOrderFetchService.class);
        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(this, 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        AlarmManager alarmService =(AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() +1000, restartServicePendingIntent);
    }

    private void startService(){
        if(isServiceStarted) return;
        Log.d(TAG, "Starting the foreground service task");
        Toast.makeText(this, "Service starting its task", Toast.LENGTH_SHORT).show();
        isServiceStarted = true;
        ServiceTracker.setServiceState(this, ServiceTracker.ServiceState.STARTED);

        // we need this lock so our service gets not affected by Doze Mode
        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        //wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "whatever");
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "NewOrderFetchService::lock");
        wakeLock.acquire();

        // we're starting a loop in a coroutine
        doInBackground();
    }
    private void stopService(){
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

    private Notification createNotification(){
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, App.CHANNEL_ID_NEW_ORDER_FETCH_SERVICE)
                .setContentTitle("Fetching New Order")
                .setContentText("Fetching....")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
                .setTicker("Ticker Text")
                .setPriority(Notification.PRIORITY_HIGH)// for under android 26 compatibility
                .setChannelId(App.CHANNEL_ID_NEW_ORDER_FETCH_SERVICE)
                .setAutoCancel(false)
                .build();

        return notification;

    }

    private void doInBackground() {
        User user = UserSession.getUserData(this);
        Timer timer = new Timer();

        if(user != null){
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    //NewOrderRequest newOrderRequest = new NewOrderRequest(user.getId(), listedOrderIds);
                    //NewOrderRequest newOrderRequest = new NewOrderRequest(userId, new ArrayList<>());
                    while (isServiceStarted){
                        if(!isLoading){
                            //fetchNewOrders(newOrderRequest);
                        }
                    }
                }
            }, 0, ORDER_FETCH_INTERVAL);
        }else{
            Log.d(TAG, "USER IS NULL");
            Toast.makeText(this, "USER IS NULL", Toast.LENGTH_SHORT).show();
        }
    }


    private void createFullScreenNotification(String orders){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, App.CHANNEL_ID_NEW_ORDER);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            Intent fullScreenIntent = new Intent(this, MainActivity.class);
            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(getApplicationContext());
            taskStackBuilder.addNextIntentWithParentStack(fullScreenIntent);
            //fullScreenIntent.putExtra(Constants.NOTIFICATION_IDS, notificationId);
            fullScreenIntent.putExtra(INTENT_EXTRA_OUTPUT_NEW_ORDERS, orders);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            builder.setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("Random notification title")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_CALL)
                    .setFullScreenIntent(pendingIntent, true)
                    .setAutoCancel(true)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setOngoing(true);
            Notification notification = builder.build();

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.notify(App.NOTIFICATION_ID_NEW_ORDER, notification);
        }else{
            Intent intent = new Intent(this, MainActivity.class);
            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(getApplicationContext());
            taskStackBuilder.addNextIntentWithParentStack(intent);
            intent.putExtra(INTENT_EXTRA_OUTPUT_NEW_ORDERS, orders);
            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);
        }
    }
    private void sendMessageToUI(String orders){
        Intent intent = new Intent(NEW_ORDER_FETCH_SERVICE_MESSAGE);
        Log.d(TAG, "SENDING: "+orders);
        intent.putExtra(INTENT_EXTRA_OUTPUT_NEW_ORDERS, orders);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG,  "Some component want to bind with the service");
        // We don't provide binding, so return null
        return null;
    }
}
