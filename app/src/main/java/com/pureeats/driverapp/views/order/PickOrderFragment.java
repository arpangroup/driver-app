package com.pureeats.driverapp.views.order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.gson.Gson;
import com.pureeats.driverapp.R;
import com.pureeats.driverapp.commons.OrderStatus;
import com.pureeats.driverapp.databinding.ActivityMainBinding;
import com.pureeats.driverapp.databinding.FragmentPickOrderBinding;
import com.pureeats.driverapp.models.Order;
import com.pureeats.driverapp.network.api.Api;
import com.pureeats.driverapp.repositories.AuthRepositoryImpl;
import com.pureeats.driverapp.repositories.BaseRepository;
import com.pureeats.driverapp.repositories.OrderRepositoryImpl;
import com.pureeats.driverapp.viewmodels.BaseViewModel;
import com.pureeats.driverapp.viewmodels.OrderViewModel;
import com.pureeats.driverapp.views.base.BaseDialogFragment;


public class PickOrderFragment extends BaseDialogFragment<OrderViewModel, FragmentPickOrderBinding, OrderRepositoryImpl> {
    private final String TAG = getClass().getName();
    private Order mOrder;

    public static PickOrderFragment newInstance(Order order){
        PickOrderFragment dialog = new PickOrderFragment();
        dialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogFragmentTheme);
        Bundle args = new Bundle();
        args.putString("order_json", new Gson().toJson(order));
        dialog.setArguments(args);
        dialog.setCancelable(false);
        return dialog;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding.setLifecycleOwner(this);
        mOrder = new Gson().fromJson(getArguments().getString("order_json"), Order.class);
        mBinding.setOrder(mOrder);
        mBinding.btnAccept.setOnSlideCompleteListener(slideToActView -> processOrder(mOrder));
    }

    private void processOrder(Order order){
        viewModel.acceptOrder(order).observe(mContext, resource -> {
            switch (resource.status){
                case LOADING:
                    break;
                case ERROR:
                    break;
                case SUCCESS:
                    gotoNextActivity(resource.data);
                    break;
            }
        });
    }



    @Override
    public Class<OrderViewModel> getViewModel() {
        return OrderViewModel.class;
    }

    @Override
    public FragmentPickOrderBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentPickOrderBinding.inflate(inflater, container, false);
    }

    @Override
    public OrderRepositoryImpl getRepository() {
        return new OrderRepositoryImpl(remoteDataSource.buildApi(Api.class), userSession);
    }
}