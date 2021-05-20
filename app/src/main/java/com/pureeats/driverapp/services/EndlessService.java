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
import com.pureeats.driverapp.models.Order;
import com.pureeats.driverapp.models.request.HeartbeatRequest;
import com.pureeats.driverapp.models.request.RequestToken;
import com.pureeats.driverapp.models.response.HeartBeatResponse;
import com.pureeats.driverapp.network.api.Api;
import com.pureeats.driverapp.network.datasource.RemoteDataSource;
import com.pureeats.driverapp.receivers.OrderSyncBroadcastReceiver;
import com.pureeats.driverapp.sharedprefs.ServiceTracker;
import com.pureeats.driverapp.sharedprefs.UserSession;
import com.pureeats.driverapp.views.App;
import com.pureeats.driverapp.views.MainActivity;

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
    private static int HEARTBEAT_COUNT = 0;
    private App app;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private static final long HEART_BEAT_INTERVAL = 1000 *5;// 1min
    private Timer timer, ringtoneTimer;
    private boolean isMusicEnable = true;

    private RequestToken requestToken;
    private static LatLng lastLocation = null;

    private PowerManager.WakeLock wakeLock;//our service never gets affected by Doze Mode
    private boolean isServiceStarted = false;

    private static MutableLiveData<List<Order>> ongoingOrderList = new MutableLiveData<>();
    private static MutableLiveData<Integer> countAcceptedOrders = new MutableLiveData<>(0);


    private List<Order> acceptedOrderList = new ArrayList<>();
    private List<Order> newOrderList = new ArrayList<>();
    private List<Order> cancelledOrderList = new ArrayList<>();
    private static List<Order> pickedUpOrderList = new ArrayList<>();
    private void pushNewOrder(Order newOrder){
        //Log.d(TAG, "Inside pushOrder.....");
        boolean isNewOrder = newOrderList.stream().noneMatch(order -> order.getId() == newOrder.getId());
        if (isNewOrder){
            newOrderList.add(newOrder);
            Intent broadcastIntent = OrderSyncBroadcastReceiver.getIntent(getApplicationContext(), Actions.ORDER_ARRIVED, newOrder.getId(), newOrder.getUniqueOrderId());
            sendBroadcast(broadcastIntent);
        }
    }
    private void pushCancelledOrder(Order cancelledOrder){
        Log.d(TAG, "Inside pushOrder.....");
        boolean isAlreadyCancelled = cancelledOrderList.stream().anyMatch(order -> order.getId() == cancelledOrder.getId());
        if (!isAlreadyCancelled){
            cancelledOrderList.add(cancelledOrder);
            newOrderList.removeIf(order -> order.getId() == cancelledOrder.getId());

            //sendBroadcast(OrderArrivedReceiver.getBroadcastIntent(this, Actions.ORDER_CANCELLED, cancelledOrder));

            Intent broadcastIntent = new Intent(getApplicationContext(), OrderSyncBroadcastReceiver.class);
            broadcastIntent.setAction(Actions.ORDER_CANCELLED.name());
            broadcastIntent.putExtra("extra_order", new Gson().toJson(cancelledOrder));
            Log.d(TAG, "SEND_BROADCAST: " + Actions.ORDER_CANCELLED.name() + " :: OrderID: " + cancelledOrder.getId());
            sendBroadcast(broadcastIntent);

        }
    }
    private void pushOngoingOrder(Order upcomingOrder){
        //Log.d(TAG, "Inside pushOngoingOrder");
        List<Order> existingOrders = ongoingOrderList.getValue();
        if(CollectionUtils.isEmpty(existingOrders)){
            existingOrders = new ArrayList<>();
        }
        boolean isExist = existingOrders.stream().anyMatch(order -> order.getId() == upcomingOrder.getId());
        if(!isExist){
            existingOrders.add(upcomingOrder);
        }
        //Log.d(TAG, "post order to live data......");
        ongoingOrderList.setValue(existingOrders);
    }
    public static LiveData<Integer> getOngoingOrders(){
        if(countAcceptedOrders == null) countAcceptedOrders = new MutableLiveData<>(0);
        return countAcceptedOrders;
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
                //else if(action.equals(Actions.NEW_ORDER_ARRIVED.name())) startMediaPlayer(NotificationSoundType.ORDER_ARRIVE);
//                else if(action.equals(Actions.NEW_ORDER_ARRIVED.name())) {
//                    app.playOrderArrivedTone(intent.getIntExtra("order_id", 0));
//                }
                else if(action.equalsIgnoreCase(Actions.ORDER_CANCELLED.name()) || action.equalsIgnoreCase(Actions.DISMISS_ORDER_NOTIFICATION.name())) {
                    app.stopOrderArrivedRingTone(intent.getIntExtra("order_id", 0));
                }
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
        app = App.getInstance();
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
                scheduleHeartBeat(lastLocation, newOrderList);
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

            if(timer != null){
                timer.cancel();
            }
            app.clearAllArrivedOrderNotification();
            HEARTBEAT_COUNT = 0;
            countAcceptedOrders.setValue(0);
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
        HEARTBEAT_COUNT = 0;
        requestToken = new UserSession(getApplicationContext()).getRequestToken();
        Log.d(TAG, "REQUEST_TOKEN: " + "user_id: "+ requestToken.getUserId() + ", token: " +requestToken.getToken());
        timer = new Timer();
        app = App.getInstance();
        startForeground(App.NOTIFICATION_CHANNEL_ID_RUNNING_ORDER, createNotification());
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());

        startLocationUpdate();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        HEARTBEAT_COUNT = 0;
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        timer = null;
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

    private void startLocationUpdate(){
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

    private Notification createNotification(){
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, App.CHANNEL_ID_SYNC_ORDER);
        builder.setContentTitle("Fetching New Order")
                .setContentText("Tap for more options")
                .setSmallIcon(R.drawable.ic_baseline_sync_24)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false)
                .setChannelId(App.CHANNEL_ID_SYNC_ORDER)
                .setOngoing(true);
        Notification notification = builder.build();


        Log.d(TAG, "Opening DialogActivity from Notification......");
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.notify(App.NOTIFICATION_CHANNEL_ID_RUNNING_ORDER, notification);


        return notification;

    }



    private void scheduleHeartBeat(LatLng latLng, List<Order> processedOrders){
        //Log.d(TAG, "SCHEDULE_HEARTBEAT: "+latLng);
        //if (latLng == null)return;
        Api apiInterface = RemoteDataSource.buildApiWithoutInterceptor(Api.class);

        HeartbeatRequest heartbeatRequest = new HeartbeatRequest(requestToken, latLng);
        heartbeatRequest.setCount(++HEARTBEAT_COUNT);
        if(!CollectionUtils.isEmpty(processedOrders)) heartbeatRequest.addProcessdOrders(processedOrders);
        Log.d(TAG, "HEARTBEAT_REQUEST"+ HEARTBEAT_COUNT + " : Latlng: "+ latLng + ", processed_orders: "+heartbeatRequest.getProcessingOrders());
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
//        Log.d(TAG, "RESPONSE: " + heartBeatResponse.getCountAcceptedOrders());
//        System.out.println("#####################################################");
//        System.out.println("NEW_ORDERS: " + heartBeatResponse.getNewOrders());
//        System.out.println("ACCEPTED_ORDERS: " + heartBeatResponse.getAcceptedOrders());
//        System.out.println("PICKEDUP_ORDERS: " + heartBeatResponse.getPickedupOrders());
//        System.out.println("#####################################################");
        if(HEARTBEAT_COUNT > 1)countAcceptedOrders.setValue(heartBeatResponse.getOnGoingDeliveriesCount());
        heartBeatResponse.getNewOrders().forEach(this::pushNewOrder);
        heartBeatResponse.getCancelledOrders().forEach(this::pushCancelledOrder);

        List<Order> processedOrders  = new ArrayList<>();
        processedOrders.addAll(heartBeatResponse.getAcceptedOrders());
        processedOrders.addAll(heartBeatResponse.getPickedupOrders());
        processedOrders.forEach(this::pushOngoingOrder);
        if(CollectionUtils.isEmpty(processedOrders)) ongoingOrderList.setValue(new ArrayList<>());
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


    public static void playOrderArrivedRingtone(Context context, int orderId, Actions action){
        Intent intent = new Intent(context, EndlessService.class);
        intent.setAction(action.name());
        intent.putExtra("order_id", orderId);
        context.startService(intent);
    }
    public static void stopOrderArrivedRingtone(Context context, int orderId){
        Intent intent = new Intent(context, EndlessService.class);
        intent.setAction(Actions.DISMISS_ORDER_NOTIFICATION.name());
        intent.putExtra("order_id", orderId);
        context.startService(intent);
    }
    public static LatLng getLastLocation(){
        return lastLocation;
    }



}
