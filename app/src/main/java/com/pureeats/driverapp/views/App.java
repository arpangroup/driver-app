package com.pureeats.driverapp.views;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.pureeats.driverapp.R;
import com.pureeats.driverapp.commons.Actions;
import com.pureeats.driverapp.commons.Constants;
import com.pureeats.driverapp.models.Order;
import com.pureeats.driverapp.sharedprefs.UserSession;
import com.pureeats.driverapp.utils.CommonUtils;
import com.pureeats.driverapp.views.order.DialogActivity;

import java.util.HashMap;
import java.util.Map;

public class App extends Application {
    private final String TAG = "Application";
    private static App mInstance;
    UserSession userSession;


    private static SoundPool mSoundPool;
    private int ORDER_ARRIVED_SOUND;
    private int ORDER_CANCELLED_SOUND;
    private int LOOP_INDEFINITE = -1; // -1: Infinite; 2: 2 times, 3: 3 times; 0: no loop
    private static HashMap<Integer, Integer> orderIdStreamIdMap = new HashMap<>();// Usesd to stop the sound/sytream

    public static final String CHANNEL_ID_SYNC_ORDER = "sync order";
    public static final String CHANNEL_ID_NEW_ORDER = "channel_new_orders";
    public static final String CHANNEL_ID_PUSH_NOTIFICATION = "channel_push_notifications";
    public static final String CHANNEL_ID_NEW_ORDER_FETCH_SERVICE = "channel_new_order_fetch_service";
    public static final String CHANNEL_NAME_NEW_ORDER = "channel new orders";
    public static final String CHANNEL_NAME_SYNC_ORDER = "Sync Order Channel";
    public static final String CHANNEL_NAME_PUSH_NOTIFICATION = "channel push notifications";
    public static final String CHANNEL_NAME_NEW_ORDER_FETCH_SERVICE = "channel new order fetch service";
    public static final int NOTIFICATION_CHANNEL_ID_RUNNING_ORDER = 600;
    public static final int NOTIFICATION_CHANNEL_ID_NEW_ORDER = 601;
    public static final int NOTIFICATION_CHANNEL_ID_PUSH_NOTIFICATION = 602;

    public static final Intent[] POWERMANAGER_INTENTS = {
            new Intent().setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")),
            new Intent().setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity")),
            new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity")),
            new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")),
            new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.appcontrol.activity.StartupAppControlActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity")),
            new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager")),
            new Intent().setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity")),
            new Intent().setComponent(new ComponentName("com.samsung.android.lool", "com.samsung.android.sm.ui.battery.BatteryActivity")),
            new Intent().setComponent(new ComponentName("com.htc.pitroad", "com.htc.pitroad.landingpage.activity.LandingPageActivity")),
            new Intent().setComponent(new ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.MainActivity"))
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        userSession = new UserSession(this);
        if(userSession.getPushToken() == null) generatePushNotificationToken();
        else{
            System.out.println("############################## PUSH_TOKEN ###########################");
            System.out.println(userSession.getPushToken());
            System.out.println("#####################################################################");
        }
        createNotificationChannels();
        initSoundPools();
        if(orderIdStreamIdMap == null) orderIdStreamIdMap = new HashMap<>();
    }

    public static synchronized App getInstance(){
        return mInstance;
    }

    private void createNotificationChannels() {
        Uri sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.default_notification_sound);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Uri emergencySoundUri = Uri.parse("android.resource://"+getPackageName()+ "/raw/order_arrived_ringtone");


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannelSyncOrder = new NotificationChannel(
                    CHANNEL_ID_SYNC_ORDER,
                    CHANNEL_NAME_SYNC_ORDER,
                    NotificationManager.IMPORTANCE_HIGH
            );

            NotificationChannel notificationChannelNewOrder = new NotificationChannel(
                    CHANNEL_ID_NEW_ORDER,
                    CHANNEL_NAME_NEW_ORDER,
                    NotificationManager.IMPORTANCE_HIGH
            );

            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID_NEW_ORDER_FETCH_SERVICE,
                    CHANNEL_NAME_NEW_ORDER_FETCH_SERVICE,
                    NotificationManager.IMPORTANCE_HIGH
            );

