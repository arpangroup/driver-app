package com.pureeats.driverapp.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.pureeats.driverapp.adapters.OrderListAdapter;
import com.pureeats.driverapp.databinding.FragmentOrderHistoryBinding;
import com.pureeats.driverapp.databinding.FragmentProfileBinding;
import com.pureeats.driverapp.models.ApiResponse;
import com.pureeats.driverapp.models.Order;
import com.pureeats.driverapp.models.response.OrderDetailsView;
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


public class OrderHistoryFragment extends BaseDialogFragment<OrderViewModel, FragmentOrderHistoryBinding, OrderRepositoryImpl> {
    private final String TAG = getClass().getName();
    private NavController navController;
    private List<OrderDetailsView> orderList = new ArrayList<>();
    OrderListAdapter adapter;

    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);
        navController = Navigation.findNavController(rootView);
        adapter = new OrderListAdapter(orderList);
        mBinding.orderRecycler.setAdapter(adapter);
        observeViewModel();
    }

    private void observeViewModel(){
        viewModel.getUsersOrderStatistics().observe(requireActivity(), resource -> {
            if(mBinding == null) return;
            switch (resource.status){
                case LOADING:
                    mBinding.setIsLoading(true);
                    break;
                case ERROR:
                    mBinding.setIsLoading(false);
                    break;
                case SUCCESS:
                    mBinding.setIsLoading(false);
                    ApiResponse<UpdateDeliveryUserInfoResponse> apiResponse = resource.data;
                    if(apiResponse != null && apiResponse.isSuccess()){
                        adapter.updateAll(apiResponse.getData().getOrders());
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
    public FragmentOrderHistoryBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentOrderHistoryBinding.inflate(inflater, container, false);
    }

    @Override
    public OrderRepositoryImpl getRepository() {
        return new OrderRepositoryImpl(remoteDataSource.buildApi(Api.class), userSession);
    }
}