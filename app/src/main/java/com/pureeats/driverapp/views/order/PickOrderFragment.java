package com.pureeats.driverapp.views.order;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pureeats.driverapp.R;
import com.pureeats.driverapp.commons.CommonUtils;
import com.pureeats.driverapp.databinding.FragmentPickOrderBinding;
import com.pureeats.driverapp.models.Order;
import com.pureeats.driverapp.utils.FormatTime;
import com.pureeats.driverapp.viewmodels.OrderViewModel;

import java.util.Date;
import java.util.Locale;

public class PickOrderFragment extends Fragment{
    private final String TAG = this.getClass().getSimpleName();

    private FragmentPickOrderBinding mBinding;
    OrderViewModel orderViewModel;
    NavController navController;
    Order mOrder = null;

    private static long ORDER_ACCEPT_TIME = 3 * 60 * 1000;
    private long mTimeLeftInMills = ORDER_ACCEPT_TIME;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentPickOrderBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        // Initialize ViewModel
        orderViewModel = new ViewModelProvider(requireActivity()).get(OrderViewModel.class);
        orderViewModel.init();

        // Initialize NavController
        navController = Navigation.findNavController(rootView);
        mBinding.pickOrder.btnClickPhoto.setVisibility(View.GONE);
        initClicks();
        mBinding.pickOrder.layoutCustomerDetails.setVisibility(View.GONE);

        orderViewModel.getOnGoingOrder().observe(requireActivity(), order -> {
            mOrder = order;
            mBinding.pickOrder.toolbar.title.setText("PICK ORDER " + mOrder.getUniqueOrderId());
            mBinding.pickOrder.setOrder(mOrder);

            orderViewModel.getSingleDeliveryOrder(mOrder.getUniqueOrderId()).observe(requireActivity(), orderObj -> {
                Log.d(TAG, "===========================SINGLE_ORDER==========================");
                Log.d(TAG, "ORDER: "+ orderObj);
                Log.d(TAG, "CREATED_TIME: "+ order.getCreatedAt());
                Log.d(TAG, "PREPARE_TIME: "+ order.getPrepareTime());

                if(order.getOrderStatusId() == 10){
                    mBinding.pickOrder.txtDeliveryCountdown.setText("READY");
                }else{
                    long currentTime = new Date().getTime();
                    long orderTime =  FormatTime.getTimeFromDateString(orderObj.getCreatedAt());
                    long targetTime =  orderTime + orderObj.getPrepareTime();
                    long remainingTime = targetTime - currentTime;


                    Log.d(TAG, "CURRENT_TIME: "+ currentTime);
                    Log.d(TAG, "ORDER_TIME_LONG: "+ orderTime);
                    Log.d(TAG, "TARGET_TIME_LONG: "+ targetTime);
                    Log.d(TAG, "REMAINING_TIME: "+ remainingTime);

                    mTimeLeftInMills = remainingTime;
                    startTimer();
                }

            });
        });


        mBinding.pickOrder.btnAccept.setOnSlideCompleteListener(slideToActView -> {
            orderViewModel.pickedUpOrder(mOrder).observe(requireActivity(), apiResponse -> {
                if(apiResponse.isSuccess()){
                    mOrder.setOrderStatusId(4);
                    orderViewModel.setOnGoingOrder(mOrder);
                    navController.navigate(R.id.action_pickOrderFragment_to_reachDirectionFragment);
                }else{
                    mBinding.pickOrder.btnAccept.resetSlider();
                    Toast.makeText(requireActivity(), apiResponse.getMesssage(), Toast.LENGTH_LONG).show();
                }
            });
        });
    }


    private void startTimer(){
        new CountDownTimer(mTimeLeftInMills, 1000){
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMills = millisUntilFinished;
                updateCancelTimer();
            }

            @Override
            public void onFinish() {
                //mBinding.txtCounter.setVisibility(View.GONE);
                //mBinding.txtResend.setEnabled(true);
                mBinding.pickOrder.txtDeliveryCountdown.setText("READY");
            }
        }.start();
    }
    private void updateCancelTimer(){
        int minutes = (int) (mTimeLeftInMills / 1000) /60;// divided by 60 seconds
        int seconds = (int) (mTimeLeftInMills / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%01d:%02d", minutes, seconds);
        mBinding.pickOrder.txtDeliveryCountdown.setText(timeLeftFormatted);

    }

    private void initClicks() {
        mBinding.pickOrder.toggleRestaurantDetails.setOnClickListener(view -> {

        });
        mBinding.pickOrder.toggleItems.setOnClickListener(view -> {
            if(mBinding.pickOrder.dishRecycler.getVisibility() ==  View.GONE){
                mBinding.pickOrder.dishRecycler.setVisibility(View.VISIBLE);
                mBinding.pickOrder.imgToggleItem.setRotation(-90);
            }else{
                mBinding.pickOrder.dishRecycler.setVisibility(View.GONE);
                mBinding.pickOrder.imgToggleItem.setRotation(270);
            }

        });
        mBinding.pickOrder.callToRestaurant.setOnClickListener(view -> {
            CommonUtils.makePhoneCall(requireActivity(), mOrder.getRestaurant().getContactNumber());
        });

        mBinding.pickOrder.callToCustomer.setOnClickListener(view -> {
            CommonUtils.makePhoneCall(requireActivity(), mOrder.getUser().getPhone());
        });

        mBinding.pickOrder.btnClickPhoto.setOnClickListener(view -> {
            VerifyBillDialog dialog = new VerifyBillDialog();
            dialog.show(requireActivity().getSupportFragmentManager(), "View Order Id From Bill");

        });
        mBinding.pickOrder.radioConfirm.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b){
                mBinding.pickOrder.btnAccept.setEnabled(true);
                mBinding.pickOrder.btnAccept.setOuterColor(R.color.orange);
            }else{
                mBinding.pickOrder.btnAccept.setEnabled(false);
                mBinding.pickOrder.btnAccept.setOuterColor(R.color.gray);
            }
        });
    }



}