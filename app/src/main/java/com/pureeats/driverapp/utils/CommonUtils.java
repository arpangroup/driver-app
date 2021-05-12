package com.pureeats.driverapp.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.BuildConfig;
import com.pureeats.driverapp.R;
import com.pureeats.driverapp.commons.Constants;
import com.pureeats.driverapp.models.Location;
import com.pureeats.driverapp.models.Restaurant;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.pureeats.driverapp.views.App;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtils {

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

}
