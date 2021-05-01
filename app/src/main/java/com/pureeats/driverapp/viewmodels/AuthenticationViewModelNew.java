package com.pureeats.driverapp.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pureeats.driverapp.models.Address;
import com.pureeats.driverapp.models.ApiResponse;
import com.pureeats.driverapp.models.LoginResponse;
import com.pureeats.driverapp.models.User;
import com.pureeats.driverapp.models.request.RequestToken;
import com.pureeats.driverapp.models.response.LoginHistory;
import com.pureeats.driverapp.repositories.AuthRepository;
import com.pureeats.driverapp.repositories.AuthRepositoryImpl;
import com.pureeats.driverapp.repositories.AuthRepositoryImplOld;
import com.pureeats.driverapp.repositories.AuthRepositoryOld;

import java.util.List;

public class AuthenticationViewModelNew extends ViewModel {
    private final String TAG = this.getClass().getSimpleName();
    AuthRepositoryImpl authRepository;



}