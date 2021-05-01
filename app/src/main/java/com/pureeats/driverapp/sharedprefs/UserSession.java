package com.pureeats.driverapp.sharedprefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.pureeats.driverapp.models.User;
import com.google.gson.Gson;
import com.pureeats.driverapp.models.request.RequestToken;

public class UserSession {
    private static final String TAG = "USER_SESSION";

    private static Context mContext;
    public static final String USER_SESSION = "USER_SESSION";
    public static final String USER_DATA = "USER_DATA";
    public static final String RESTAURANT_DATA = "RESTAURANT_DATA";
    public static final String PUSH_TOKEN = "PUSH_TOKEN";
    public static final String ACCEPTING_ORDER = "ACCEPTING_ORDER";
    private final String KEY_PUSH_TOKEN = "key_push_token";
    private static final String KEY_REQUEST_TOKEN = "key_request_token";
    //public static final SharedPreferences sharedPref = mContext.getSharedPreferences("curito_prefs",0);

    public UserSession(Context context) {
        this.mContext = context;
    }


    private SharedPreferences getUserPreference(){
        return mContext.getSharedPreferences(USER_SESSION,0);
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

            String userId = String.valueOf(user.getId());
            String authToken = user.getAuthToken();
            RequestToken requestToken = new RequestToken(userId, authToken);
            String requestTokenJson = new Gson().toJson(requestToken);
            if(!TextUtils.isEmpty(authToken)){
                setStr(KEY_REQUEST_TOKEN, requestTokenJson);
            }
            return true;
        }catch (Exception e){
            Log.e(TAG, "Error in setUserData() method in UserSession  "+e);
            return false;
        }
    }

    public RequestToken getRequestToken() {
        try{
            String requestTokenJson = getStr(KEY_REQUEST_TOKEN);
            if (requestTokenJson != null){
                return new Gson().fromJson(requestTokenJson, RequestToken.class);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
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
        User user = null;
        try {
            String userJson = getStr(USER_DATA);
            if(userJson != null){
                User userObj = new Gson().fromJson(userJson, User.class);
                return userObj;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return user;
    }

    public Boolean savePushToken(String pushToken) {
        try {
            if(pushToken != null){
                setStr(KEY_PUSH_TOKEN, pushToken);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
    public String getPushToken() {
        return getStr(KEY_PUSH_TOKEN);
    }




    public Boolean clear(){
        try{
            SharedPreferences.Editor editor = getUserPreference().edit();
            editor.clear();
            editor.apply();//editor.commit();
            return true;
        }catch (Exception e){
            Log.e(TAG, "Error in clearSharedPreference() method in UserSession  "+e);
        }
        return false;
    }


}
