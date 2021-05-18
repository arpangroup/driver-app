package com.pureeats.driverapp.utils;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;

import com.android.volley.BuildConfig;
import com.pureeats.driverapp.R;
import com.pureeats.driverapp.commons.Actions;
import com.pureeats.driverapp.commons.Constants;
import com.pureeats.driverapp.commons.NotificationSoundType;
import com.pureeats.driverapp.models.Order;
import com.pureeats.driverapp.models.Restaurant;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.pureeats.driverapp.views.App;
import com.pureeats.driverapp.views.order.DialogActivity;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtils {
    public static final String TAG = "CommonUtils";
    private static DecimalFormat DECIMAL_FORMATTER = new DecimalFormat("#.##");
    private static final String SPACE_REPLACE_PATTERN = "_^_";
    private static final String SPACE = " ";

    public static final String DATE_FORMAT = "dd-MM-YYYY hh:mm aa";
    public static final String DATE_FORMAT_DD_MM_YYYY = "dd/MM/yyyy";
    public static final String DATE_FORMAT_MM_DD_YYYY = "MM/dd/yyyy";
    public static final String DATE_FORMAT_DDMMYYYY = "dd-MM-yyyy";
    public static final String DATE_FORMAT_dd_MMM_yyyy = "dd-MMM-yyyy";
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_yyyy_MM_dd = "yyyy-MM-dd";

    public static SimpleDateFormat sdfhh_mm_aa = new SimpleDateFormat("hh:mm aa");//03:40 PM
    public static SimpleDateFormat sdfDefaultDateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);

    public static DateTimeFormatter formaterMMddyyyy = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    public static DateTimeFormatter formaterddMMyyyy = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final SimpleDateFormat sdf_yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");
    public static final SimpleDateFormat sdf_yyyy = new SimpleDateFormat("yyyy");
    public static DateTimeFormatter formateryyyy_MM_dd = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final String CUSTOM_PARAM_DELIMITER = "~#CUST_PARAM_DELIMITER#~";
    public static DateTimeFormatter formatteryyyy_MM_dd_ = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void makePhoneCall(Activity activity, String phoneNumber){
        if(phoneNumber == null) return;
        if(phoneNumber.trim().length() > 0){
            if(ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE}, Constants.REQUEST_CALL_ACTIVITY);
            }else{
                String dial = "tel:" + phoneNumber;
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse(dial));
                activity.startActivity(intent);
            }
        }

    }

    public static LatLng getRestaurantLocation(Restaurant restaurant){
        LatLng latLng = null;
        try{
            double lat = Double.parseDouble(restaurant.getLatitude());
            double lng = Double.parseDouble(restaurant.getLongitude());
            latLng = new LatLng(lat, lng);
        }catch (Exception e){
            e.printStackTrace();
        }
        return latLng;
    }

    public static LatLng getUserLocation(String address){
        LatLng latLng = null;
        try{
            System.out.println("==============address==============================");
            System.out.println(address);
            Map<String, Double> orderLocation = new Gson().fromJson(address, Map.class);
            System.out.println("ORDER_LOCATION: "+orderLocation);
            double lat = orderLocation.get("latitude");
            double lng = orderLocation.get("longitude");
            latLng = new LatLng(lat, lng);
        }catch (Exception e){
            e.printStackTrace();
        }
        return latLng;
    }

    public static void shareApp(Context context){
        try{
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
            String shareMessage= "\nLet me recommend you this application\n\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            context.startActivity(Intent.createChooser(shareIntent, "choose one"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void rateApp(Context context){
        try{
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse("market://details?id=" + context.getPackageName()));
            context.startActivity(i);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * @param phone
     * @return The number which satisfies the above criteria, is a valid mobile Number.
     * The first digit should contain number between 7 to 9.
     * The rest 9 digit can contain any number between 0 to 9.
     * The mobile number can have 11 digits also by including 0 at the starting.
     * The mobile number can be of 12 digits also by including 91 at the starting
     */
    public static boolean isValidPhoneNumber(String phone) {
        phone = trimPhoneNumber(phone);
        // The given argument to compile() method
        // is regular expression. With the help of
        // regular expression we can validate mobile
        // number.
        // 1) Begins with 0 or 91
        // 2) Then contains 6 or 7 or 8 or 9.
        // 3) Then contains 9 digits
        Pattern p = Pattern.compile("(0/91)?[6-9][0-9]{9}");

        // Pattern class contains matcher() method
        // to find matching between given number
        // and regular expression
        Matcher m = p.matcher(phone);
        return (m.find() && m.group().equals(phone));
    }

    public static String trimPhoneNumber(String phone) {
        if (TextUtils.isEmpty(phone)) {
            return phone;
        } else {
            try {
                phone = phone.replace("+91", "");
                phone = phone.replaceAll("[^0-9]", "");//replace all except 0-9
                return phone;
            } catch (Exception e) {
                e.printStackTrace();
                return phone;
            }
        }
    }


    public static Map<String, String> getDeviceInfo(Context context) {
        Map<String, String> deviceInfo = new HashMap<>();
        String serial = Build.SERIAL;
        String model = Build.MODEL;
        String id = Build.ID;
        String manufacturer = Build.MANUFACTURER;
        String brand = Build.BRAND;
        String type = Build.TYPE;
        String user = Build.USER;
        String baseVersionCode = Build.VERSION_CODES.BASE + "";
        String incrementalVersionCode = Build.VERSION.INCREMENTAL + "";
        String sdk = Build.VERSION.SDK;
        String board = Build.BOARD;
        String fingerprint = Build.FINGERPRINT;
        String releaseVersionCode = Build.VERSION.RELEASE;
        String host = Build.HOST;

        deviceInfo.put("serial", Build.SERIAL);
        deviceInfo.put("model", Build.MODEL);
        deviceInfo.put("id", Build.ID);
        deviceInfo.put("manufacturer", Build.MANUFACTURER);
        deviceInfo.put("brand", Build.BRAND);
        deviceInfo.put("type", Build.TYPE);
        deviceInfo.put("user", Build.USER);
        deviceInfo.put("baseVersionCode", Build.VERSION_CODES.BASE + "");
        deviceInfo.put("incrementalVersionCode", Build.VERSION.INCREMENTAL + "");
        deviceInfo.put("sdk", Build.VERSION.SDK);
        deviceInfo.put("board", Build.BOARD);
        deviceInfo.put("fingerprint", Build.FINGERPRINT);
        deviceInfo.put("releaseVersionCode", Build.VERSION.RELEASE);
        deviceInfo.put("host", Build.HOST);
        return deviceInfo;
    }



    public static void showOrderArriveNotification(Context context, int orderId, String uniqueOrderId) {
        //Log.d(TAG, "showOrderArriveNotification...");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, App.CHANNEL_ID_PUSH_NOTIFICATION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            PendingIntent pendingIntent = DialogActivity.getPendingIntent(context, orderId, uniqueOrderId);
            builder.setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle("New Order Arrive")
                    .setContentText("Order # " + uniqueOrderId)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_CALL)
                    .setFullScreenIntent(pendingIntent, true)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setOngoing(true);
            android.app.Notification notification = builder.build();

            //Log.d(TAG, "Opening DialogActivity from Notification......");
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.notify(orderId, notification);
        }else{
            //Log.d(TAG, "Starting DialogActivity......");
            context.startActivity(DialogActivity.getIntent(context, orderId, uniqueOrderId));
        }

    }


    public static void displayNotification(Context context, String title, String message, NotificationSoundType soundType) {
        displayNotification(context, title, message);
        playNotificationSound(context, soundType);
    }
    public static void displayNotification(Context context, String title, String message) {
        //Intent notificationIntent = new Intent(context, HomePageNavigationActivityOld.class);
        //PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, App.CHANNEL_ID_PUSH_NOTIFICATION)
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentTitle(title)
                        .setContentText(message)
                        //.setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)// clear notification after click
                        //.setChannelId(App.CHANNEL_ID_PUSH_NOTIFICATION);
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(App.NOTIFICATION_CHANNEL_ID_PUSH_NOTIFICATION, builder.build());
    }

    public static void openNavigationActivity(Context context, LatLng destination){
        try{
            Uri uri = Uri.parse("google.navigation:q="+destination.latitude + ","  + destination.longitude +  "&mode=1");
            //Uri uri = Uri.parse("https://www.google.co.in/maps/dir/" + sSource + "/" + sDestination);
            Intent intent  = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.google.android.apps.maps");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }catch (ActivityNotFoundException e){
            // when google map is not installed
            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.google.ndroid.apps.maps");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
        }
    }

    /**
     * Input Format : "2021-01-21 02:29:55"
     * Output Format: 10837474
     *
     * @param dateStr
     * @return
     */
    public static long getTimeInMilliseconds(String dateStr) {
        try {
            Date date = sdfDefaultDateFormat.parse(dateStr);
            return date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
            return 0l;
        }
    }

    public static String capitalize(String str) {
        if (str == null) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static String getTipDuration(String riderAssignedAt, String deliverAt){
       try {
           long startTime = getTimeInMilliseconds(riderAssignedAt);
           long endTime = getTimeInMilliseconds(deliverAt);
           long millis = endTime - startTime;

           //long hour = TimeUnit.MILLISECONDS.toHours(requiredTime);
           //long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
           //long seconds = (TimeUnit.MILLISECONDS.toSeconds(millis) % 60);
           //return minutes + " minutes " + seconds + " seconds";

           String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                   TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                   TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
          return hms;

       }catch (Exception e){
            return "null";
       }
    }


    public static String calculateDistance(Order order){
        try{

            LatLng latLngRestaurant = CommonUtils.getRestaurantLocation(order.getRestaurant());
            LatLng latLngCustomer = CommonUtils.getUserLocation(order.getLocation());

            android.location.Location locationA = new android.location.Location("locationA");
            locationA.setLatitude(latLngRestaurant.latitude);
            locationA.setLongitude(latLngRestaurant.longitude);
            android.location.Location locationB = new Location("locationB");
            locationB.setLatitude(latLngCustomer.latitude);
            locationB.setLongitude(latLngCustomer.longitude);

            float distanceInMeter = locationA.distanceTo(locationB);
            float distanceInKm = distanceInMeter / 1000;

            return DECIMAL_FORMATTER.format(distanceInKm) + " km";
        }catch (Exception e){
            return "0 km";
        }
    }

    public static String convertToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }


    public static void playNotificationSound(Context context, NotificationSoundType soundType){
        MediaPlayer mediaPlayer = new MediaPlayer();

        if(soundType == NotificationSoundType.ORDER_ARRIVE) mediaPlayer = MediaPlayer.create(context, R.raw.order_arrived_ringtone);
        else if(soundType == NotificationSoundType.ORDER_CANCELED) mediaPlayer = MediaPlayer.create(context, R.raw.swiggy_order_cancel_ringtone);
        else mediaPlayer = MediaPlayer.create(context, R.raw.default_notification_sound);

        try{
            mediaPlayer.start();
        }catch (Exception e){
            //e.printStackTrace();
        }
    }

    private void stopMediaPlayer(MediaPlayer mMediaPlayer){
        try {
            if(mMediaPlayer != null){
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
        }catch (Throwable t){}
    }

    public static void cancelNotification(Context context, int notifyId) {
        try{
            String ns = Context.NOTIFICATION_SERVICE;
            NotificationManager nMgr = (NotificationManager) context.getSystemService(ns);
            nMgr.cancel(notifyId);
        }catch (Exception e){

        }
    }

    public static String getTimeFromDate(String dateStr){
        DateFormat dateFormat = sdfhh_mm_aa;
        return sdfhh_mm_aa.format(getTimeInMilliseconds(dateStr));
    }

}
