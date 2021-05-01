package com.pureeats.driverapp.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.pureeats.driverapp.models.ApiResponse;
import com.pureeats.driverapp.models.User;
import com.pureeats.driverapp.models.request.LoginRequest;
import com.pureeats.driverapp.models.request.RequestToken;
import com.pureeats.driverapp.models.response.LoginHistory;
import com.pureeats.driverapp.network.Resource;
import com.pureeats.driverapp.network.api.Api;
import com.pureeats.driverapp.sharedprefs.UserSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthRepositoryImpl extends BaseRepository implements AuthRepository{
    private final String TAG = this.getClass().getSimpleName();
    private Api api;
    private UserSession userSession;
    private RequestToken requestToken;


    public AuthRepositoryImpl(Api api, UserSession userSession){
        this.api = api;
        this.userSession = userSession;
        this.requestToken = userSession.getRequestToken();
    }


    @Override
    public LiveData<Resource<ApiResponse<Object>>> verifyPhone(String phone) {
        HashMap<String, String> map = new HashMap<>();
        map.put("phone", phone);
        return safeApiCall(api.verifyPhone(map));
    }

    @Override
    public LiveData<Resource<ApiResponse<User>>> loginByOtp(@NonNull String phone, @NonNull String otp, String pushNotificationToken, @Nullable Map<String, String> meta) {
        LoginRequest loginRequest = new LoginRequest(phone, otp, pushNotificationToken, meta);
        Log.d(TAG, "REQUEST: "+new Gson().toJson(loginRequest));
        return safeApiCall(api.loginUsingOtp(loginRequest));
    }

    @Override
    public LiveData<Resource<ApiResponse<List<LoginHistory>>>> getLoginHistory() {
        if (requestToken == null) return  new MutableLiveData<>(Resource.authError());
        return safeApiCall(api.loginHistory(requestToken.getUserId()));
    }


}
