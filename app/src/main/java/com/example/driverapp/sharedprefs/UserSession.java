package com.example.driverapp.sharedprefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.driverapp.models.Restaurant;
import com.example.driverapp.models.User;
import com.google.gson.Gson;

public class UserSession {
    private static final String TAG = "USER_SESSION";

    private static Context mContext;
    public static final String USER_SESSION = "USER_SESSION";
    public static final String USER_DATA = "USER_DATA";
    public static final String RESTAURANT_DATA = "RESTAURANT_DATA";
    public static final String PUSH_TOKEN = "PUSH_TOKEN";
    public static final String ACCEPTING_ORDER = "ACCEPTING_ORDER";
    //public static final SharedPreferences sharedPref = mContext.getSharedPreferences("curito_prefs",0);

    public UserSession(Context context) {
        this.mContext = context;
    }


    public static void setStr(String key,  String value){
        SharedPreferences sharedPref = mContext.getSharedPreferences(USER_SESSION,0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();//editor.commit();
    }
    public static String getStr(String key){
        SharedPreferences sharedPref = mContext.getSharedPreferences(USER_SESSION,0);
        return sharedPref.getString(key, null);
    }

    public static boolean isLoggedIn(){
        User user = new User();
        try{
            user  =  getUserData();
            if(user == null) return false;
            else return user.getAuthToken() != null;
        }catch (Exception e){
            //Log.e(TAG, "Error in isLoggedIn() method in UserSession  "+e);
            return false;
        }
    }
    public static boolean isPushNotificationAvailable(){
        boolean result = false;
        try{
            String token = getStr(PUSH_TOKEN);
            result = token != null;
        }catch (Exception e){
            //Log.e(TAG, "Error in isLoggedIn() method in UserSession  "+e);
            result = false;
        }
        return result;
    }
    public static boolean setUserData(User user){
        try{
            String userStr = new Gson().toJson(user);
            setStr(USER_DATA, userStr);

            return true;
        }catch (Exception e){
            Log.e(TAG, "Error in setUserData() method in UserSession  "+e);
            return false;
        }
    }
    public static boolean setPushNotificationToken(String token){
        try{
            setStr(PUSH_TOKEN, token);
            return true;
        }catch (Exception e){
            Log.e(TAG, "Error in setPushNotificationToken() method in UserSession  "+e);
            return false;
        }
    }
    public static String getPushNotificationToken(){
        try{
            return getStr(PUSH_TOKEN);
        }catch (Exception e){
            return null;
        }
    }
    public static User getUserData(){
        User user = new User();
        try{
            String str = getStr(USER_DATA);
            user = new Gson().fromJson(str, User.class);
            return user;
        }catch (Exception e){
            Log.e(TAG, "Error in setUserData() method in UserSession  "+e);
            return null;
        }
    }
    public static User getUserData(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(USER_SESSION,0);
        String userJson = sharedPref.getString(USER_DATA, null);
        if(userJson != null){
            return new Gson().fromJson(userJson, User.class);
        }
        return null;
    }




    private static void clearSharedPreference(){
        try{
            SharedPreferences sharedPref = mContext.getSharedPreferences(USER_SESSION,0);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.clear();
            editor.apply();//editor.commit();
        }catch (Exception e){
            Log.e(TAG, "Error in clearSharedPreference() method in UserSession  "+e);
        }
    }
    public static void logOut(){
        setStr(PUSH_TOKEN, null);
        clearSharedPreference();
    }

}
