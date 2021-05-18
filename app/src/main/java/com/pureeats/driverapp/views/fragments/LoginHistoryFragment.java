package com.pureeats.driverapp.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.common.util.CollectionUtils;
import com.pureeats.driverapp.adapters.LoginHistoryAdapter;
import com.pureeats.driverapp.databinding.FragmentLoginHistoryBinding;
import com.pureeats.driverapp.models.ApiResponse;
import com.pureeats.driverapp.models.response.LoginHistory;
import com.pureeats.driverapp.network.api.Api;
import com.pureeats.driverapp.repositories.AuthRepositoryImpl;
import com.pureeats.driverapp.utils.CommonUiUtils;
import com.pureeats.driverapp.viewmodels.AuthViewModel;
import com.pureeats.driverapp.views.base.BaseDialogFragment;

import java.util.ArrayList;
import java.util.List;


public class LoginHistoryFragment extends BaseDialogFragment<AuthViewModel, FragmentLoginHistoryBinding, AuthRepositoryImpl> {
    private final String TAG = getClass().getName();
    private NavController navController;
    private List<LoginHistory> loginHistoryList = new ArrayList<>();
    private LoginHistoryAdapter adapter;

    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);
        navController = Navigation.findNavController(rootView);
        // Initialize RecyclerView
        adapter = new LoginHistoryAdapter(loginHistoryList);
        mBinding.loginHistoryRecycler.setAdapter(adapter);
        observeViewModel();

    }

    private void observeViewModel(){
        viewModel.getLoginHistory().observe(mContext, resource ->{
            if(mBinding == null) return;
            switch (resource.status){
                case LOADING:
                    mBinding.progressbar.setVisibility(View.VISIBLE);
                    break;
                case ERROR:
                    mBinding.progressbar.setVisibility(View.GONE);
                    CommonUiUtils.showSnackBar(getView(), resource.message);
                    break;
                case SUCCESS:
                    mBinding.progressbar.setVisibility(View.GONE);
                    if (!CollectionUtils.isEmpty(resource.data)){
                        adapter.updateAll(resource.data);
                    }
                    break;
            }
        });
    }

    @Override
    public Class<AuthViewModel> getViewModel() {
        return AuthViewModel.class;
    }

    @Override
    public FragmentLoginHistoryBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentLoginHistoryBinding.inflate(inflater, container, false);
    }

    @Override
    public AuthRepositoryImpl getRepository() {
        return new AuthRepositoryImpl(remoteDataSource.buildApi(Api.class), userSession);
    }
}