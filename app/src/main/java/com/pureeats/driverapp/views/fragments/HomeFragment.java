package com.pureeats.driverapp.views.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.common.util.CollectionUtils;
import com.pureeats.driverapp.databinding.FragmentHomeBinding;
import com.pureeats.driverapp.models.ApiResponse;
import com.pureeats.driverapp.models.ChartData;
import com.pureeats.driverapp.models.response.Dashboard;
import com.pureeats.driverapp.network.api.Api;
import com.pureeats.driverapp.repositories.OrderRepositoryImpl;
import com.pureeats.driverapp.utils.CommonUiUtils;
import com.pureeats.driverapp.viewmodels.OrderViewModel;
import com.pureeats.driverapp.views.base.BaseDialogFragment;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends BaseDialogFragment<OrderViewModel, FragmentHomeBinding, OrderRepositoryImpl> {
    private final String TAG = getClass().getName();
    private NavController navController;

    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);
        navController = Navigation.findNavController(rootView);
        observeViewModel();
    }

    @Override
    public void onResume() {
        super.onResume();
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
                       Dashboard dashboard = apiResponse.getData();
                        mBinding.setDashboard(dashboard);
                        setUpBarChart(dashboard.getChartData());
                   }else {
                       CommonUiUtils.showSnackBar(getView(), apiResponse.getMessage());
                   }
                   break;
           }
        });
    }

    private void setUpBarChart(List<ChartData> chartDataList){
        if(CollectionUtils.isEmpty(chartDataList)) return;
        Log.d(TAG, "CHART_DATA_LIST......" );
        chartDataList.stream().forEach(chartData -> {
            System.out.println(chartData.getX() + ":" + chartData.getY());
        });
        ArrayList<BarEntry> visitors = new ArrayList<>();
//        visitors.add(new BarEntry(2014, 420));
//        visitors.add(new BarEntry(2015, 475));
//        visitors.add(new BarEntry(2016, 300));
//        visitors.add(new BarEntry(2017, 500));
//        visitors.add(new BarEntry(2018, 200));
//        visitors.add(new BarEntry(2019, 300));
//        visitors.add(new BarEntry(2020, 470));
        for (int i=0; i< chartDataList.size(); i++){
            visitors.add(new BarEntry(i, chartDataList.get(i).getY()));
        }

        BarDataSet barDataSet =  new BarDataSet(visitors, "Visitors");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);

        BarData barData  = new BarData(barDataSet);

        //String[] days = new String[] {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        String[] days = chartDataList.stream().map(ChartData::getX).toArray(String[]::new);
        XAxis xAxis = mBinding.barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(days));
        xAxis.setCenterAxisLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1);
        xAxis.setGranularityEnabled(true);





        mBinding.barChart.setFitBars(true);
        mBinding.barChart.setData(barData);
        mBinding.barChart.getDescription().setText("Earnings");
        mBinding.barChart.animateY(2000);
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