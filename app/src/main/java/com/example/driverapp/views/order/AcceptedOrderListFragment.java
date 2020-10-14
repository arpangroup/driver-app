package com.example.driverapp.views.order;

import android.content.Intent;
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

import com.example.driverapp.R;
import com.example.driverapp.adapters.AcceptedOrderListAdapter;
import com.example.driverapp.adapters.ItemClickInterface;
import com.example.driverapp.commons.Actions;
import com.example.driverapp.commons.CommonUtils;
import com.example.driverapp.commons.Constants;
import com.example.driverapp.commons.OrderStatus;
import com.example.driverapp.databinding.FragmentAcceptedOrderListBinding;
import com.example.driverapp.databinding.FragmentProfileBinding;
import com.example.driverapp.directionhelpers.ConstructDirectionUrl;
import com.example.driverapp.directionhelpers.FetchURL;
import com.example.driverapp.models.Order;
import com.example.driverapp.services.FetchOrderService;
import com.example.driverapp.viewmodels.LocationViewModel;
import com.example.driverapp.viewmodels.OrderViewModel;
import com.example.driverapp.views.order.ProcessOrderActivityDialog;
import com.google.android.gms.common.util.CollectionUtils;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.util.Collections;
import java.util.List;

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

        orderViewModel.getIsLoading().observe(requireActivity(), aBoolean -> {
            if(aBoolean)mBinding.progressbar.setVisibility(View.VISIBLE);
            else mBinding.progressbar.setVisibility(View.GONE);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        orderViewModel.getDeliveryOrders().observe(requireActivity(), deliveryOrderResponse -> {
            List<Order> acceptedOrders = deliveryOrderResponse.getAcceptedOrders();
            Collections.sort(acceptedOrders);
            orderListAdapter.submitList(acceptedOrders);
        });
    }

    @Override
    public void onItemVClick(Order order) {
        int orderStatus = order.getOrderStatusId();
        Log.d(TAG, "ORDER: " + order.getId() + ", "+ order.getUniqueOrderId());
        Log.d(TAG, "ORDER_STATUS: " + order.getOrderStatusId());

        String orderJson = new Gson().toJson(order);

        Intent intent = new Intent(getActivity(), ProcessOrderActivityDialog.class);
        intent.putExtra(ProcessOrderActivityDialog.ORDER_REQUEST, orderJson);

        // Order Accepted(3); Restaurant Mark the order Ready(7); ReadyToPickUp(10)
        if(orderStatus == OrderStatus.DELIVERY_GUY_ASSIGNED.value() || orderStatus == OrderStatus.ORDER_READY.value() || orderStatus == OrderStatus.ORDER_READY_TO_PICKUP.value()){
            // Driver want to go to PickupLocation OR Driver want to go to Deliver location
            // start ReachDirectionFragment
            intent.setAction(Actions.REACH_DIRECTION_FRAGMENT.name());
            startActivity(intent);

        }else if(orderStatus == OrderStatus.ON_THE_WAY.value()){
            // start DeliverOrderFragment
            intent.setAction(Actions.DELIVER_ORDER_FRAGMENT.name());
            startActivity(intent);
        }


    }
}