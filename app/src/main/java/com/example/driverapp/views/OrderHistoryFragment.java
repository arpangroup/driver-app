package com.example.driverapp.views;

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
import com.example.driverapp.adapters.AcceptedOrderListAdapter;
import com.example.driverapp.adapters.ItemClickInterface;
import com.example.driverapp.adapters.OrderListAdapter;
import com.example.driverapp.databinding.FragmentAcceptedOrderListBinding;
import com.example.driverapp.databinding.FragmentOrderHistoryBinding;
import com.example.driverapp.databinding.FragmentProfileBinding;
import com.example.driverapp.models.Order;
import com.example.driverapp.models.response.OrderDetailsView;
import com.example.driverapp.viewmodels.LocationViewModel;
import com.example.driverapp.viewmodels.OrderViewModel;

import java.util.Collections;
import java.util.List;

public class OrderHistoryFragment extends Fragment implements ItemClickInterface {
    private final String TAG = this.getClass().getSimpleName();

    private FragmentOrderHistoryBinding mBinding;
    OrderViewModel orderViewModel;
    LocationViewModel locationViewModel;
    NavController navController;
    OrderListAdapter orderListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentOrderHistoryBinding.inflate(inflater, container, false);
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
        orderListAdapter = new OrderListAdapter(this);
        mBinding.orderRecycler.setAdapter(orderListAdapter);


        orderViewModel.getIsLoading().observe(requireActivity(), aBoolean -> {
            if(aBoolean)mBinding.progressbar.setVisibility(View.VISIBLE);
            else mBinding.progressbar.setVisibility(View.GONE);
        });

        orderViewModel.getUsersOrderStatistics().observe(requireActivity(), updateDeliveryUserInfoResponse -> {
            List<OrderDetailsView> orders = updateDeliveryUserInfoResponse.getOrders();
            orderListAdapter.submitList(orders);
        });
    }

    @Override
    public void onItemVClick(Order order) {

    }
}