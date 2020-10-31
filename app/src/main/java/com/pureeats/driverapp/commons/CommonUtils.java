package com.pureeats.driverapp.commons;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.pureeats.driverapp.BuildConfig;
import com.pureeats.driverapp.models.Location;
import com.pureeats.driverapp.models.Restaurant;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

public class CommonUtils {

    public static void makePhoneCall(Activity activity, String phoneNumber){
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
            Location orderLocation = new Gson().fromJson(address, Location.class);
            System.out.println("ORDER_LOCATION: "+orderLocation);
            double lat = Double.parseDouble(orderLocation.getLat());
            double lng = Double.parseDouble(orderLocation.getLng());
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
}
