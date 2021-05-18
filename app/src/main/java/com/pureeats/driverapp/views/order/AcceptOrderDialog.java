package com.pureeats.driverapp.views.order;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.gson.Gson;
import com.pureeats.driverapp.R;
import com.pureeats.driverapp.commons.Actions;
import com.pureeats.driverapp.commons.Constants;
import com.pureeats.driverapp.commons.OrderStatus;
import com.pureeats.driverapp.databinding.FragmentAcceptOrderBinding;
import com.pureeats.driverapp.models.Order;
import com.pureeats.driverapp.network.api.Api;
import com.pureeats.driverapp.repositories.OrderRepositoryImpl;
import com.pureeats.driverapp.viewmodels.OrderViewModel;
import com.pureeats.driverapp.views.App;
import com.pureeats.driverapp.views.base.BaseDialogFragment;

import java.util.Locale;
import java.util.Timer;


public class AcceptOrderDialog extends BaseDialogFragment<OrderViewModel, FragmentAcceptOrderBinding, OrderRepositoryImpl> {
    private final String TAG = getClass().getName();
    private Order mOrder;


    private int mProgress = 0;
    private static long ORDER_ACCEPT_TIME = 3 * 60 * 1000;
    private long mTimeLeftInMills = ORDER_ACCEPT_TIME;
    private boolean isMusicEnable = true;
    private MediaPlayer mMediaPlayer;
    private CountDownTimer countDownTimer;

