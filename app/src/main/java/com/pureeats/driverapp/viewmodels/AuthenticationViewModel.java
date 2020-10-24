package com.pureeats.driverapp.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pureeats.driverapp.models.Address;
import com.pureeats.driverapp.models.ApiResponse;
import com.pureeats.driverapp.models.LoginResponse;
import com.pureeats.driverapp.models.User;
import com.pureeats.driverapp.repositories.AuthRepository;
import com.pureeats.driverapp.repositories.AuthRepositoryImpl;

public class AuthenticationViewModel extends ViewModel {
    private final String TAG = this.getClass().getSimpleName();
    public static int MAX_LOGIN_ATTEMPT_COUNT = 3;
    private AuthRepository authRepository;
    private MutableLiveData<Address> mCurrentAddress = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoggedIn = new MutableLiveData<>();
    private MutableLiveData<String> mutablePhoneNumber = new MutableLiveData<>();
    private static int LOGIN_ATTEMPT = 0;
    private MutableLiveData<Integer> mutableLoginAttempt = new MutableLiveData<>(0);

    private MutableLiveData<Boolean> mIsLoading = new MutableLiveData<>();

    public void init(){
        //boolean loggedIn = UserSession.isLoggedIn();
        //if(loggedIn) isLoggedIn.setValue(true);
        //else isLoggedIn.setValue(false);

        authRepository = AuthRepositoryImpl.getInstance();
    }


    public LiveData<Boolean> getIsLoading(){
        LiveData<Boolean> isLoading=authRepository.getIsLoading();
        return isLoading;
    }
    public LiveData<ApiResponse> sendLoginOtp(String phone){
        return authRepository.sendLoginOtp(phone);
    }
    public LiveData<LoginResponse<User>> loginByOtp(@NonNull String phone, @NonNull String otp){
        mutableLoginAttempt.setValue(++LOGIN_ATTEMPT);
        String pushToken = authRepository.getPushNotificationToken();
        return authRepository.loginByOtp(phone, otp, pushToken);
    }
    public LiveData<LoginResponse<User>> loginByMobileAndPassword(@NonNull String phone, @NonNull String password, Address defaultAddress){
        mutableLoginAttempt.setValue(++LOGIN_ATTEMPT);
        String pushToken = authRepository.getPushNotificationToken();
        return authRepository.loginByMobileAndPassword(phone, password, defaultAddress, pushToken);
    }
    public LiveData<LoginResponse<User>> getLoginResponse(){
        return authRepository.getLoginResponse();
    }
    public void setPhoneNumber(String phoneNumber){
        if(mutablePhoneNumber == null) mutablePhoneNumber = new MutableLiveData<>();
        mutablePhoneNumber.setValue(phoneNumber);
    }
    public LiveData<String> getPhoneNumber(){
        return mutablePhoneNumber;
    }
    public LiveData<Boolean> isLoggedIn(){
        return authRepository.isLoggedIn();
    }
    public void logout(){
        authRepository.logout();
        onCleared();
    }
    public LiveData<Boolean> isFirebaseTokenAvailable(){
        return authRepository.isPushNotificationTokenAvailable();
    }
    public void setFirebaseToken(String firebaseToken){
        authRepository.setPushNotificationToken(firebaseToken);
    }
    public LiveData<Integer> getLoginAttempt() {
        return mutableLoginAttempt;
    }


    @Override
    protected void onCleared() {
        super.onCleared();
    }
}