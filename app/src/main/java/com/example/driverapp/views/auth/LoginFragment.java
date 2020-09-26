package com.example.driverapp.views.auth;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.driverapp.databinding.FragmentLoginBinding;
import com.example.driverapp.R;
import com.example.driverapp.viewmodels.AuthenticationViewModel;
import com.example.driverapp.views.MainActivity;

public class LoginFragment extends Fragment {
    private final String TAG = this.getClass().getSimpleName();

    private FragmentLoginBinding mBinding;
    AuthenticationViewModel authenticationViewModel;
    NavController navController;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentLoginBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        // Initialize ViewModel
        authenticationViewModel = new ViewModelProvider(requireActivity()).get(AuthenticationViewModel.class);
        authenticationViewModel.init();

        // Initialize NavController
        navController = Navigation.findNavController(rootView);

        mBinding.etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() == 10) {
                    mBinding.btnSendOtp.setBackgroundResource(R.drawable.ripple_orange);
                    mBinding.btnSendOtp.setEnabled(true);
                }
                else {
                    mBinding.btnSendOtp.setBackgroundColor(Color.parseColor("#E3E3E3"));
                    mBinding.btnSendOtp.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mBinding.btnSendOtp.setOnClickListener(view -> {
            String phone = mBinding.etPhone.getText() != null ? mBinding.etPhone.getText().toString() : null;
            if(phone != null) {
                mBinding.btnSendOtp.setBackgroundColor(Color.parseColor("#E3E3E3"));
                authenticationViewModel.setPhoneNumber(phone);
                navController.navigate(R.id.action_otpSentFragment_to_loginUsingPasswordFragment);

//                authenticationViewModel.sendLoginOtp(phone).observe(getViewLifecycleOwner(), apiResponse -> {
//                    if(apiResponse.isSuccess()){
//                        navController.navigate(R.id.action_loginUsingPasswordFragment_to_loginUsingOTPFragment);
//                    }
//                    else {
//                        if(authenticationViewModel.getIsLoading().getValue() == false){
//                            String message = apiResponse.getMessage();
//                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });

            }
        });

        mBinding.signup.setOnClickListener(view -> {
            navController.navigate(R.id.action_otpSentFragment_to_signupFragment);
        });

        mBinding.layoutHeader.setOnClickListener(view -> {
            startActivity(new Intent(requireActivity(), MainActivity.class));
        });



        authenticationViewModel.getIsLoading().observe(getViewLifecycleOwner(), aBoolean -> {
            if(aBoolean) {
                mBinding.progress.setVisibility(View.VISIBLE);
            }
            else {
                mBinding.progress.setVisibility(View.GONE);
            }
        });



    }
}