package com.pureeats.driverapp.repositories;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.pureeats.driverapp.models.Address;
import com.pureeats.driverapp.models.ApiResponse;
import com.pureeats.driverapp.models.LoginResponse;
import com.pureeats.driverapp.models.User;
import com.pureeats.driverapp.models.request.RequestToken;

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

    public  LiveData<ApiResponse> logoutSession(RequestToken requestToken);
}
