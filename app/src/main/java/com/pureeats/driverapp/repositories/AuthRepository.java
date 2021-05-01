package com.pureeats.driverapp.repositories;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.pureeats.driverapp.models.ApiResponse;
import com.pureeats.driverapp.models.User;
import com.pureeats.driverapp.models.response.LoginHistory;
import com.pureeats.driverapp.network.Resource;

import java.util.List;
import java.util.Map;

public interface AuthRepository {
    LiveData<Resource<ApiResponse<Object>>> verifyPhone(String phone);
    LiveData<Resource<ApiResponse<User>>> loginByOtp(@NonNull String phone, @NonNull String otp, String pushNotificationToken, @Nullable Map<String, String> meta);

    public LiveData<Resource<ApiResponse<List<LoginHistory>>>> getLoginHistory();
}
