package com.example.driverapp.sharedprefs;

import android.content.Context;
import android.content.SharedPreferences;

public class ServiceTracker {

    public static enum ServiceState {
        STARTED,
        STOPPED,
    }

    private static final String NAME = "SPYSERVICE_KEY";
    private static final String KEY  = "SPYSERVICE_STATE";

    public static void setServiceState(Context context, ServiceState state){
        SharedPreferences sharedPref = getPreference(context);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(KEY, state.name());
        editor.apply();
    }

    public static ServiceState getServiceState(Context context){
        SharedPreferences sharedPref = getPreference(context);
        String val = sharedPref.getString(KEY, ServiceState.STOPPED.name());
        return ServiceState.valueOf(val);
    }

    private static SharedPreferences getPreference(Context context){
        return context.getSharedPreferences(NAME,0);
    }
}
