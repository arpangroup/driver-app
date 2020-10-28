package com.pureeats.driverapp.views.order;

import android.content.Context;
import android.media.MediaPlayer;
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
import com.pureeats.driverapp.commons.Constants;
import com.pureeats.driverapp.commons.ErrorCode;
import com.pureeats.driverapp.commons.NotificationSoundType;
import com.pureeats.driverapp.databinding.FragmentAcceptOrderBinding;
import com.pureeats.driverapp.directionhelpers.ConstructDirectionUrl;
import com.pureeats.driverapp.directionhelpers.FetchURL;
import com.pureeats.driverapp.models.Order;
import com.pureeats.driverapp.viewmodels.LocationViewModel;
import com.pureeats.driverapp.viewmodels.OrderViewModel;
import com.google.android.gms.maps.model.LatLng;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class AcceptOrderFragment extends Fragment{
    private final String TAG = this.getClass().getSimpleName();

    private int mProgress = 0;
    private static long ORDER_ACCEPT_TIME = 3 * 60 * 1000;
    private long mTimeLeftInMills = ORDER_ACCEPT_TIME;

    private boolean isMusicEnable = true;
    private MediaPlayer mMediaPlayer;
    Timer timer;

    Order mIncoOrder = null;



    private FragmentAcceptOrderBinding mBinding;
    OrderViewModel orderViewModel;
    LocationViewModel locationViewModel;
    NavController navController;

    public int counter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentAcceptOrderBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);


        // Initialize ViewModel
        orderViewModel = new ViewModelProvider(requireActivity()).get(OrderViewModel.class);
        locationViewModel = new ViewModelProvider(requireActivity()).get(LocationViewModel.class);
        //orderViewModel.init();

        // Initialize NavController
        navController = Navigation.findNavController(rootView);

        mBinding.setDeliveryTime(null);
        startTimer();


        orderViewModel.getIncomingOrder().observe(requireActivity(), order -> {
            if(order != null){
                Log.d(TAG, "ORDER: " + order);
                LatLng locationRestaurant  = CommonUtils.getRestaurantLocation(order.getRestaurant());
                LatLng locationCustomer  = CommonUtils.getUserLocation(order.getLocation());
                LatLng locationDriver  = locationViewModel.getCurrentLocation().getValue();
                if(locationDriver != null && locationRestaurant  != null){
                    String url = ConstructDirectionUrl.getUrl(locationDriver, locationRestaurant, "driving", Constants.GOOGLE_MAP_AUTH_KEY);
                    Log.d(TAG, "REQUEST FOR POLYLINE");
                    Log.d(TAG, "URL: "+ url);
                    new FetchURL(requireActivity(), FetchURL.DISTANCE_PARSER).execute(url, "driving");
                    new FetchURL(requireActivity(), FetchURL.POINT_PARSER).execute(url, "driving");
                }


                mBinding.setOrder(order);
                startMediaPlayer(NotificationSoundType.ORDER_ARRIVE);
            }
        });

        locationViewModel.getDirection().observe(requireActivity(), direction -> {
            mBinding.setDeliveryTime(direction.getDistance().getText());
        });

        mBinding.btnClose.setOnClickListener(view -> {
            orderViewModel.setOnGoingOrder(null);
            requireActivity().finish();
        });

        mBinding.btnAccept.setOnSlideCompleteListener(slideToActView -> {
            Order incomingOrder = orderViewModel.getIncomingOrder().getValue();
            Log.d(TAG, "Inside setOnSlideCompleteListener..........");
            Log.d(TAG, "ACCEPTED_ORDER: "+incomingOrder);
            if(incomingOrder != null){
                orderViewModel.acceptOrder(incomingOrder).observe(requireActivity(), apiResponse -> {
                    if(apiResponse.isSuccess()){
                        incomingOrder.setOrderStatusId(3);
                        orderViewModel.setOnGoingOrder(incomingOrder);
                        navController.navigate(R.id.action_acceptOrderFragment_to_reachDirectionFragment);
                    }else {
                        mBinding.btnAccept.resetSlider();
                        Toast.makeText(requireActivity(), apiResponse.getMesssage(), Toast.LENGTH_SHORT).show();
                        if(apiResponse.getMesssage().equalsIgnoreCase(ErrorCode.MAX_ORDER_REACHED.name())){
                            requireActivity().finish();
                        }

                    }
                });
            }else{
                Log.d(TAG, "Order is null");
            }
        });




    }

    private void startProgressCountDown() {

    }
    private void updateProgressBar(int progressVal){
        mBinding.progressBar.setProgress(progressVal);
        mBinding.txtProgress.setText(progressVal +"%");
    }



    private void startMediaPlayer(NotificationSoundType soundType) {
        mMediaPlayer = new MediaPlayer();
        Context context = requireActivity();
        if(soundType == NotificationSoundType.ORDER_ARRIVE)mMediaPlayer = MediaPlayer.create(context, R.raw.order_arrived_ringtone);
        else if(soundType == NotificationSoundType.ORDER_CANCELED)mMediaPlayer = MediaPlayer.create(context, R.raw.swiggy_order_cancel_ringtone);
        else mMediaPlayer = MediaPlayer.create(context, R.raw.default_notification_sound);

        if(soundType == NotificationSoundType.ORDER_ARRIVE){
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if(isMusicEnable){
                        try{
                            mMediaPlayer.start();
                        }catch (Exception e){
                            //e.printStackTrace();
                        }
                    }
                }
            }, 0, 3000);

        }else{
            try{
                mMediaPlayer.start();
            }catch (Exception e){
                //e.printStackTrace();
            }
        }

    }

    private void stopMediaPlayer(){
        isMusicEnable  = false;
        if(mMediaPlayer != null){
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isMusicEnable = false;
        stopMediaPlayer();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isMusicEnable = false;
        stopMediaPlayer();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
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

                mBinding.progressBar.setProgress(100);
                mBinding.txtProgress.setText("00:00");
            }
        }.start();
    }
    private void updateCancelTimer(){
        int minutes = (int) (mTimeLeftInMills / 1000) /60;// divided by 60 seconds
        int seconds = (int) (mTimeLeftInMills / 1000) % 60;

        // 100% =====progress complete

        counter++;
        try{
            int val = (int) (counter * 100 / (mTimeLeftInMills/1000));
            mBinding.progressBar.setProgress(val);
        }catch (Exception e){
            e.printStackTrace();
        }

        String timeLeftFormatted = String.format(Locale.getDefault(), "%01d:%02d", minutes, seconds);
        mBinding.txtProgress.setText(timeLeftFormatted);

    }
}