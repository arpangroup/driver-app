package com.pureeats.driverapp.sharedprefs;

import android.content.Context;
import android.content.SharedPreferences;

public class OrderTracker {

    public static enum OrderState {
        ONGOING,
        COMPLETED,
    }

    private static final String NAME = "ORDER_KEY";
    private static final String KEY  = "ORDER_STATE";

    public static void setServiceState(Context context, OrderState state){
        SharedPreferences sharedPref = getPreference(context);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(KEY, state.name());
        editor.apply();
    }

    public static OrderState getOrderState(Context context){
        SharedPreferences sharedPref = getPreference(context);
        String val = sharedPref.getString(KEY, OrderState.COMPLETED.name());
        return OrderState.valueOf(val);
    }

    private static SharedPreferences getPreference(Context context){
        return context.getSharedPreferences(NAME,0);
    }
}
