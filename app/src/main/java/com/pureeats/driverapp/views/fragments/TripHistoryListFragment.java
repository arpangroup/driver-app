package com.pureeats.driverapp.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pureeats.driverapp.adapters.TripListAdapter;
import com.pureeats.driverapp.databinding.FragmentTripHistoryListBinding;
import com.pureeats.driverapp.models.ApiResponse;
import com.pureeats.driverapp.models.response.TripDetails;
import com.pureeats.driverapp.network.api.Api;
import com.pureeats.driverapp.repositories.AuthRepositoryImpl;
import com.pureeats.driverapp.repositories.BaseRepository;
import com.pureeats.driverapp.repositories.OrderRepositoryImpl;
import com.pureeats.driverapp.viewmodels.BaseViewModel;
import com.pureeats.driverapp.viewmodels.OrderViewModel;
import com.pureeats.driverapp.views.base.BaseDialogFragment;

import java.util.ArrayList;
import java.util.List;


public class TripHistoryListFragment extends BaseDialogFragment<OrderViewModel, FragmentTripHistoryListBinding, OrderRepositoryImpl> {
    private final String TAG = getClass().getName();
    private List<TripDetails> tripList = new ArrayList<>();
    private TripListAdapter adapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new TripListAdapter(tripList);
        mBinding.tripRecycler.setAdapter(adapter);
        observeViewModel();
    }

    private void observeViewModel(){

        viewModel.getTripSummary().observe(mContext, resource -> {
            if(mBinding == null) return;
            switch (resource.status){
                case LOADING:
                    mBinding.progressbar.setVisibility(View.VISIBLE);
                    break;
                case ERROR:
                    mBinding.progressbar.setVisibility(View.GONE);
                    break;
                case SUCCESS:
                    mBinding.progressbar.setVisibility(View.GONE);
                    ApiResponse<List<TripDetails>> apiResponse = resource.data;
                    if(apiResponse != null && apiResponse.isSuccess()){
                        adapter.updateAll(apiResponse.getData());
                    }
                    break;
            }
        });
    }

    @Override
    public Class<OrderViewModel> getViewModel() {
        return OrderViewModel.class;
    }

    @Override
    public FragmentTripHistoryListBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentTripHistoryListBinding.inflate(inflater, container, false);
    }

    @Override
    public OrderRepositoryImpl getRepository() {
        return new OrderRepositoryImpl(remoteDataSource.buildApi(Api.class), userSession);
    }
}