package com.example.driverapp.repositories;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.example.driverapp.models.Address;
import com.example.driverapp.models.ApiResponse;
import com.example.driverapp.models.LoginResponse;
import com.example.driverapp.models.User;

public interface AuthRepository {
    public LiveData<Boolean> getIsLoading();
    public LiveData<ApiResponse> sendLoginOtp(@NonNull String phone);
    public LiveData<LoginResponse<User>> loginByOtp(@NonNull String phone, @NonNull String otp, String pushNotificationToken);
    public LiveData<LoginResponse<User>> loginByMobileAndPassword(@NonNull String phone, @NonNull String password, Address defaultAddress, String pushNotificationToken);
    public LiveData<LoginResponse<User>> getLoginResponse();
    public LiveData<Boolean> isLoggedIn();
    public void logout();
    public void setPushNotificationToken(@NonNull String token);
    public String getPushNotificationToken();
    public LiveData<Boolean> isPushNotificationTokenAvailable();
}
