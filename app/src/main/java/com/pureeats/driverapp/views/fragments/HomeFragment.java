package com.pureeats.driverapp.views.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.pureeats.driverapp.R;
import com.pureeats.driverapp.databinding.FragmentHomeBinding;
import com.pureeats.driverapp.databinding.FragmentOtpBinding;
import com.pureeats.driverapp.models.ApiResponse;
import com.pureeats.driverapp.models.response.Dashboard;
import com.pureeats.driverapp.network.api.Api;
import com.pureeats.driverapp.repositories.AuthRepositoryImpl;
import com.pureeats.driverapp.repositories.BaseRepository;
import com.pureeats.driverapp.repositories.OrderRepositoryImpl;
import com.pureeats.driverapp.utils.CommonUiUtils;
import com.pureeats.driverapp.viewmodels.AuthViewModel;
import com.pureeats.driverapp.viewmodels.BaseViewModel;
import com.pureeats.driverapp.viewmodels.OrderViewModel;
import com.pureeats.driverapp.views.base.BaseDialogFragment;
import com.pureeats.driverapp.views.order.VerifyBillDialog;

import java.util.ArrayList;


public class HomeFragment extends BaseDialogFragment<OrderViewModel, FragmentHomeBinding, OrderRepositoryImpl> {
    private final String TAG = getClass().getName();
    private NavController navController;

    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);
        navController = Navigation.findNavController(rootView);
        observeViewModel();
    }

    private void observeViewModel(){
        viewModel.getDashboard().observe(mContext, resource -> {
            if(mBinding == null) return;
           switch (resource.status){
               case LOADING:
                   break;
               case ERROR:
                   break;
               case SUCCESS:
                   ApiResponse<Dashboard> apiResponse = resource.data;
                   if(apiResponse.isSuccess()){
                        mBinding.setDashboard(apiResponse.getData());
                   }else {
                       CommonUiUtils.showSnackBar(getView(), apiResponse.getMessage());
                   }
                   break;
           }
        });
    }

    private void setUpBarChart(){
        ArrayList<BarEntry> visitors = new ArrayList<>();
        visitors.add(new BarEntry(2014, 420));
        visitors.add(new BarEntry(2015, 475));
        visitors.add(new BarEntry(2016, 300));
        visitors.add(new BarEntry(2017, 500));
        visitors.add(new BarEntry(2018, 200));
        visitors.add(new BarEntry(2019, 300));
        visitors.add(new BarEntry(2020, 470));

        BarDataSet barDataSet =  new BarDataSet(visitors, "Visitors");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);

        BarData barData  = new BarData(barDataSet);

        mBinding.layoutDeliveredOrders.barChart.setFitBars(true);
        mBinding.layoutDeliveredOrders.barChart.setData(barData);
        mBinding.layoutDeliveredOrders.barChart.getDescription().setText("Earnings");
        mBinding.layoutDeliveredOrders.barChart.animateY(2000);
    }

    @Override
    public Class<OrderViewModel> getViewModel() {
        return OrderViewModel.class;
    }

    @Override
    public FragmentHomeBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentHomeBinding.inflate(inflater, container, false);
    }

    @Override
    public OrderRepositoryImpl getRepository() {
        return new OrderRepositoryImpl(remoteDataSource.buildApi(Api.class), userSession);
    }
}