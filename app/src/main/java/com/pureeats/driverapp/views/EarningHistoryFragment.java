package com.pureeats.driverapp.views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pureeats.driverapp.adapters.EarningListAdapter;
import com.pureeats.driverapp.databinding.FragmentAcceptedOrderListBinding;
import com.pureeats.driverapp.databinding.FragmentEarningHistoryBinding;
import com.pureeats.driverapp.models.Earning;
import com.pureeats.driverapp.viewmodels.LocationViewModel;
import com.pureeats.driverapp.viewmodels.OrderViewModel;

import java.util.List;

public class EarningHistoryFragment extends Fragment {
    private final String TAG = this.getClass().getSimpleName();

    private FragmentEarningHistoryBinding mBinding;
    OrderViewModel orderViewModel;
    LocationViewModel locationViewModel;
    NavController navController;
    EarningListAdapter earningListAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentEarningHistoryBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        // Initialize ViewModel
        orderViewModel = new ViewModelProvider(requireActivity()).get(OrderViewModel.class);
        locationViewModel = new ViewModelProvider(requireActivity()).get(LocationViewModel.class);
        orderViewModel.init();

        // Initialize NavController
        navController = Navigation.findNavController(rootView);

        // Initialize RecyclerView
        earningListAdapter = new EarningListAdapter();
        mBinding.earningRecycler.setAdapter(earningListAdapter);

        orderViewModel.getIsLoading().observe(requireActivity(), aBoolean -> {
            if(aBoolean)mBinding.progressbar.setVisibility(View.VISIBLE);
            else mBinding.progressbar.setVisibility(View.GONE);
        });

        orderViewModel.getUsersOrderStatistics().observe(requireActivity(), updateDeliveryUserInfoResponse -> {
            List<Earning> earnings = updateDeliveryUserInfoResponse.getEarnings();
            Log.d(TAG, "EARNINGS: "+earnings);
            earningListAdapter.submitList(earnings);
        });
    }

}