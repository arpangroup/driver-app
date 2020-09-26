package com.example.driverapp.views.auth;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.driverapp.databinding.FragmentLoginUsingOTPBinding;
import com.example.driverapp.R;
import com.example.driverapp.viewmodels.AuthenticationViewModel;

public class LoginUsingOTPFragment extends Fragment {
    private final String TAG = this.getClass().getSimpleName();
    private static long COUNT_DOWN_TIME = 1 * 60 *1000; // 1 Min;

    private FragmentLoginUsingOTPBinding mBinding;
    AuthenticationViewModel authenticationViewModel;
    NavController navController;

    private long mTimeLeftInMills = COUNT_DOWN_TIME;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navController.popBackStack();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentLoginUsingOTPBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        // Initialize ViewModel
        authenticationViewModel = new ViewModelProvider(requireActivity()).get(AuthenticationViewModel.class);
        authenticationViewModel.init();
        initOTPInput();

        // Initialize NavController
        navController = Navigation.findNavController(rootView);

        mBinding.loginWithPassword.setOnClickListener(view -> {
            navController.navigate(R.id.action_loginUsingOTPFragment_to_loginUsingPasswordFragment);
        });

        // Injecting the viewmodel to the view for data-binding
        //mBinding.setAuthViewModel(authenticationViewModel);
    }


    private void initOTPInput() {
        mBinding.et1.requestFocus();
        //requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mBinding.et1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(mBinding.et1.getText().toString().trim().length() == 1){
                    mBinding.et2.requestFocus();
                    isValidOTP();
                }
            }
        });
        mBinding.et2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(mBinding.et2.getText().toString().trim().length() == 1){
                    mBinding.et3.requestFocus();
                    isValidOTP();
                }
            }
        });
        mBinding.et3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(mBinding.et3.getText().toString().trim().length() == 1){
                    mBinding.et4.requestFocus();
                    isValidOTP();
                }
            }
        });
        mBinding.et4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(mBinding.et4.getText().toString().trim().length() == 1){
                    mBinding.et5.requestFocus();
                    isValidOTP();
                }
            }
        });
        mBinding.et5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(mBinding.et5.getText().toString().trim().length() == 1){
                    //mBinding.et6.requestFocus();
                    isValidOTP();
                }
            }
        });


        mBinding.et5.setOnKeyListener((view, keyCode, keyEvent) -> {
            if(keyCode == KeyEvent.KEYCODE_DEL){
                mBinding.et5.setText(null);
                isValidOTP();
                mBinding.et4.requestFocus();
            }
            return false;

        });
        mBinding.et4.setOnKeyListener((view, keyCode, keyEvent) -> {
            if(keyCode == KeyEvent.KEYCODE_DEL){
                mBinding.et4.setText(null);
                isValidOTP();
                mBinding.et3.requestFocus();
            }
            return false;

        });
        mBinding.et3.setOnKeyListener((view, keyCode, keyEvent) -> {
            if(keyCode == KeyEvent.KEYCODE_DEL){
                mBinding.et3.setText(null);
                isValidOTP();
                mBinding.et2.requestFocus();
            }
            return false;

        });
        mBinding.et2.setOnKeyListener((view, keyCode, keyEvent) -> {
            if(keyCode == KeyEvent.KEYCODE_DEL){
                mBinding.et2.setText(null);
                isValidOTP();
                mBinding.et1.requestFocus();
            }
            return false;

        });

    }
    private boolean isValidOTP(){
        if(mBinding.et1.getText().length()==1 && mBinding.et2.getText().length()==1 && mBinding.et3.getText().length()==1 && mBinding.et4.getText().length()==1 && mBinding.et5.getText().length()==1){
            mBinding.btnVerify.setEnabled(true);
            mBinding.btnVerify.setBackgroundResource(R.drawable.ripple_orange);
            loginNow();
            return true;
        }else{
            mBinding.btnVerify.setEnabled(false);
            mBinding.btnVerify.setBackgroundResource(R.drawable.ripple);
            return false;
        }

    }

    private void loginNow() {
    }


}