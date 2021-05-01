package com.pureeats.driverapp.repositories;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.pureeats.driverapp.models.ApiResponse;
import com.pureeats.driverapp.models.User;
import com.pureeats.driverapp.network.Resource;

import java.util.Map;

public interface AuthRepository {
    public LiveData<Resource<ApiResponse<Object>>> verifyPhone(String phone);
    public LiveData<Resource<ApiResponse<User>>> loginByOtp(@NonNull String phone, @NonNull String otp, String pushNotificationToken, @Nullable Map<String, String> meta);
}
