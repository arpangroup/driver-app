package com.example.driverapp.views.order;

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

import com.example.driverapp.R;
import com.example.driverapp.databinding.FragmentReachDropLocationBinding;
import com.example.driverapp.databinding.FragmentReachPickUpLocationBinding;
import com.example.driverapp.viewmodels.AuthenticationViewModel;

public class ReachDropLocationFragment extends Fragment {
    private final String TAG = this.getClass().getSimpleName();

    private FragmentReachDropLocationBinding mBinding;
    AuthenticationViewModel authenticationViewModel;
    NavController navController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentReachDropLocationBinding.inflate(inflater, container, false);
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

        mBinding.reachDropLocation.toolbar.title.setText("Reach Drop Location");

    }
}