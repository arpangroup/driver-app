package com.pureeats.driverapp.services;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.common.util.CollectionUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.pureeats.driverapp.R;
import com.pureeats.driverapp.commons.Actions;
import com.pureeats.driverapp.commons.NotificationSoundType;
import com.pureeats.driverapp.models.Order;
import com.pureeats.driverapp.models.request.DeliveryGuySetGpsRequest;
import com.pureeats.driverapp.models.request.RequestToken;
import com.pureeats.driverapp.models.response.HeartBeatResponse;
import com.pureeats.driverapp.network.api.Api;
import com.pureeats.driverapp.network.datasource.RemoteDataSource;
import com.pureeats.driverapp.receivers.OrderArrivedReceiver;
import com.pureeats.driverapp.sharedprefs.ServiceTracker;
import com.pureeats.driverapp.sharedprefs.UserSession;
import com.pureeats.driverapp.utils.CommonUiUtils;
import com.pureeats.driverapp.utils.CommonUtils;
import com.pureeats.driverapp.utils.GpsUtils;
import com.pureeats.driverapp.views.App;
import com.pureeats.driverapp.views.MainActivity;
import com.pureeats.driverapp.views.order.DialogActivity;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EndlessService extends Service {
    //https://robertohuertas.com/2019/06/29/android_foreground_services/
    private static final String TAG = "EndlessService";
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private static final long HEART_BEAT_INTERVAL = 1000 *5;// 1min
    private Timer timer, ringtoneTimer;
    private boolean isMusicEnable = true;
    private static MediaPlayer mMediaPlayer;
    private static SoundPool mSoundPool;
    private int ORDER_ARRIVED_SOUND;
    private int ORDER_CANCELLED_SOUND;
    private int LOOP_INDEFINITE = -1; // -1: Infinite; 2: 2 times, 3: 3 times; 0: no loop

    /**
     * Usesd to stop the sound/sytream
     */
    private static int ORDER_ARRIVED_STREAM_ID;


    private RequestToken requestToken;
    private static LatLng lastLocation = null;

    private PowerManager.WakeLock wakeLock;//our service never gets affected by Doze Mode
    private boolean isServiceStarted = false;

    private static MutableLiveData<List<Order>> ongoingOrderList = new MutableLiveData<>();


    private List<Order> acceptedOrderList = new ArrayList<>();
    private List<Order> newOrderList = new ArrayList<>();
    private static List<Order> pickedUpOrderList = new ArrayList<>();
    private void pushNewOrder(Order newOrder){
        Log.d(TAG, "Inside pushOrder.....");
        boolean isNewOrder = newOrderList.stream().noneMatch(order -> order.getId() == newOrder.getId());
        if (isNewOrder){
            newOrderList.add(newOrder);
            //showFullScreenOrderArriveNotification(newOrder);
            sendBroadcast(OrderArrivedReceiver.getBroadcastIntent(this, Actions.NEW_ORDER_ARRIVED, newOrder));
        }
    }
    private void pushOngoingOrder(Order upcomingOrder){
        Log.d(TAG, "Inside pushOngoingOrder");
        List<Order> existingOrders = ongoingOrderList.getValue();
        if(CollectionUtils.isEmpty(existingOrders)){
            existingOrders = new ArrayList<>();
        }
        boolean isExist = existingOrders.stream().anyMatch(order -> order.getId() == upcomingOrder.getId());
        if(!isExist){
            existingOrders.add(upcomingOrder);
        }
        Log.d(TAG, "post order to live data......");
        ongoingOrderList.setValue(existingOrders);
    }
    public static LiveData<List<Order>> getOngoingOrders(){
        Log.d(TAG, "getOngoingOrders..................");
        Log.d(TAG, "RETURNING: " + ongoingOrderList.getValue());
        return ongoingOrderList;
    }

    private void handleStatusChanged(List<Order> upcomingOrders){
        //check if any accepted order is transfer to other DeliveryGuy:
        // i.e., the order which is present in ongoing orders, but not present in acceptedOrders
        Log.d(TAG, "handleStatusChanged");
        if(ongoingOrderList == null || CollectionUtils.isEmpty(ongoingOrderList.getValue())) return;


        ongoingOrderList.getValue().forEach(existingOrder ->{
            boolean isPresent = upcomingOrders.stream().anyMatch(u -> u.getId() == existingOrder.getId());
            if(!isPresent){ // if the ongoing order is not present in upcoming acceptedOrderList
                // remove the order from the ongoing order list:
                CommonUtils.displayNotification(this, "Order cancelled", "Order is transfered to other delivery guy or it cancelled by the customer");
                List<Order> existingOrders = new ArrayList<>(ongoingOrderList.getValue());
                existingOrders.removeIf(order -> order.getId() == existingOrder.getId());
                ongoingOrderList.setValue(existingOrders);
            }
        });



    }





    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand executed with startId: " + startId);
        if(intent  != null){
            String action = intent.getAction();
            Log.d(TAG, "Using an Intent with action "+action);
            if(action != null){
                if(action.equals(Actions.START.name()))startService();
                else if(action.equals(Actions.STOP.name()))stopService();
                else if(action.equals(Actions.NEW_ORDER_ARRIVED.name())) startMediaPlayer(NotificationSoundType.ORDER_ARRIVE);
                else if(action.equals(Actions.ORDER_CANCELLED.name())) startMediaPlayer(NotificationSoundType.ORDER_CANCELED);
                else if(action.equals(Actions.DISMISS_ORDER_NOTIFICATION.name())) stopMediaPlayer();
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
        isServiceStarted = true;
        ServiceTracker.setServiceState(this, ServiceTracker.ServiceState.STARTED);
        getLocationUpdates();

        // Start the Location Service:

        // we need this lock so our service gets not affected by Doze Mode
        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        //wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "whatever");
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "NewOrderFetchService::lock");
        wakeLock.acquire(60 * 1000L /*1 minutes*/);

        // we're starting a loop in a coroutine
        doInBackground();
        Log.d(TAG, "End of the loop for the service");
    }

    private void doInBackground(){
        Log.d(TAG, "doInBackground.......");
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                scheduleHeartBeat(lastLocation);
            }
        }, 0, HEART_BEAT_INTERVAL);
    }




    private void stopService() {
        Log.d(TAG, "Stopping the foreground service");
        try{
            if(wakeLock != null && wakeLock.isHeld()){
                wakeLock.release();
            }
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
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
        requestToken = new UserSession(getApplicationContext()).getRequestToken();
        Log.d(TAG, "REQUEST_TOKEN: " + "user_id: "+ requestToken.getUserId() + ", token: " +requestToken.getToken());
        timer = new Timer();
        initSoundPools();
        startForeground(App.NOTIFICATION_CHANNEL_ID_RUNNING_ORDER, createNotification());
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult != null && locationResult.getLastLocation() != null) {
                    double lat = locationResult.getLastLocation().getLatitude();
                    double lng = locationResult.getLastLocation().getLongitude();
                    lastLocation = new LatLng(lat, lng);
                    Log.d(TAG, "LATLNG: " + lastLocation);
                }
            }
        };


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        timer = null;
        mSoundPool.release();
        mSoundPool = null;
        stopForeground(true);
        Log.d(TAG, "onDestroy()");
        Toast.makeText(this, "Service destroyed", Toast.LENGTH_SHORT).show();

        //Intent broadcastIntent = new Intent(this, RestartBroadcastReceiver.class);
        //sendBroadcast(broadcastIntent);

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
                .setContentText("Tap for more options")
                .setSmallIcon(R.drawable.ic_baseline_sync_24)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setChannelId(App.CHANNEL_ID_NEW_ORDER)
                .setOngoing(true);
        Notification notification = builder.build();


        Log.d(TAG, "Opening DialogActivity from Notification......");
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.notify(App.NOTIFICATION_CHANNEL_ID_RUNNING_ORDER, notification);

        return notification;

    }



    private void scheduleHeartBeat(LatLng latLng){
        Log.d(TAG, "SCHEDULE_HEARTBEAT: "+latLng);
        if (latLng == null)return;
        Api apiInterface = RemoteDataSource.buildApiWithoutInterceptor(Api.class);

        DeliveryGuySetGpsRequest heartbeatRequest = new DeliveryGuySetGpsRequest(requestToken, latLng);
        apiInterface.scheduleHeartbeat(heartbeatRequest).enqueue(new Callback<HeartBeatResponse>() {
            @Override
            public void onResponse(Call<HeartBeatResponse> call, Response<HeartBeatResponse> response) {
                if(response.isSuccessful()){
                   handleHeartbeatResponse(response.body());
                }else {
                    Log.d(TAG, "ERROR_RESPONSE");
                }
            }

            @Override
            public void onFailure(Call<HeartBeatResponse> call, Throwable t) {
                Log.d(TAG, "FAIL_RESPONSE");
            }
        });
    }

    private void handleHeartbeatResponse(HeartBeatResponse heartBeatResponse){
        if (heartBeatResponse == null) return;
        System.out.println("#####################################################");
        System.out.println("NEW_ORDERS: " + heartBeatResponse.getNewOrders());
        System.out.println("ACCEPTED_ORDERS: " + heartBeatResponse.getAcceptedOrders());
        System.out.println("PICKEDUP_ORDERS: " + heartBeatResponse.getPickedupOrders());
        System.out.println("#####################################################");
        heartBeatResponse.getNewOrders().forEach(this::pushNewOrder);
        heartBeatResponse.getAcceptedOrders().forEach(this::pushOngoingOrder);
        heartBeatResponse.getPickedupOrders().forEach(this::pushOngoingOrder);
        handleStatusChanged(heartBeatResponse.getAcceptedOrders());


    }





    private void getLocationUpdates() {
        Log.d(TAG, "getLocationUpdates().......");
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000 * 4); // 60  second
        locationRequest.setFastestInterval(1000 * 2); // 30 second
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setMaxWaitTime(15 * 1000);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Location permission not available", Toast.LENGTH_LONG).show();;
            stopService();
            return;
        }
        mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.getMainLooper());
    }

    private void showFullScreenOrderArriveNotification(Order order) {
//        startMediaPlayer(NotificationSoundType.ORDER_ARRIVE);
        ORDER_ARRIVED_STREAM_ID = mSoundPool.play(ORDER_ARRIVED_SOUND, 0.1f, 0.1f, 0, LOOP_INDEFINITE, 1);
        System.out.println("######## PlayingOrderArrivedSoundInStreamId: " + ORDER_ARRIVED_STREAM_ID);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, App.CHANNEL_ID_PUSH_NOTIFICATION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            Intent fullScreenIntent = new Intent(this, DialogActivity.class);
            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(getApplicationContext());
            taskStackBuilder.addNextIntentWithParentStack(fullScreenIntent);
            fullScreenIntent.setAction(Actions.ACCEPT_ORDER_FRAGMENT.name());
            //fullScreenIntent.putExtra(Constants.NOTIFICATION_IDS, notificationId);
            fullScreenIntent.putExtra(DialogActivity.INTENT_EXTRA_ORDER, new Gson().toJson(order));
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            builder.setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle("New Order Arrive")
                    .setContentText("Order # " + order.getUniqueOrderId())
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_CALL)
                    .setFullScreenIntent(pendingIntent, true)
                    .setAutoCancel(true)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setOngoing(true);
            Notification notification = builder.build();

            Log.d(TAG, "Opening DialogActivity from Notification......");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.notify(App.NOTIFICATION_CHANNEL_ID_NEW_ORDER, notification);
        }else{
            Intent intent = new Intent(this, DialogActivity.class);
            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(getApplicationContext());
            taskStackBuilder.addNextIntentWithParentStack(intent);
            intent.putExtra(DialogActivity.INTENT_EXTRA_ORDER, new Gson().toJson(order));
            //intent.putExtra(INTENT_EXTRA_ORDER_STATUS, orderJson);
            intent.setAction(Actions.ACCEPT_ORDER_FRAGMENT.name());
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            Log.d(TAG, "Starting DialogActivity......");
            startActivity(intent);
        }
    }


    private void initSoundPools(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            mSoundPool = new SoundPool.Builder()
                    .setMaxStreams(5)
                    .setAudioAttributes(audioAttributes)
                    .build();
        }else{
            mSoundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }

        ORDER_ARRIVED_SOUND = mSoundPool.load(this, R.raw.order_arrived_ringtone, 1);
        ORDER_CANCELLED_SOUND = mSoundPool.load(this, R.raw.swiggy_order_cancel_ringtone, 2);

    }

    private void startMediaPlayer(NotificationSoundType soundType) {
        mMediaPlayer = new MediaPlayer();
        Context context = this;
        if(soundType == NotificationSoundType.ORDER_ARRIVE)mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.order_arrived_ringtone);
        else if(soundType == NotificationSoundType.ORDER_CANCELED)mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.swiggy_order_cancel_ringtone);
        else mMediaPlayer = MediaPlayer.create(context, R.raw.default_notification_sound);

        if(soundType == NotificationSoundType.ORDER_ARRIVE){
            ringtoneTimer = new Timer();
            ringtoneTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if(isMusicEnable){
                        try{
                            mMediaPlayer.start();
                        }catch (Exception e){
                            //e.printStackTrace();
                        }
                    }
                }
            }, 0, mMediaPlayer.getDuration());

        }else{
            try{
                mMediaPlayer.start();
            }catch (Exception e){
                //e.printStackTrace();
            }
        }

    }
    private void stopMediaPlayer(){
       try {
           if(mMediaPlayer != null){
               mMediaPlayer.stop();
               mMediaPlayer.release();
               mMediaPlayer = null;
           }
       }catch (Throwable t){}
    }

    private static void stopOrderArrivedTone(){
        Log.d(TAG, "stopOrderArrivedTone..." + ORDER_ARRIVED_STREAM_ID);
       try {
           mSoundPool.autoPause();
           mSoundPool.stop(ORDER_ARRIVED_STREAM_ID);
       }catch (Throwable t){
        t.printStackTrace();
       }
    }

    public static void playRingtone(Context context, Actions action){
        Intent intent = new Intent(context, EndlessService.class);
        intent.setAction(action.name());
        context.startService(intent);
    }
    public static void stopRingtone(Context context){
        Intent intent = new Intent(context, EndlessService.class);
        intent.setAction(Actions.DISMISS_ORDER_NOTIFICATION.name());
        context.startService(intent);
    }

    public static LatLng getLastLocation(){
        return lastLocation;
    }



}
