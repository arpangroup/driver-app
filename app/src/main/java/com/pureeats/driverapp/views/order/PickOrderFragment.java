package com.pureeats.driverapp.views.order;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.pureeats.driverapp.R;
import com.pureeats.driverapp.commons.Constants;
import com.pureeats.driverapp.commons.OrderStatus;
import com.pureeats.driverapp.databinding.FragmentPickOrderBinding;
import com.pureeats.driverapp.models.Order;
import com.pureeats.driverapp.network.Resource;
import com.pureeats.driverapp.network.api.Api;
import com.pureeats.driverapp.repositories.OrderRepositoryImpl;
import com.pureeats.driverapp.utils.CommonUtils;
import com.pureeats.driverapp.viewmodels.OrderViewModel;
import com.pureeats.driverapp.views.base.BaseDialogFragment;

import java.util.Date;


public class PickOrderFragment extends BaseDialogFragment<OrderViewModel, FragmentPickOrderBinding, OrderRepositoryImpl> {
    private final String TAG = getClass().getName();
    private static final long ONE_SECOND = 1000;
    private CountDownTimer countDownTimer;
    private Order mOrder;
    private boolean isReadyMarked = false;
    private static final long FETCH_INTERVAL = 30 * ONE_SECOND;
    private boolean isFirstTime = true;

    private final Handler handler = new Handler();
    Runnable runnable;

    private MutableLiveData<Long> remainingPreparingTime = new MutableLiveData<>();

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
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);
        mBinding.setLifecycleOwner(this);
        mOrder = new Gson().fromJson(getArguments().getString("order_json"), Order.class);
        mBinding.setOrder(mOrder);
        mBinding.btnAccept.setOnSlideCompleteListener(slideToActView -> processOrder(mOrder));
        mBinding.toolbar.back.setOnClickListener(view -> dismissOrderDialog());
        handleStatus(mOrder);
    }

    private void handleStatus(Order order){
        if (mBinding == null) return;
        mOrder = order;
        mBinding.setOrder(order);
        OrderStatus orderStatus = OrderStatus.getStatus(order.getOrderStatusId());
        if(orderStatus == OrderStatus.ORDER_READY_AND_DELIVERY_REACHED_TO_PICKUP){
            isReadyMarked = true;
            remainingPreparingTime.setValue(0L);
            mBinding.btnAccept.setEnabled(true);

            if(countDownTimer != null) countDownTimer.cancel();
            if(runnable != null)handler.removeCallbacks(runnable);
        }else {
            startOrderPrepareCountDownTimer(order.getRestaurantAcceptAt(), order.getPrepareTime());
            mBinding.btnAccept.setEnabled(false);
            syncOrderStatus();
        }

        remainingPreparingTime.observe(this, aLong -> {
            if (mBinding == null) return;
            //DateUtils.formatElapsedTime((int) viewModel.arriveTimeLeft)
            if(isReadyMarked){
                mBinding.txtDeliveryCountdown.setText("READY");
                mBinding.txtDeliveryCountdown.setTextColor(mContext.getColor(R.color.green));
            }else{
                mBinding.txtDeliveryCountdown.setText(DateUtils.formatElapsedTime(aLong));
            }
        });
    }

    private void syncOrderStatus(){
        if(!isFirstTime) return;
        handler.postDelayed(runnable = () -> {
            handler.postDelayed(runnable, FETCH_INTERVAL);
            fetchOrderStatus();
            isFirstTime = false;
        }, FETCH_INTERVAL);
    }

    private void fetchOrderStatus(){
        viewModel.getSingleDeliveryOrder(mOrder).observe(this, resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                if(mOrder.getOrderStatusId() != resource.data.getOrderStatusId()){
                    handleStatus(resource.data);
                }
            }
        });
    }

    private void processOrder(Order order){
        viewModel.pickedUpOrder(order).observe(mContext, resource -> {
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




    private void startOrderPrepareCountDownTimer(String orderAcceptedAt, int prepareTimeInMin){
        Log.d(TAG, "startArrivingCountDownTimer....");
        Log.d(TAG, "ORDER_ACCEPTED_TIME: "+ orderAcceptedAt);
        Log.d(TAG, "PREPARE_TIME_IN_MINUTE: "+ prepareTimeInMin);
        if(countDownTimer != null) return; // already running the timer, then no need to initialize


        long acceptedTimeInMills = CommonUtils.getTimeInMilliseconds(orderAcceptedAt);
        long prepareTimeInMills = prepareTimeInMin * 60 * 1000;// 1s = 1000ms
        long targetTime = acceptedTimeInMills + prepareTimeInMills;
        long currentTime = new Date().getTime();
        long timeElapsed = currentTime - acceptedTimeInMills;


        long countDownValue =  targetTime - currentTime;
        Log.d(TAG, "COUNTDOWN_VALUE : "+ countDownValue);

        countDownTimer = new CountDownTimer(countDownValue, ONE_SECOND) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingPreparingTime.setValue(millisUntilFinished / ONE_SECOND);
            }

            @Override
            public void onFinish() {
                remainingPreparingTime.setValue(0L);
            }
        };
        countDownTimer.start();
    }


    @Override
    public void onDestroy() {
        if(countDownTimer != null) countDownTimer.cancel();
        handler.removeCallbacks(runnable);
        countDownTimer = null;
        super.onDestroy();
    }
}