    public static AcceptOrderDialog newInstance(int orderId, String uniqueOrderId){
        AcceptOrderDialog dialog = new AcceptOrderDialog();
        dialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogFragmentTheme);
        Bundle args = new Bundle();
        args.putInt(Constants.STR_ORDER_ID, orderId);
        args.putString(Constants.STR_UNIQUE_ORDER_ID, uniqueOrderId);
        dialog.setArguments(args);
        dialog.setCancelable(false);
        return dialog;
    }


    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);
        app = App.getInstance();
        mBinding.setLifecycleOwner(this);
        String uniqueOrderId = getArguments().getString(Constants.STR_UNIQUE_ORDER_ID);
        int orderId = getArguments().getInt(Constants.STR_ORDER_ID);
        mBinding.btnClose.setOnClickListener(view -> {stopOrderArrivedTone(orderId); mContext.finish();});
        mBinding.btnAccept.setOnSlideCompleteListener(slideToActView -> processOrder(mOrder));
        startTimer();
    }

    private void init(String uniqueOrderId){
        viewModel.getSingleDeliveryOrder(uniqueOrderId).observe(mContext, resource -> {
            if(mBinding == null) return;
            switch (resource.status){
                case LOADING:
                    mBinding.setIsLoading(true);
                    break;
                case ERROR:
                    mBinding.setIsLoading(false);
                    new AlertDialog.Builder(mContext).setTitle(resource.message).setPositiveButton("OK", (d, i) -> mContext.finish()).show();
                    break;
                case SUCCESS:
                    mBinding.setIsLoading(false);
                    mOrder = resource.data;
                    mBinding.setOrder(mOrder);
                    viewModel.setOrder(mOrder);
                    break;
            }
        });

    }

    private void stopOrderArrivedTone(int orderId){
        Log.d(TAG, "stopOrderArrivedTone");
        if(app == null) app = App.getInstance();
        app.stopOrderArrivedRingTone(orderId);
    }

    private void processOrder(Order order){
        stopOrderArrivedTone(order.getId());
        viewModel.acceptOrder(order).observe(mContext, resource -> {
            switch (resource.status){
                case LOADING:
                    break;
                case ERROR:
                    showAlert(resource.message);
                    break;
                case SUCCESS:
                    Order currentOrder = resource.data;
                    if(!currentOrder.isAlreadyAccepted()){
                        gotoNextActivity(currentOrder);
                    }else{
                       showAlert("Order is already accepted");
                    }

                    break;
            }
        });
    }

    private void showAlert(String title){
        new AlertDialog.Builder(mContext)
                .setTitle(title)
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, i) -> dismissOrderDialog()).show();
    }


    private void startTimer(){
        //https://stackoverflow.com/questions/52569581/countdown-timer-with-a-progressbar
        /**
         * You need to calculate the percentage factor based on total number of seconds to be countdown.
         * For example in your case 100/START_TIME_IN_MILLIS/1000 = 5; after all need to multiply this for
         * each tick count.
         *
         *         tick_count_in_second            percentage_value (5 * second)
         *               1                              5
         *               2                              10
         *               3                              15
         *             .....                            ....
         *             .....                            ....
         *             20                               100
         *
         */

        int numberOfSeconds = (int) ORDER_ACCEPT_TIME / 1000; // Ex : 20000/1000 = 20
        float factor = (float) (100.0f / numberOfSeconds); // 100/20 = 5, for each second multiply this, for sec 1 progressPercentage = 1x5 =5, for sec 5 progressPercentage = 5x5 = 25, for sec 20 progressPercentage = 20x5 =100

        //Log.d("PROGRESS_PERCENTAGE: ", "NUMBER_OF_SECONDS: " + numberOfSeconds);
        //Log.d("PROGRESS_PERCENTAGE: ", "FACTOR: " + factor);

       countDownTimer = new CountDownTimer(mTimeLeftInMills, 1000){
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMills = millisUntilFinished;
                updateCountDownText();

                int secondsRemaining = (int) (millisUntilFinished / 1000);
                int progressPercentage = (int) ((numberOfSeconds-secondsRemaining) * factor) ;
                Log.d("PROGRESS_PERCENTAGE: ", progressPercentage +"");
                if(mBinding != null)mBinding.progressBar.setProgress(progressPercentage);
            }

            @Override
            public void onFinish() {
                //mBinding.txtCounter.setVisibility(View.GONE);
                //mBinding.txtResend.setEnabled(true);

                if(mBinding != null)mBinding.progressBar.setProgress(100);
                if(mBinding != null)mBinding.txtProgress.setText("00:00");
                try{
                    requireActivity().finish();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
       countDownTimer.start();
    }
    private void updateCountDownText(){// this function is calling on each 1 second
        int minutes = (int) (mTimeLeftInMills / 1000) /60;// divided by 60 seconds
        int seconds = (int) (mTimeLeftInMills / 1000) % 60;

        // 100% =====progress complete

        /*
        counter++;

        try{
            int val = (int) (counter * 100 / (mTimeLeftInMills/1000));
            mBinding.progressBar.setProgress(val);
        }catch (Exception e){
            e.printStackTrace();
        }
         */

        String timeLeftFormatted = String.format(Locale.getDefault(), "%01d:%02d", minutes, seconds);
        if(mBinding != null)mBinding.txtProgress.setText(timeLeftFormatted);

    }



    private void sendDismissBroadcast(){
        try {
            //mContext.sendBroadcast(OrderArrivedReceiver.getBroadcastIntent(mContext, Actions.DISMISS_ORDER_NOTIFICATION, mOrder));
            app.stopOrderArrivedRingTone(mOrder.getId());
        }catch (Throwable t){}
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimeLeftInMills = 0;
        if(countDownTimer != null) countDownTimer.cancel();
        countDownTimer = null;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        mTimeLeftInMills = 0;
        if(countDownTimer != null) countDownTimer.cancel();
        countDownTimer = null;
    }

    @Override
    public Class<OrderViewModel> getViewModel() {
        return OrderViewModel.class;
    }

    @Override
    public FragmentAcceptOrderBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentAcceptOrderBinding.inflate(inflater, container, false);
    }

    @Override
    public OrderRepositoryImpl getRepository() {
        return new OrderRepositoryImpl(remoteDataSource.buildApi(Api.class), userSession);
    }
}