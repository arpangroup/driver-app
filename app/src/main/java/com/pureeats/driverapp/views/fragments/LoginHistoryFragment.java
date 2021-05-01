package com.pureeats.driverapp.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.pureeats.driverapp.adapters.LoginHistoryAdapter;
import com.pureeats.driverapp.databinding.FragmentLoginHistoryBinding;
import com.pureeats.driverapp.models.response.LoginHistory;
import com.pureeats.driverapp.network.api.Api;
import com.pureeats.driverapp.repositories.AuthRepositoryImpl;
import com.pureeats.driverapp.repositories.BaseRepository;
import com.pureeats.driverapp.viewmodels.AuthViewModel;
import com.pureeats.driverapp.viewmodels.BaseViewModel;
import com.pureeats.driverapp.views.base.BaseDialogFragment;

import java.util.ArrayList;
import java.util.List;


public class LoginHistoryFragment extends BaseDialogFragment<AuthViewModel, FragmentLoginHistoryBinding, AuthRepositoryImpl> {
    private final String TAG = getClass().getName();
    private NavController navController;
    private List<LoginHistory> loginHistoryList = new ArrayList<>();
    private LoginHistoryAdapter loginHistoryAdapter;

    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);
        navController = Navigation.findNavController(rootView);
        // Initialize RecyclerView
        loginHistoryAdapter = new LoginHistoryAdapter(loginHistoryList);
        mBinding.loginHistoryRecycler.setAdapter(loginHistoryAdapter);

    }

    private void observeViewModel(){
        viewModel.getLoginHistory().observe(mContext, resource ->{
            switch (resource.status){
                case LOADING:
                    break;
                case ERROR:
                    break;
                case SUCCESS:
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