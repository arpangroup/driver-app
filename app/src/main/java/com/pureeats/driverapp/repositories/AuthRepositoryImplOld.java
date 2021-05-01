package com.pureeats.driverapp.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.pureeats.driverapp.network.Api;
import com.pureeats.driverapp.network.ApiService;
import com.pureeats.driverapp.models.Address;
import com.pureeats.driverapp.models.ApiResponse;
import com.pureeats.driverapp.models.LoginResponse;
import com.pureeats.driverapp.models.User;
import com.pureeats.driverapp.models.request.LoginRequest;
import com.pureeats.driverapp.models.request.RequestToken;
import com.pureeats.driverapp.models.response.LoginHistory;
import com.pureeats.driverapp.sharedprefs.UserSession;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepositoryImplOld implements AuthRepositoryOld {
    private final String TAG = this.getClass().getSimpleName();
    private static AuthRepositoryImplOld authRepository;


    private final MutableLiveData<Boolean> isLoading=new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isLoggedIn = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isPushNotificationAvailable = new MutableLiveData<>(false);
    private MutableLiveData<ApiResponse> sendOtpResponse = new MutableLiveData<>();
    private MutableLiveData<LoginResponse<User>> loginResponse = new MutableLiveData<>();


    public static AuthRepositoryOld getInstance(){
        if (authRepository == null){
            authRepository = new AuthRepositoryImplOld();
        }
        return authRepository;
    }

    @Override
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    @Override
    public LiveData<ApiResponse> sendLoginOtp(String phone) {
        if(sendOtpResponse == null){
            sendOtpResponse = new MutableLiveData<>();
        }
        return sendOtp(phone);
    }

    @Override
    public LiveData<LoginResponse<User>> loginByOtp(@NonNull String phone, @NonNull String otp, String pushToken) {
        if(loginResponse == null){
            loginResponse = new MutableLiveData<>();
        }
        LoginRequest loginRequest = new LoginRequest(phone, otp);
        loginRequest.setPushToken(pushToken);
        return loginNow(loginRequest);
    }

    @Override
    public LiveData<LoginResponse<User>> loginByMobileAndPassword(@NonNull String phone, @NonNull String password, Address defaultAddress, String pushToken) {
        if(loginResponse == null){
            loginResponse = new MutableLiveData<>();
        }
        LoginRequest loginRequest = new LoginRequest(phone, password);
        loginRequest.setPushToken(pushToken);
        return loginNow(loginRequest);
    }

    @Override
    public LiveData<LoginResponse<User>> getLoginResponse() {
        return loginResponse;
    }

    @Override
    public LiveData<Boolean> isLoggedIn() {
        if(UserSession.isLoggedIn()){
            isLoggedIn.setValue(true);
        }else {
            isLoggedIn.setValue(false);
        }
        return isLoggedIn;
    }

    @Override
    public void logout() {
        isLoading.setValue(true);
        UserSession.logOut();
        isLoggedIn.setValue(false);
        isLoading.setValue(false);
        sendOtpResponse = new MutableLiveData<>();
        loginResponse= new MutableLiveData<>();
    }

    @Override
    public void setPushNotificationToken(@NonNull String token) {
        UserSession.setPushNotificationToken(token);
    }

    @Override
    public String getPushNotificationToken() {
        return UserSession.getPushNotificationToken();
    }

    @Override
    public LiveData<Boolean> isPushNotificationTokenAvailable() {
        Boolean result = UserSession.isPushNotificationAvailable();
        isPushNotificationAvailable.setValue(result);
        return isPushNotificationAvailable;
    }

    @Override
    public LiveData<ApiResponse> logoutSession(RequestToken requestToken) {
        return logoutSessionApi(requestToken);
    }

    @Override
    public LiveData<List<LoginHistory>> getLoginHistory(RequestToken requestToken) {
        return getLoginHistoryApi(requestToken);
    }


    private LiveData<ApiResponse> sendOtp(String phone){
        isLoading.setValue(true);
        Log.d(TAG, "Inside sendOtp()......................");
        Log.d(TAG, "PHONE: "+ phone);
        Api apiInterface = ApiService.getApiService();
        apiInterface.sendLoginOtp(phone).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                isLoading.setValue(false);
                try{
                    sendOtpResponse.setValue(response.body());
                }catch (Exception e){
                    Log.d(TAG, "Error inside sendOtp()... method");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                isLoading.setValue(false);
            }
        });
        return sendOtpResponse;
    }
    private LiveData<LoginResponse<User>> loginNow(LoginRequest loginRequest){
        isLoading.setValue(true);
        Log.d(TAG, "Inside loginNow()......................");
        Log.d(TAG, "LOGIN_REQUEST: "+ new Gson().toJson(loginRequest));
        Api apiInterface = ApiService.getApiService();
        apiInterface.login(loginRequest).enqueue(new Callback<LoginResponse<User>>() {
            @Override
            public void onResponse(Call<LoginResponse<User>> call, Response<LoginResponse<User>> response) {
                isLoading.setValue(false);
                try{
                    LoginResponse<User> resp  = response.body();
                    Log.d(TAG, "RESPONSE: "+resp);
                    if (resp != null && resp.isSuccess()) {
                        UserSession.setUserData(resp.getData());
                        isLoggedIn.setValue(true);
                        String pushToken = resp.getData().getPushToken();
                        if(pushToken != null){
                            UserSession.setPushNotificationToken(pushToken);
                        }
                    }
                    loginResponse.setValue(resp);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse<User>> call, Throwable t) {
                Log.d(TAG, "fAIL API Call");
                isLoading.setValue(false);
            }
        });
        return loginResponse;
    }

    private LiveData<ApiResponse> logoutSessionApi(RequestToken requestToken){
        isLoading.setValue(true);
        MutableLiveData<ApiResponse> mutableResponse  = new MutableLiveData<>();
        Log.d(TAG, "Inside logoutSessionApi()......................");
        Log.d(TAG, "REQUEST: "+ new Gson().toJson(requestToken));
        Api apiInterface = ApiService.getApiService();
        apiInterface.logoutSession(requestToken).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                ApiResponse apiResponse = response.body();
                Log.d(TAG, "RESPONSE: " + apiResponse.getMessage());
                mutableResponse.setValue(apiResponse);
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.d(TAG, "FAIL");
                ApiResponse apiResponse = new ApiResponse(false, "FAIL");
                mutableResponse.setValue(apiResponse);
            }
        });
        return mutableResponse;
    }


    private LiveData<List<LoginHistory>> getLoginHistoryApi(RequestToken requestToken){
        isLoading.setValue(true);
        MutableLiveData<List<LoginHistory>> mutableResponse  = new MutableLiveData<>(new ArrayList<>());
        Log.d(TAG, "Inside getLoginHistoryApi()......................");
        String riderId = requestToken.getUserId()+"";
        Log.d(TAG, "REQUEST: rider_id: "+ riderId);
        Api apiInterface = ApiService.getApiService();
        apiInterface.loginHistory(riderId).enqueue(new Callback<com.pureeats.driverapp.models.response.ApiResponse<List<LoginHistory>>>() {
            @Override
            public void onResponse(Call<com.pureeats.driverapp.models.response.ApiResponse<List<LoginHistory>>> call, Response<com.pureeats.driverapp.models.response.ApiResponse<List<LoginHistory>>> response) {
                isLoading.setValue(false);
                if(response.isSuccessful()){
                    mutableResponse.setValue(response.body().getData());
                }
            }

            @Override
            public void onFailure(Call<com.pureeats.driverapp.models.response.ApiResponse<List<LoginHistory>>> call, Throwable t) {
                isLoading.setValue(false);
                Log.d(TAG, "FAIL");
            }
        });
        return mutableResponse;
    }

}
