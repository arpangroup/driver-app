package com.example.driverapp.views.order;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.driverapp.R;
import com.example.driverapp.commons.Constants;
import com.example.driverapp.databinding.FragmentAcceptOrderBinding;
import com.example.driverapp.databinding.FragmentLoginBinding;
import com.example.driverapp.directionhelpers.ConstructDirectionUrl;
import com.example.driverapp.directionhelpers.FetchURL;
import com.example.driverapp.directionhelpers.TaskLoadedCallback;
import com.example.driverapp.firebase.MessagingService;
import com.example.driverapp.models.Direction;
import com.example.driverapp.models.Order;
import com.example.driverapp.models.Restaurant;
import com.example.driverapp.viewmodels.AuthenticationViewModel;
import com.example.driverapp.viewmodels.LocationViewModel;
import com.example.driverapp.viewmodels.OrderViewModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.ncorti.slidetoact.SlideToActView;

import java.util.Timer;
import java.util.TimerTask;

public class AcceptOrderFragment extends Fragment implements TaskLoadedCallback {
    private final String TAG = this.getClass().getSimpleName();

    private int mProgress = 0;
    private static long ORDER_ACCEPT_TIME = 2*1000;
    Timer mTimer;
    private MediaPlayer mMediaPlayer;

    private FragmentAcceptOrderBinding mBinding;
    OrderViewModel orderViewModel;
    LocationViewModel locationViewModel;
    NavController navController;

    public int counter;


    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try{
                String ordersJson = intent.getStringExtra(MessagingService.INTENT_EXTRA_ORDER_STATUS);
                System.out.println("==================RECEIVED==========================");
                System.out.println(ordersJson);
                System.out.println("====================================================");
                Order order = new Gson().fromJson(ordersJson, Order.class);
                //Toast.makeText(context, "ID: "+orderObj.getId(), Toast.LENGTH_SHORT).show();
                Toast.makeText(context, " New order received", Toast.LENGTH_SHORT).show();
                Toast.makeText(context, "RECEIVER TRIGGERED", Toast.LENGTH_SHORT).show();

                //boolean isStatusChanged = orderViewModel.setStatusChange(order);
                //if(isStatusChanged) orderListAdapter.notifyDataSetChanged();
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(context, "RECEIVER: EXCEPTION", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(mReceiver, new IntentFilter(MessagingService.MESSAGE_ORDER_STATUS));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(mReceiver);
    }

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
        orderViewModel.init();

        // Initialize NavController
        navController = Navigation.findNavController(rootView);
        mBinding.setOrder(orderViewModel.getOrder());
        //mBinding.approxDistance.setText(restaurant.getA);
        //LatLng place1  = new LatLng(Double.parseDouble(restaurant.getLatitude()), Double.parseDouble(restaurant.getLongitude()));
        //LatLng place2  = new LatLng(Double.parseDouble(address.getLatitude()), Double.parseDouble(address.getLongitude()));
        //String url = ConstructDirectionUrl.getUrl(place1, place2, "driving", Constants.GOOGLE_MAP_AUTH_KEY);
        //new FetchURL(requireActivity(), FetchURL.DISTANCE_PARSER).execute(url, "driving");


        setupMediaPlayer();
        new CountDownTimer(50000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mBinding.txtProgress.setText(String.valueOf(counter) + "%");
                mBinding.progressBar.setProgress(counter);
                counter++;
            }
            @Override
            public void onFinish() {
                //counttime.setText("Finished");
                //requireActivity().finish();
            }
        }.start();

        locationViewModel.getDirection().observe(requireActivity(), direction -> {
            mBinding.approxDistance.setText("Approx dist " + direction.getDistance().getText());
        });

        mBinding.btnClose.setOnClickListener(view -> requireActivity().finish());

        mBinding.btnAccept.setOnSlideCompleteListener(slideToActView -> {
            orderViewModel.acceptOrder(orderViewModel.getOrder()).observe(requireActivity(), isAccepted -> {
                if(isAccepted){
                    navController.navigate(R.id.action_acceptOrderFragment_to_reachDirectionFragment);
                }else {
                    Toast.makeText(requireActivity(), "Something error happened", Toast.LENGTH_SHORT).show();
                }
            });
        });




    }

    private void startProgressCountDown() {

    }
    private void updateProgressBar(int progressVal){
        mBinding.progressBar.setProgress(progressVal);
        mBinding.txtProgress.setText(progressVal +"%");
    }

    private void setupMediaPlayer() {
        mMediaPlayer = new MediaPlayer();
        Context context = requireActivity();
        mMediaPlayer = MediaPlayer.create(context, R.raw.alert_5);
        mMediaPlayer.start();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mMediaPlayer != null){
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public void onTaskDone(Object... values) {
//        try{
//            Direction direction =(Direction) values[0];
//            deliveryTime.setText(direction.getDeliveryDuration());
//            if(SettingSession.getDeliveryType() == 1){
//                deliveryTime.setText(direction.getDeliveryDuration());
//            }else{
//                deliveryTime.setText(direction.getDistance().getText());
//                //deliveryTimeLabel.setText("Distance");
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
    }
}