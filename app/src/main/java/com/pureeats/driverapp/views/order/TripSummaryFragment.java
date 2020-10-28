package com.pureeats.driverapp.views.order;

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

import com.pureeats.driverapp.R;
import com.pureeats.driverapp.adapters.EarningListAdapter;
import com.pureeats.driverapp.adapters.TripListAdapter;
import com.pureeats.driverapp.databinding.FragmentEarningHistoryBinding;
import com.pureeats.driverapp.databinding.FragmentTripSummaryBinding;
import com.pureeats.driverapp.models.Earning;
import com.pureeats.driverapp.models.request.RequestToken;
import com.pureeats.driverapp.models.response.TripDetails;
import com.pureeats.driverapp.viewmodels.LocationViewModel;
import com.pureeats.driverapp.viewmodels.OrderViewModel;

import java.util.List;

public class TripSummaryFragment extends Fragment {
    private final String TAG = this.getClass().getSimpleName();

    private FragmentTripSummaryBinding mBinding;
    OrderViewModel orderViewModel;
    NavController navController;
    TripListAdapter tripListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentTripSummaryBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);
        RequestToken requestToken = new RequestToken(requireActivity());

        // Initialize ViewModel
        orderViewModel = new ViewModelProvider(requireActivity()).get(OrderViewModel.class);
        orderViewModel.init();

        // Initialize NavController
        navController = Navigation.findNavController(rootView);

        // Initialize RecyclerView
        tripListAdapter = new TripListAdapter();
        mBinding.tripRecycler.setAdapter(tripListAdapter);

        orderViewModel.getIsLoading().observe(requireActivity(), aBoolean -> {
            if(aBoolean)mBinding.progressbar.setVisibility(View.VISIBLE);
            else mBinding.progressbar.setVisibility(View.GONE);
        });

        orderViewModel.getTripSummary(requestToken).observe(requireActivity(), listApiResponse -> {
            List<TripDetails> tripDetailsList = listApiResponse.getData();
            Log.d(TAG, "TRIPS: "+tripDetailsList);
            tripListAdapter.submitList(tripDetailsList);
        });
    }
}