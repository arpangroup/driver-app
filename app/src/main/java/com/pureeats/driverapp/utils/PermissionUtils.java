package com.pureeats.driverapp.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.pureeats.driverapp.commons.Constants;

public class PermissionUtils {

    public static boolean isCameraPermissionEnable(Context context){
        return context.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }
    public static boolean isLocationPermissionEnable(Context context){
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }
    public static boolean isStoragePermissionEnable(Context context){
        return ContextCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }
    public static boolean isTelephonePermissionEnable(Context context){
        return ContextCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED;
    }

    public static void askCameraPermission(Activity activity){
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, Constants.REQUEST_IMAGE_CAPTURE);
    }
    public static void askLocationPermission(Activity activity){
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.REQUEST_LOCATION_PERMISSION);
    }
    public static void askStoragePermission(){}
    public static void askTelephonePermission(Activity activity){
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE}, Constants.REQUEST_CALL_ACTIVITY);
    }

}
