package com.pureeats.driverapp.services;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.MutableLiveData;

import com.pureeats.driverapp.R;
import com.pureeats.driverapp.api.ApiInterface;
import com.pureeats.driverapp.api.ApiService;
import com.pureeats.driverapp.commons.Actions;
import com.pureeats.driverapp.models.Order;
import com.pureeats.driverapp.models.request.DeliveryGuySetGpsRequest;
import com.pureeats.driverapp.models.request.RequestToken;
import com.pureeats.driverapp.models.response.DeliveryOrderResponse;
import com.pureeats.driverapp.sharedprefs.ServiceTracker;
import com.pureeats.driverapp.views.App;
import com.pureeats.driverapp.views.MainActivity;
import com.pureeats.driverapp.views.order.ProcessOrderActivityDialog;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FetchOrderService extends LifecycleService {
    private static final String TAG = "FetchOrderService";
    private static final long ORDER_FETCH_INTERVAL = 1000 * 10;
    private boolean isLoading = false;

    public static MutableLiveData<Boolean> isFetching = new MutableLiveData<>(false);
    public static MutableLiveData<List<Order>> mutableAcceptedOrders = new MutableLiveData<>(new ArrayList<>());
    public static MutableLiveData<List<Order>> mutableNewOrders = new MutableLiveData<>(new ArrayList<>());
    public static List<Order> processedOrders = new ArrayList<>();


    private FusedLocationProviderClient mFusedLocationClient;
    LocationCallback mLocationCallback;
    private List<Location> locationList = new ArrayList<>();
    public static MutableLiveData<List<Location>> mutableLocations  = new MutableLiveData<>(new ArrayList<>());

    RequestToken requestToken = null;
    Timer timer = null;


    private PowerManager.WakeLock wakeLock;
    private boolean isServiceStarted = false;

    private void setLocation(Location location){
        if(mutableLocations == null){
            mutableLocations = new MutableLiveData<>(new ArrayList<>());
        }
        locationList.add(location);

        mutableLocations.postValue(locationList);

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        DeliveryGuySetGpsRequest deliveryGuySetGpsRequest = new DeliveryGuySetGpsRequest(requestToken, latLng);
        new Handler().postDelayed(() -> saveUserGpsLocation(deliveryGuySetGpsRequest), 15 * 1000);

    }


    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
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
        return super.onStartCommand(intent, flags, startId);
    }



    private void startService() {
        if(isServiceStarted) return;
        Log.d(TAG, "Starting the foreground service task");
        Toast.makeText(this, "Service starting its task", Toast.LENGTH_SHORT).show();
        isServiceStarted = true;
        ServiceTracker.setServiceState(this, ServiceTracker.ServiceState.STARTED);
        getLocationUpdates();

        // we need this lock so our service gets not affected by Doze Mode
        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        //wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "whatever");
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "NewOrderFetchService::lock");
        wakeLock.acquire();

        // we're starting a loop in a coroutine
        doInBackground();
        Log.d(TAG, "End of the loop for the service");
    }

    private void stopService() {
        Log.d(TAG, "Stopping the foreground service");
        Toast.makeText(this, "Service stopping", Toast.LENGTH_SHORT).show();

        try{
            if(wakeLock != null){
                if(wakeLock.isHeld()){
                    wakeLock.release();
                }
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
        requestToken = new RequestToken(this);
        timer = new Timer();
        Notification notification = createNotification();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                //Log.d(TAG, "onLocationResult:  " + locationResult.getLastLocation());
                setLocation(locationResult.getLastLocation());
                if(locationResult == null){
                    //Log.d(TAG, "onLocationResult: location error");
                    return;
                }
                List<Location> locations = locationResult.getLocations();
                for(Location location : locationResult.getLocations()){
                    //Log.d(TAG, "onLocationResult: " + location.toString());
                }
            }
        };

        startForeground(App.NOTIFICATION_ID_NEW_ORDER, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        Log.d(TAG, "onDestroy()");
        Toast.makeText(this, "Service destroyed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.d(TAG, "TASK REMOVED..............");

        Intent restartServiceIntent  = new Intent(getApplicationContext(), FetchOrderService.class);
        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(this, 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        AlarmManager alarmService =(AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() +1000, restartServicePendingIntent);

//        PendingIntent service = PendingIntent.getService(
//                getApplicationContext(),
//                1001,
//                new Intent(getApplicationContext(), EndlessService.class),
//                PendingIntent.FLAG_ONE_SHOT);
//
//        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, service);
    }


    private Notification createNotification(){
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, App.CHANNEL_ID_NEW_ORDER);
        builder.setContentTitle("Fetching New Order")
                .setContentText("Fetching....")
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(false)// clear notification after click
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setChannelId(App.CHANNEL_ID_NEW_ORDER)
                .setOngoing(true);
        Notification notification = builder.build();


        Log.d(TAG, "Opening DialogActivity from Notification......");
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.notify(App.NOTIFICATION_ID_NEW_ORDER, notification);

        return notification;

    }

    private void doInBackground(){
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //NewOrderRequest newOrderRequest = new NewOrderRequest(userId, new ArrayList<>());
                if(!isLoading && !ProcessOrderActivityDialog.isActivityOpen){
                    getDeliveryOrders(requestToken);
                }
            }
        }, 0, ORDER_FETCH_INTERVAL);
    }

    private void getDeliveryOrders(RequestToken requestToken){
        isLoading = true;
        ApiInterface apiInterface = ApiService.getApiService();
        Log.d(TAG, "FETCHING NEW ORDER........");
        apiInterface.getAllDeliveryOrders(requestToken).enqueue(new Callback<DeliveryOrderResponse>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<DeliveryOrderResponse> call, Response<DeliveryOrderResponse> response) {
                try{
                    DeliveryOrderResponse responseObj = response.body();
                    if(responseObj != null){
                        //Log.d(TAG, "RESPONSE: " + responseObj);
                        List<Order> newOrders = responseObj.getNewOrders();
                        List<Order> acceptedOrders = responseObj.getAcceptedOrders();
                        if(newOrders .size() > 0){
                            Log.d(TAG, "##########################NEW_ORDER_ARRIVED#########################################");
                            newOrders.forEach(order -> System.out.println("ORDER: "+ order.getId() + ", "+order.getUniqueOrderId()));
                            Log.d(TAG,"#####################################################################################");
                            if(!ProcessOrderActivityDialog.isActivityOpen){
                                // Check whether the order is already processed or not
                                boolean isOrderAlreadyProcessed = processedOrders.stream().anyMatch(processedOrder -> newOrders.get(0).getId() == processedOrder.getId());
                                if(!isOrderAlreadyProcessed){
                                    List<Order> orderList = Collections.singletonList(newOrders.get(0));
                                    mutableNewOrders.postValue(orderList);
                                    showFullScreenOrderArriveNotification();
                                }
                            }
                        }

                        if(mutableAcceptedOrders.getValue().size() != acceptedOrders.size()){
                            mutableAcceptedOrders.postValue(acceptedOrders);
                        }
                    }
                    isLoading = false;
                }catch (Exception e){
                    e.printStackTrace();
                    isLoading = false;
                }
            }

            @Override
            public void onFailure(Call<DeliveryOrderResponse> call, Throwable t) {
                Log.d(TAG, "FAIL");
                isLoading = false;
            }
        });
    }


    private void showFullScreenOrderArriveNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, App.CHANNEL_ID_PUSH_NOTIFICATION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            Intent fullScreenIntent = new Intent(this, ProcessOrderActivityDialog.class);
            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(getApplicationContext());
            taskStackBuilder.addNextIntentWithParentStack(fullScreenIntent);
            fullScreenIntent.setAction(Actions.ACCEPT_ORDER_FRAGMENT.name());
            //fullScreenIntent.putExtra(Constants.NOTIFICATION_IDS, notificationId);
            //fullScreenIntent.putExtra(INTENT_EXTRA_ORDER_STATUS, orderJson);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            builder.setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle("New Order Arrive")
                    .setContentText("A new order is arrived, please accept the order")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_CALL)
                    .setFullScreenIntent(pendingIntent, true)
                    .setAutoCancel(true)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setOngoing(true);
            Notification notification = builder.build();

            Log.d(TAG, "Opening DialogActivity from Notification......");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.notify(App.NOTIFICATION_ID_PUSH_NOTIFICATION, notification);
        }else{
            Intent intent = new Intent(this, ProcessOrderActivityDialog.class);
            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(getApplicationContext());
            taskStackBuilder.addNextIntentWithParentStack(intent);
            //intent.putExtra(INTENT_EXTRA_ORDER_STATUS, orderJson);
            intent.setAction(Actions.ACCEPT_ORDER_FRAGMENT.name());
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            Log.d(TAG, "Starting DialogActivity......");
            startActivity(intent);
        }
    }


    private void getLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setMaxWaitTime(15 * 1000);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            stopService();
            return;
        }
        mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.getMainLooper());
    }



    private void saveUserGpsLocation(DeliveryGuySetGpsRequest gpsRequest){
        isLoading = true;
        ApiInterface apiInterface = ApiService.getApiService();
        Log.d(TAG, "Saving user location.........");
        Log.d(TAG, "REQUEST: "+ new Gson().toJson(gpsRequest));
        apiInterface.setDeliveryGuyGpsLocation(gpsRequest).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                isLoading = false;
                if(response.isSuccessful()){
                    Log.d(TAG, "RESPONSE: Success");
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                isLoading  = false;
                Log.d(TAG, "FAIL");
            }
        });
    }





}