            NotificationChannel notificationChannelPushNotification = new NotificationChannel(
                    CHANNEL_ID_PUSH_NOTIFICATION,
                    CHANNEL_NAME_PUSH_NOTIFICATION,
                    NotificationManager.IMPORTANCE_HIGH
            );

            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build();

            //notificationChannelNewOrder.setSound(emergencySoundUri, attributes);

            notificationChannelSyncOrder.setDescription("Sync Order Notification Channel");
            notificationChannelSyncOrder.enableLights(true);
            notificationChannelSyncOrder.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationChannelSyncOrder.setLightColor(Color.RED);
            notificationChannelSyncOrder.enableVibration(true);
            notificationChannelSyncOrder.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

            notificationChannelNewOrder.setDescription("This is New Order Notification Channel");
            notificationChannelNewOrder.enableLights(true);
            notificationChannelNewOrder.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationChannelNewOrder.setLightColor(Color.RED);
            notificationChannelNewOrder.enableVibration(true);
            notificationChannelNewOrder.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});


            notificationChannelPushNotification.setDescription("This is Push Notifications channel");
            notificationChannelPushNotification.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationChannelPushNotification.setDescription("This is New Order Fetch Service channel");
            notificationChannelPushNotification.enableLights(true);
            notificationChannelPushNotification.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            notificationChannelPushNotification.setLightColor(Color.RED);
            notificationChannelPushNotification.enableVibration(true);
            notificationChannelPushNotification.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

            serviceChannel.setDescription("This is New Order Fetch Service channel");
            serviceChannel.enableLights(true);
            serviceChannel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            serviceChannel.setLightColor(Color.RED);
            serviceChannel.enableVibration(true);
            serviceChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

            // Register the channels with Notification Framework
            //NotificationManager notificationManager = getSystemService(NotificationManager.class);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannelSyncOrder);
            manager.createNotificationChannel(notificationChannelNewOrder);
            manager.createNotificationChannel(notificationChannelPushNotification);
            manager.createNotificationChannel(serviceChannel);

        }
    }

    public void generatePushNotificationToken(){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();
                    if(token != null)userSession.savePushToken(token);
                    System.out.println("############################## PUSH_TOKEN ###########################");
                    System.out.println(token);
                    System.out.println("#####################################################################");
                });
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

    public void playOrderArrivedTone(int orderId){
        if (mSoundPool == null || orderIdStreamIdMap == null) return;;
       try{
           //mSoundPool.autoPause();
           if(orderIdStreamIdMap.get(orderId) != null)mSoundPool.pause(orderIdStreamIdMap.getOrDefault(orderId, 0));//if same orderid is already playing
           int streamId = mSoundPool.play(ORDER_ARRIVED_SOUND, 1f, 1f, 0, LOOP_INDEFINITE, 1);
           System.out.println("######## PlayingOrderArrivedSoundInStreamId: " + streamId + ", OrderId: " + orderId);
           orderIdStreamIdMap.put(orderId, streamId);
       }catch (Throwable t){
           t.printStackTrace();
       }
    }

    public void stopOrderArrivedRingTone(int orderId){
        Log.d(TAG, "######## stopOrderArrivedTone... OrderId: "+ orderId);
        orderIdStreamIdMap.forEach((k, v) -> System.out.println("Key : " + k + ", Value : " + v));
        try {
            int streamId = orderIdStreamIdMap.get(orderId);
            Log.d(TAG, "Stopping StreamID: " + streamId);
            //mSoundPool.autoPause();
            mSoundPool.stop(streamId);
            CommonUtils.cancelNotification(getApplicationContext(), orderId);
        }catch (Throwable t){
            t.printStackTrace();
        }
    }

    public void clearAllArrivedOrderNotification(){
        mSoundPool.autoPause();
        //mSoundPool.release();
        //mSoundPool = null;

        try{
            orderIdStreamIdMap.forEach((k, v) -> CommonUtils.cancelNotification(getApplicationContext(), k));
        }catch (Throwable t){
            t.printStackTrace();
        }
    }


}
