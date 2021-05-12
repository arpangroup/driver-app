package com.pureeats.driverapp.utils;

import android.app.Activity;
import android.content.Context;
import android.provider.Settings;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.common.util.CollectionUtils;
import com.google.android.material.snackbar.Snackbar;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommonUiUtils {
    public static final String TAG = "UTIL";

    public static void closeKeyBoard(Activity activity){
        View view = activity.getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    public static void openKeyBoard(Activity activity){
        View view = activity.getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInputFromWindow(view.getWindowToken(), InputMethodManager.SHOW_FORCED, 0);
        }
    }
    public static String getDeviceId(Context context) {
        String deviceId;
        deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return deviceId;
    }
    public static void showSnackBar(View view, String message){
        //fragment.requireView()
        Snackbar snackBar  = Snackbar.make(view,  message,  Snackbar.LENGTH_LONG);
        snackBar.show();

    }

    public static void showSnackBarWithAction(View view, String message){
        //fragment.requireView()
        Snackbar snackBar  = Snackbar.make(view,  message,  Snackbar.LENGTH_LONG);
        snackBar.show();

    }

    public static void enable(View view, boolean isEnable){
        if(isEnable){
            view.setAlpha(1.0f);
        }else {
            view.setAlpha(0.6f);
        }
        view.setEnabled(isEnable);
    }

    public int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
