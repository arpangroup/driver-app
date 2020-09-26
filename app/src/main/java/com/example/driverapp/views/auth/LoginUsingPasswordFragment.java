package com.example.driverapp.views.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

import com.example.driverapp.R;
import com.example.driverapp.databinding.FragmentLoginUsingPasswordBinding;
import com.example.driverapp.models.Address;
import com.example.driverapp.models.request.RequestToken;
import com.example.driverapp.viewmodels.AuthenticationViewModel;
import com.example.driverapp.views.MainActivity;

public class LoginUsingPasswordFragment extends Fragment {
    private final String TAG = this.getClass().getSimpleName();

    private FragmentLoginUsingPasswordBinding mBinding;
    AuthenticationViewModel authenticationViewModel;
    NavController navController;

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
        mBinding = FragmentLoginUsingPasswordBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        // Initialize ViewModel
        authenticationViewModel = new ViewModelProvider(requireActivity()).get(AuthenticationViewModel.class);
        authenticationViewModel.init();
        initOTPInput();
        mBinding.layoutAlert.setVisibility(View.GONE);

        // Initialize NavController
        navController = Navigation.findNavController(rootView);

        authenticationViewModel.getIsLoading().observe(getViewLifecycleOwner(), aBoolean -> {
            if(aBoolean) {
                mBinding.btnLogin.setEnabled(false);
                mBinding.progress.setVisibility(View.VISIBLE);
            }
            else {
                mBinding.btnLogin.setEnabled(true);
                mBinding.progress.setVisibility(View.GONE);
            }
        });

        authenticationViewModel.getPhoneNumber().observe(requireActivity(), phoneNumber -> {
            mBinding.txtPhone.setText(phoneNumber);
        });

        authenticationViewModel.getLoginAttempt().observe(requireActivity(), attemptCount -> {
            Log.d(TAG, "ATTEMPT: "+attemptCount);
            if(attemptCount > AuthenticationViewModel.MAX_LOGIN_ATTEMPT_COUNT){
                mBinding.txtAlertText.setText("Maximum attempt reached");
                mBinding.layoutAlert.setVisibility(View.VISIBLE);
                mBinding.et1.setEnabled(false);
                mBinding.et2.setEnabled(false);
                mBinding.et3.setEnabled(false);
                mBinding.et4.setEnabled(false);
                mBinding.et5.setEnabled(false);
                mBinding.et6.setEnabled(false);
                mBinding.btnLogin.setEnabled(false);
                mBinding.et1.setText(null);
                mBinding.et2.setText(null);
                mBinding.et3.setText(null);
                mBinding.et4.setText(null);
                mBinding.et5.setText(null);
                mBinding.et6.setText(null);
                mBinding.btnLogin.setBackgroundResource(R.drawable.ripple);
                mBinding.btnLogin.setEnabled(false);
            }
        });

        mBinding.loginWithOtp.setOnClickListener(view -> {
            navController.navigate(R.id.action_loginUsingPasswordFragment_to_loginUsingOTPFragment);
        });

        mBinding.btnLogin.setOnClickListener(view -> {
            if(isValidPassword()){
                mBinding.btnLogin.setEnabled(false);
                mBinding.btnLogin.setBackgroundResource(R.drawable.ripple);
                mBinding.layoutAlert.setVisibility(View.GONE);
                loginNow();
            }
        });

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
                    isValidPassword();
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
                    isValidPassword();
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
                    isValidPassword();
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
                    isValidPassword();
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
                    mBinding.et6.requestFocus();
                    isValidPassword();
                }
            }
        });
        mBinding.et6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(mBinding.et6.getText().toString().trim().length() == 1){
                    //mBinding.et6.requestFocus();
                    isValidPassword();
                }
            }
        });


        mBinding.et6.setOnKeyListener((view, keyCode, keyEvent) -> {
            if(keyCode == KeyEvent.KEYCODE_DEL){
                mBinding.et6.setText(null);
                isValidPassword();
                mBinding.et5.requestFocus();
            }
            return false;

        });
        mBinding.et5.setOnKeyListener((view, keyCode, keyEvent) -> {
            if(keyCode == KeyEvent.KEYCODE_DEL){
                mBinding.et5.setText(null);
                isValidPassword();
                mBinding.et4.requestFocus();
            }
            return false;

        });
        mBinding.et4.setOnKeyListener((view, keyCode, keyEvent) -> {
            if(keyCode == KeyEvent.KEYCODE_DEL){
                mBinding.et4.setText(null);
                isValidPassword();
                mBinding.et3.requestFocus();
            }
            return false;

        });
        mBinding.et3.setOnKeyListener((view, keyCode, keyEvent) -> {
            if(keyCode == KeyEvent.KEYCODE_DEL){
                mBinding.et3.setText(null);
                isValidPassword();
                mBinding.et2.requestFocus();
            }
            return false;

        });
        mBinding.et2.setOnKeyListener((view, keyCode, keyEvent) -> {
            if(keyCode == KeyEvent.KEYCODE_DEL){
                mBinding.et2.setText(null);
                isValidPassword();
                mBinding.et1.requestFocus();
            }
            return false;

        });

    }
    private boolean isValidPassword(){
        if(mBinding.et1.getText().length()==1 && mBinding.et2.getText().length()==1 && mBinding.et3.getText().length()==1 && mBinding.et4.getText().length()==1 && mBinding.et5.getText().length()==1 && mBinding.et6.getText().length()==1){
            mBinding.btnLogin.setEnabled(true);
            mBinding.btnLogin.setBackgroundResource(R.drawable.ripple_orange);
            //loginNow();
            return true;
        }else{
            mBinding.btnLogin.setEnabled(false);
            mBinding.btnLogin.setBackgroundResource(R.drawable.ripple);
            return false;
        }

    }

    private void loginNow() {
        String password   = mBinding.et1.getText().toString() + mBinding.et2.getText().toString() + mBinding.et3.getText().toString() + mBinding.et4.getText().toString() + mBinding.et5.getText().toString() + mBinding.et6.getText().toString();
        Log.d(TAG, "Inside loginNow().....");
        String phone = authenticationViewModel.getPhoneNumber().getValue();
        Address defaultAddress = null;
        authenticationViewModel.loginByMobileAndPassword(phone, password, defaultAddress).observe(getViewLifecycleOwner(), userLoginResponse -> {
            if(userLoginResponse.isSuccess()){
                Intent intent = new Intent(requireActivity(), MainActivity.class);
                startActivity(intent);
                requireActivity().finish();
                System.out.println("=========================LOGIN_RESPONSE========================");
                System.out.println(userLoginResponse);
                System.out.println("REQUEST_TOKEN:"+new RequestToken());

            }else{
                mBinding.layoutAlert.setVisibility(View.VISIBLE);
                mBinding.btnLogin.setEnabled(true);
                mBinding.btnLogin.setBackgroundResource(R.drawable.ripple_orange);
            }
        });
    }

}