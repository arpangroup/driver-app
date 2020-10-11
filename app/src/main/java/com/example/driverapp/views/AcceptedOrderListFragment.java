package com.example.driverapp.views;

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
import com.google.android.gms.maps.model.LatLng;

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
        int orderStatus = order.getOrderStatusId();
        Log.d(TAG, "ORDER: " + order.getId() + ", "+ order.getUniqueOrderId());
        Log.d(TAG, "ORDER_STATUS: " + order.getOrderStatusId());

        // Order Accepted
        if(orderStatus == OrderStatus.DELIVERY_GUY_ASSIGNED.ordinal() || orderStatus == OrderStatus.ON_THE_WAY.ordinal()){
            // Driver want to go to PickupLocation OR Driver want to go to Deliver location
            // start ReachDirectionFragment
            Log.d(TAG, "Start ReachDirectionFragment....................");

            orderViewModel.setOnGoingOrder(order);
            Intent intent = new Intent(getActivity(), ProcessOrderActivityDialog.class);
            intent.setAction(Actions.REACH_DIRECTION_FRAGMENT.name());
            startActivity(intent);

        }else if(orderStatus == OrderStatus.ORDER_READY.ordinal() || orderStatus == OrderStatus.REACHED_PICKUP_LOCATION.ordinal()){
            // start PickOrderFragment
            Log.d(TAG, "Start PickOrderFragment....................");

            orderViewModel.setOnGoingOrder(order);
            Intent intent = new Intent(getActivity(), ProcessOrderActivityDialog.class);
            intent.setAction(Actions.PICK_ORDER_FRAGMENT.name());
            startActivity(intent);
        }else if(orderStatus == OrderStatus.REACHED_DELIVERY_LOCATION.ordinal()){
            // start DeliverOrderFragment
            Log.d(TAG, "Start DeliverOrderFragment....................");

            orderViewModel.setOnGoingOrder(order);
            Intent intent = new Intent(getActivity(), ProcessOrderActivityDialog.class);
            intent.setAction(Actions.DELIVER_ORDER_FRAGMENT.name());
            startActivity(intent);
        }


    }
}