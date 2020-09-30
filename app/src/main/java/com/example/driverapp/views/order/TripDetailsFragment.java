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
import com.example.driverapp.databinding.FragmentPickOrderBinding;
import com.example.driverapp.databinding.FragmentTripDetailsBinding;
import com.example.driverapp.models.Order;
import com.example.driverapp.viewmodels.OrderViewModel;

public class TripDetailsFragment extends Fragment {
    private final String TAG = this.getClass().getSimpleName();

    private FragmentTripDetailsBinding mBinding;
    OrderViewModel orderViewModel;
    NavController navController;
    Order mOrder = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentTripDetailsBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        // Initialize ViewModel
        orderViewModel = new ViewModelProvider(requireActivity()).get(OrderViewModel.class);
        orderViewModel.init();

        // Initialize NavController
        navController = Navigation.findNavController(rootView);
        mBinding.tripDetails.toolbar.title.setText("Trip Summary");

        mBinding.tripDetails.btnFinish.setOnClickListener(view -> requireActivity().finish());

    }
}