package com.pureeats.driverapp.views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pureeats.driverapp.adapters.LoginHistoryAdapter;
import com.pureeats.driverapp.adapters.OrderListAdapter;
import com.pureeats.driverapp.databinding.FragmentLoginHistoryBinding;
import com.pureeats.driverapp.databinding.FragmentProfileBinding;
import com.pureeats.driverapp.models.request.RequestToken;
import com.pureeats.driverapp.viewmodels.AuthenticationViewModel;
import com.pureeats.driverapp.viewmodels.LocationViewModel;
import com.pureeats.driverapp.viewmodels.OrderViewModel;

public class LoginHistoryFragment extends Fragment {
    private final String TAG = this.getClass().getSimpleName();

    private FragmentLoginHistoryBinding mBinding;
    AuthenticationViewModel authViewModel;
    LocationViewModel locationViewModel;
    NavController navController;
    LoginHistoryAdapter loginHistoryAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentLoginHistoryBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        // Initialize ViewModel
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthenticationViewModel.class);
        authViewModel.init();

        // Initialize NavController
        navController = Navigation.findNavController(rootView);

        // Initialize RecyclerView
        loginHistoryAdapter = new LoginHistoryAdapter();
        mBinding.loginHistoryRecycler.setAdapter(loginHistoryAdapter);

        authViewModel.getIsLoading().observe(requireActivity(), aBoolean -> {
            if(aBoolean) mBinding.progressbar.setVisibility(View.VISIBLE);
            else mBinding.progressbar.setVisibility(View.GONE);
        });

        RequestToken requestToken = new RequestToken(requireActivity());
        authViewModel.getLoginHistory(requestToken).observe(requireActivity(), loginHistories -> {
            loginHistoryAdapter.submitList(loginHistories);
        });
    }
}