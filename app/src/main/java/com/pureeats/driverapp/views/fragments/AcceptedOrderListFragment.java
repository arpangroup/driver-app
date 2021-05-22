package com.pureeats.driverapp.views.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.common.util.CollectionUtils;
import com.pureeats.driverapp.R;
import com.pureeats.driverapp.adapters.AcceptedOrderListAdapter;
import com.pureeats.driverapp.databinding.FragmentAcceptedOrderListBinding;
import com.pureeats.driverapp.models.Order;
import com.pureeats.driverapp.models.response.DeliveryOrderResponse;
import com.pureeats.driverapp.network.api.Api;
import com.pureeats.driverapp.repositories.OrderRepositoryImpl;
import com.pureeats.driverapp.viewmodels.OrderViewModel;
import com.pureeats.driverapp.views.base.BaseDialogFragment;
import com.pureeats.driverapp.views.order.AbstractOrderFragment;
import com.pureeats.driverapp.views.order.DialogActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class AcceptedOrderListFragment extends BaseDialogFragment<OrderViewModel, FragmentAcceptedOrderListBinding, OrderRepositoryImpl> {
    private final String TAG = getClass().getName();
    private List<Order> acceptedOrders = new ArrayList<>();
    private AcceptedOrderListAdapter adapter;
    private ProgressDialog progressDialog;

    public static AcceptedOrderListFragment newInstance(){
        AcceptedOrderListFragment dialog = new AcceptedOrderListFragment();
        dialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogFragmentTheme);
        dialog.setCancelable(true);
        return dialog;
    }


    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);
        adapter = new AcceptedOrderListAdapter(acceptedOrders, this::processOrder);
        mBinding.orderRecycler.setAdapter(adapter);
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        mBinding.toolbar.setNavigationOnClickListener(view -> dismiss());
    }

    private void processOrder(Order order){
        //DialogActivity.start(mContext, order);
        // First check the order details, because in the mean time the status might changed in server side
        viewModel.getSingleDeliveryOrder(order.getUniqueOrderId()).observe(mContext, resource -> {
            if(mBinding == null) return;
            switch (resource.status){
                case LOADING:
                    progressDialog.show();
                    break;
                case ERROR:
                    progressDialog.dismiss();
                    break;
                case SUCCESS:
                    progressDialog.dismiss();
                    Order currentOrder = resource.data;
                    if(!currentOrder.isAlreadyAccepted()){
                        //gotoNextActivity(resource.data);
                        DialogActivity.start(mContext, currentOrder);
                    }
                    else new AlertDialog.Builder(mContext).setTitle("Order is already accepted").show();
                    break;
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        observeViewModel();
    }

    private void observeViewModel(){
        viewModel.getDeliveryOrders().observe(mContext, resource -> {
            if (mBinding == null) return;
            switch (resource.status){
                case LOADING:
                    mBinding.setIsLoading(true);
                    break;
                case ERROR:
                    mBinding.setIsLoading(false);
                    break;
                case SUCCESS:
                    mBinding.setIsLoading(false);
                    DeliveryOrderResponse deliveryOrderResponse =  resource.data;
                    if(deliveryOrderResponse != null){
                        acceptedOrders = deliveryOrderResponse.getAcceptedOrders();
                        mBinding.setOrders(acceptedOrders);
                        if (!CollectionUtils.isEmpty(deliveryOrderResponse.getPickedupOrders())) {
                            acceptedOrders.addAll(deliveryOrderResponse.getPickedupOrders());
                        }
                        Collections.sort(acceptedOrders);
                        adapter.updateAll(acceptedOrders);
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
    public FragmentAcceptedOrderListBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentAcceptedOrderListBinding.inflate(inflater, container, false);
    }

    @Override
    public OrderRepositoryImpl getRepository() {
        return new OrderRepositoryImpl(remoteDataSource.buildApi(Api.class), userSession);
    }

    @Override
    public void onDestroy() {
        if(progressDialog != null) progressDialog.dismiss();
        progressDialog = null;
        super.onDestroy();
    }
}