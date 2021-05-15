package com.pureeats.driverapp.views.auth;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.pureeats.driverapp.R;
import com.pureeats.driverapp.commons.ErrorCode;
import com.pureeats.driverapp.databinding.FragmentAuthBinding;
import com.pureeats.driverapp.models.ApiResponse;
import com.pureeats.driverapp.network.api.Api;
import com.pureeats.driverapp.repositories.AuthRepositoryImpl;
import com.pureeats.driverapp.utils.CommonUiUtils;
import com.pureeats.driverapp.utils.CommonUtils;
import com.pureeats.driverapp.viewmodels.AuthViewModel;
import com.pureeats.driverapp.views.base.BaseDialogFragment;

public class AuthFragment extends BaseDialogFragment<AuthViewModel, FragmentAuthBinding, AuthRepositoryImpl> {
    private final String TAG = getClass().getSimpleName();
    NavController navController;

    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);
        navController = Navigation.findNavController(rootView);
        mBinding.btnVerify.setOnClickListener(view -> verifyPhoneNumber());
    }


    private void verifyPhoneNumber(){
        CommonUiUtils.closeKeyBoard(mContext);
        String phone = mBinding.etPhone.getText() != null ? mBinding.etPhone.getText().toString().trim() : "";
        phone = CommonUtils.trimPhoneNumber(phone);
        if(CommonUtils.isValidPhoneNumber(phone)){
            mBinding.btnVerify.setEnabled(false);
            viewModel.verifyPhone(phone).observe(requireActivity(), resource -> {
                switch (resource.status){
                    case LOADING:
                        break;
                    case ERROR:
                        mBinding.btnVerify.setEnabled(true);
                        CommonUiUtils.showSnackBar(getView(), resource.message);
                        break;
                    case SUCCESS:
                        ApiResponse<Object> apiResponse = resource.data;
                        if(apiResponse.isSuccess()){
                            try{
                                navController.navigate(R.id.otpFragment);
                            }catch (Exception e){
                                CommonUiUtils.showSnackBar(requireView(), "Exception occur during navigation");
                                e.printStackTrace();
                            }
                        }else{
                            ErrorCode errorCode = ErrorCode.getErrorCode(apiResponse.getCode());
                            CommonUiUtils.showSnackBar(getView(), errorCode.getMessage());
                        }
                        break;
                }





            });
        }else{
            mBinding.etPhone.setError("Invalid Mobile number");
        }
    }

    @Override
    public Class<AuthViewModel> getViewModel() {
        return AuthViewModel.class;
    }

    @Override
    public FragmentAuthBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentAuthBinding.inflate(inflater, container, false);
    }

    @Override
    public AuthRepositoryImpl getRepository() {
        return new AuthRepositoryImpl(remoteDataSource.buildApi(Api.class), userSession);
    }
}