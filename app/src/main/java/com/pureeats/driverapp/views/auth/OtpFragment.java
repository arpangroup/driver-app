package com.pureeats.driverapp.views.auth;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavController;

import com.pureeats.driverapp.databinding.FragmentOtpBinding;
import com.pureeats.driverapp.models.ApiResponse;
import com.pureeats.driverapp.models.User;
import com.pureeats.driverapp.network.api.Api;
import com.pureeats.driverapp.repositories.AuthRepositoryImpl;
import com.pureeats.driverapp.repositories.BaseRepository;
import com.pureeats.driverapp.utils.CommonUiUtils;
import com.pureeats.driverapp.utils.CommonUtils;
import com.pureeats.driverapp.utils.SafeClickListener;
import com.pureeats.driverapp.viewmodels.AuthViewModel;
import com.pureeats.driverapp.views.MainActivity;
import com.pureeats.driverapp.views.base.BaseDialogFragment;

import java.util.Locale;
import java.util.Map;

public class OtpFragment extends BaseDialogFragment<AuthViewModel, FragmentOtpBinding, AuthRepositoryImpl> {
    private final String TAG = this.getClass().getSimpleName();
    private static long COUNT_DOWN_TIME = 1 * 30 *1000; // 1 Min;
    NavController navController;
    private long mTimeLeftInMills = COUNT_DOWN_TIME;
    private MutableLiveData<Boolean> isValidOtp = new MutableLiveData<>(false);

    @Override
    public void onViewCreated(@NonNull View rooView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rooView, savedInstanceState);
        mBinding.setLifecycleOwner(this);
        mBinding.setViewModel(viewModel);
        startTimer();

        mBinding.pinView.setPinViewEventListener((pinview, fromUser) ->{
            if(mBinding != null)mBinding.btnVerify.setEnabled(true);
            isValidOtp.setValue(true);
        });

        mBinding.back.setOnClickListener(view -> {
            navController.popBackStack();
        });

        mBinding.txtResend.setOnClickListener(new SafeClickListener(1000) {
            @Override
            public void onSafeClick(View view) {
                mTimeLeftInMills = COUNT_DOWN_TIME;
                startTimer();
                viewModel.verifyPhone(viewModel.getPhoneNumber());
            }
        });

        isValidOtp.observe(requireActivity(), aBoolean -> {
            if(aBoolean){
                loginNow();
            }
        });


    }

    private void startTimer(){
        if(mBinding == null) return;
        mBinding.txtResend.setEnabled(false);
        mBinding.txtResend.setTextColor(Color.BLACK);
        mBinding.txtCounter.setVisibility(View.VISIBLE);
        new CountDownTimer(mTimeLeftInMills, 1000){
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMills = millisUntilFinished;
                updateCancelTimer();
            }

            @Override
            public void onFinish() {
               if(mBinding != null){
                   mBinding.txtCounter.setText("01:59");
                   mBinding.txtCounter.setVisibility(View.GONE);
                   mBinding.txtResend.setEnabled(true);
                   mBinding.txtResend.setTextColor(Color.BLUE);
               }
            }
        }.start();
    }
    private void updateCancelTimer(){
        int minutes = (int) (mTimeLeftInMills / 1000) /60;// divided by 60 seconds
        int seconds = (int) (mTimeLeftInMills / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%01d:%02d", minutes, seconds);
        if(mBinding != null)mBinding.txtCounter.setText(timeLeftFormatted);

    }


    private void loginNow(){

        try{
            CommonUiUtils.closeKeyBoard(requireActivity());
            String phone = viewModel.getPhoneNumber();
            String otp   = mBinding.pinView.getValue();
            String pushToken = userSession.getPushToken();
            if(mBinding != null) mBinding.btnVerify.setEnabled(false);
            Map<String, String> deviceDetails = CommonUtils.getDeviceInfo(mContext);
            if (pushToken == null) {
                new AlertDialog.Builder(mContext).setTitle("Push Notification token is missing");
            }


            viewModel.loginByOtp(phone, otp, pushToken, null).observe(requireActivity(), resource -> {
                if (mBinding == null) return;
                switch (resource.status){
                    case LOADING:
                        mBinding.progress.setVisibility(View.VISIBLE);
                        break;
                    case ERROR:
                        mBinding.progress.setVisibility(View.GONE);
                        CommonUiUtils.showSnackBar(requireView(), resource.message);
                        break;
                    case SUCCESS:
                        mBinding.progress.setVisibility(View.GONE);
                        try{
                            ApiResponse<User> apiResponse = resource.data;
                            if(apiResponse != null && apiResponse.isSuccess()){
                                User user = apiResponse.getData();
                                user.setPushToken(userSession.getPushToken());
                                userSession.setUserData(user);
                                MainActivity.start(mContext);
                                mContext.finishAffinity();
                                mContext.finish();
                            }else {
                                String errorMessage = apiResponse != null ? apiResponse.getMessage() : "";
                                CommonUiUtils.showSnackBar(requireView(), errorMessage);
                            }
                        }catch (Throwable t){
                            t.printStackTrace();
                            CommonUiUtils.showSnackBar(requireView(), "Invalid Server response format");
                        }
                        break;
                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }
    }



    @Override
    public Class<AuthViewModel> getViewModel() {
        return AuthViewModel.class;
    }

    @Override
    public FragmentOtpBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentOtpBinding.inflate(inflater, container, false);
    }

    @Override
    public AuthRepositoryImpl getRepository() {
        return new AuthRepositoryImpl(remoteDataSource.buildApi(Api.class), userSession);
    }





}