package com.example.driverapp.views;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.driverapp.R;

public class App extends Application {
    public static final String CHANNEL_ID_NEW_ORDER = "channel_new_orders";
    public static final String CHANNEL_NAME_NEW_ORDER = "channel new orders";
    public static final int NOTIFICATION_ID_NEW_ORDER = 700;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();

    }

    private void createNotificationChannels() {
        Uri sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notification_mp3);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannelNewOrder = new NotificationChannel(
                    CHANNEL_ID_NEW_ORDER,
                    CHANNEL_NAME_NEW_ORDER,
                    NotificationManager.IMPORTANCE_HIGH
                    );

//            NotificationChannel serviceChannel = new NotificationChannel(
//                    CHANNEL_ID_NEW_ORDER_FETCH_SERVICE,
//                    CHANNEL_NAME_NEW_ORDER_FETCH_SERVICE,
//                    NotificationManager.IMPORTANCE_HIGH
//            );

//            NotificationChannel notificationChannelPushNotification = new NotificationChannel(
//                    CHANNEL_ID_PUSH_NOTIFICATION,
//                    CHANNEL_NAME_PUSH_NOTIFICATION,
//                    NotificationManager.IMPORTANCE_DEFAULT
//            );

            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build();

            notificationChannelNewOrder.setSound(sound, attributes);


            notificationChannelNewOrder.setDescription("This is New Order Notification Channel");
            notificationChannelNewOrder.enableLights(true);
            notificationChannelNewOrder.setLightColor(Color.RED);
//            notificationChannelPushNotification.setDescription("This is Push Notifications channel");
//            serviceChannel.setDescription("This is New Order Fetch Service channel");
//            serviceChannel.enableLights(true);
//            serviceChannel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
//            serviceChannel.setLightColor(Color.RED);
//            serviceChannel.enableVibration(true);
//            serviceChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

            // Register the channels with Notification Framework
            //NotificationManager notificationManager = getSystemService(NotificationManager.class);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannelNewOrder);
            //manager.createNotificationChannel(notificationChannelPushNotification);
            //manager.createNotificationChannel(serviceChannel);

        }
    }

}
