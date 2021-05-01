package com.pureeats.driverapp.viewmodels;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


import com.pureeats.driverapp.models.ApiResponse;
import com.pureeats.driverapp.models.User;
import com.pureeats.driverapp.network.Resource;
import com.pureeats.driverapp.repositories.AuthRepositoryImpl;
import com.pureeats.driverapp.utils.CommonUtils;

import java.util.Map;


public class AuthViewModel extends BaseViewModel {
    private final String TAG = this.getClass().getSimpleName();
    public boolean isFirebasePhoneVerificationEnable = true;
    private AuthRepositoryImpl authRepository;
    private String phoneNumber;
    public String verificationId;

    public AuthViewModel(AuthRepositoryImpl repository) {
        super(repository);
        this.authRepository = repository;
    }

    public LiveData<Resource<ApiResponse<Object>>> verifyPhone(String phone){
        Log.d(TAG, "Inside sendOtp()....");
        if(CommonUtils.isValidPhoneNumber(phone)){
            this.phoneNumber = phone;
            return authRepository.verifyPhone(phone);
        }else{
            return new MutableLiveData<>(Resource.error("", "Invalid Phone Number"));
        }
    }

    public LiveData<Resource<ApiResponse<User>>> loginByOtp(@NonNull String phone, @NonNull String otp, String pushToken, Map<String, String> meta){
        return authRepository.loginByOtp(phone, otp, pushToken, meta);
    }





    /*############################### GETTER__SETTER[START] #################################*/

    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    /*################################# GETTER__SETTER[END] #################################*/


}