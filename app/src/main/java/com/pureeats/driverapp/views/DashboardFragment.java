package com.pureeats.driverapp.views;

import android.graphics.Color;
import android.media.MediaPlayer;
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

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.pureeats.driverapp.R;
import com.pureeats.driverapp.databinding.FragmentAcceptOrderBinding;
import com.pureeats.driverapp.databinding.FragmentDashboardBinding;
import com.pureeats.driverapp.models.request.RequestToken;
import com.pureeats.driverapp.models.response.Dashboard;
import com.pureeats.driverapp.services.FetchOrderService;
import com.pureeats.driverapp.viewmodels.LocationViewModel;
import com.pureeats.driverapp.viewmodels.OrderViewModel;

import java.util.ArrayList;
import java.util.Timer;


public class DashboardFragment extends Fragment {private final String TAG = this.getClass().getSimpleName();

    private int mProgress = 0;
    private static long ORDER_ACCEPT_TIME = 2*1000;
    Timer mTimer;
    private MediaPlayer mMediaPlayer;

    private FragmentDashboardBinding mBinding;
    OrderViewModel orderViewModel;
    LocationViewModel locationViewModel;
    NavController navController;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentDashboardBinding.inflate(inflater, container, false);
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
        //mBinding.setOrder(orderViewModel.getOrder());

        orderViewModel.setOnGoingOrder(null);//important, otherwise new order popup will not show

        mBinding.btnSnackbarAction.setOnClickListener(view -> {
            navController.navigate(R.id.acceptedOrderListFragment);
        });

        FetchOrderService.mutableAcceptedOrders.observe(requireActivity(), orders -> {
            if(orders.size() > 0){
                mBinding.snackbar.setVisibility(View.VISIBLE);
                mBinding.snackbarTitle.setText("You have" + orders.size()+ " On going Orders");
                mBinding.snackbarDescription.setText(orders.size() + " orders yet to be deliver");
            }else{
                mBinding.snackbar.setVisibility(View.GONE);
            }
        });

        RequestToken requestToken = new RequestToken(requireActivity());
        orderViewModel.getDashboard(requestToken).observe(requireActivity(), apiResponse -> {
            Dashboard dashboard = (Dashboard) apiResponse.getData();
            mBinding.setDashboard(dashboard);
        });

        setUpBarChart();

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

        mBinding.barChart.setFitBars(true);
        mBinding.barChart.setData(barData);
        mBinding.barChart.getDescription().setText("Earnings");
        mBinding.barChart.animateY(2000);
    }
}