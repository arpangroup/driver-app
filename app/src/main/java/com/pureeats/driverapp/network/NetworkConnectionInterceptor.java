package com.pureeats.driverapp.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.pureeats.driverapp.exceptions.NoInternetException;


import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class NetworkConnectionInterceptor implements Interceptor {
    private static final String DEBUG_TAG = "NetworkStatusExample";
    private Context applicationContext;

    public NetworkConnectionInterceptor(Context context) {
        this.applicationContext = context.getApplicationContext();
    }


    @Override
    public Response intercept(Chain chain) throws IOException {
        if(!isInternetAvailable()) throw new NoInternetException("Make sure you have an active data connection");
        return chain.proceed(chain.request());
    }


    private boolean isInternetAvailable(){
        boolean isWifiConn = false;
        boolean isMobileConn = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
