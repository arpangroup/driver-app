package com.pureeats.driverapp.views.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pureeats.driverapp.adapters.EarningListAdapter;
import com.pureeats.driverapp.databinding.ActivityMainBinding;
import com.pureeats.driverapp.databinding.FragmentEarningHistoryBinding;
import com.pureeats.driverapp.models.ApiResponse;
import com.pureeats.driverapp.models.Earning;
import com.pureeats.driverapp.models.response.UpdateDeliveryUserInfoResponse;
import com.pureeats.driverapp.network.api.Api;
import com.pureeats.driverapp.repositories.AuthRepositoryImpl;
import com.pureeats.driverapp.repositories.BaseRepository;
import com.pureeats.driverapp.repositories.OrderRepositoryImpl;
import com.pureeats.driverapp.viewmodels.BaseViewModel;
import com.pureeats.driverapp.viewmodels.OrderViewModel;
import com.pureeats.driverapp.views.base.BaseDialogFragment;

import java.util.ArrayList;
import java.util.List;


public class EarningHistoryFragment extends BaseDialogFragment<OrderViewModel, FragmentEarningHistoryBinding, OrderRepositoryImpl> {
    private final String TAG = getClass().getName();
    private List<Earning> earningList = new ArrayList<>();
    EarningListAdapter adapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new EarningListAdapter(earningList);
        mBinding.earningRecycler.setAdapter(adapter);
        observeViewModel();
    }

    private void observeViewModel(){
        viewModel.getUsersOrderStatistics().observe(requireActivity(), resource -> {
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
                    ApiResponse<UpdateDeliveryUserInfoResponse> apiResponse = resource.data;
                    if(apiResponse.isSuccess()){
                        List<Earning> earnings = apiResponse.getData().getEarnings();
                        Log.d(TAG, "EARNINGS: "+earnings);
                        adapter.updateAll(earnings);
                    }else{

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
    public FragmentEarningHistoryBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentEarningHistoryBinding.inflate(inflater, container, false);
    }

    @Override
    public OrderRepositoryImpl getRepository() {
        return new OrderRepositoryImpl(remoteDataSource.buildApi(Api.class), userSession);
    }
}