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
import com.example.driverapp.databinding.FragmentAcceptedOrderListBinding;
import com.example.driverapp.databinding.FragmentProfileBinding;
import com.example.driverapp.models.Order;
import com.example.driverapp.services.FetchOrderService;
import com.example.driverapp.viewmodels.LocationViewModel;
import com.example.driverapp.viewmodels.OrderViewModel;

public class AcceptedOrderListFragment extends Fragment implements ItemClickInterface {
    private final String TAG = this.getClass().getSimpleName();

    private FragmentAcceptedOrderListBinding mBinding;
    OrderViewModel orderViewModel;
    LocationViewModel locationViewModel;
    NavController navController;
    AcceptedOrderListAdapter orderListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentAcceptedOrderListBinding.inflate(inflater, container, false);
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
        orderListAdapter = new AcceptedOrderListAdapter(this);
        mBinding.orderRecycler.setAdapter(orderListAdapter);

        FetchOrderService.mutableAcceptedOrders.observe(requireActivity(), orders -> {
            if(orders.size() != orderListAdapter.getItemCount()){
                orderListAdapter.submitList(orders);
            }
        });
    }

    @Override
    public void onItemVClick(Order order) {

    }
}