package com.example.driverapp.views.order;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.driverapp.R;
import com.example.driverapp.commons.Actions;
import com.example.driverapp.commons.CommonUtils;
import com.example.driverapp.commons.Constants;
import com.example.driverapp.commons.NotificationSoundType;
import com.example.driverapp.databinding.ActivityProcessOrderBinding;
import com.example.driverapp.directionhelpers.ConstructDirectionUrl;
import com.example.driverapp.directionhelpers.FetchURL;
import com.example.driverapp.directionhelpers.TaskLoadedCallback;
import com.example.driverapp.firebase.MessagingService;
import com.example.driverapp.models.Direction;
import com.example.driverapp.models.Order;
import com.example.driverapp.services.FetchOrderService;
import com.example.driverapp.sharedprefs.UserSession;
import com.example.driverapp.viewmodels.LocationViewModel;
import com.example.driverapp.viewmodels.OrderViewModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import java.util.Timer;
import java.util.TimerTask;

import static com.example.driverapp.commons.Actions.DELIVER_ORDER_FRAGMENT;
import static com.example.driverapp.commons.Actions.PICK_ORDER_FRAGMENT;
import static com.example.driverapp.commons.Actions.REACH_DIRECTION_FRAGMENT;

public class ProcessOrderActivityDialog extends AppCompatActivity implements TaskLoadedCallback,  VerifyBillDialog.VerifyBillDialogListener {
    private final String TAG = this.getClass().getSimpleName();
    ActivityProcessOrderBinding mBinding;
    NavController navController;
    private Polyline currentPolyline;
    OrderViewModel orderViewModel;
    LocationViewModel locationViewModel;

    public static boolean isActivityOpen = false;




    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "Inside onNewIntent().................");

    }
    private void subscribeToObserver(){
        Log.d(TAG, "Inside subscribeToObserver....");
        /*
        FetchOrderService.mutableNewOrders.observe(this, orders -> {
            if(orders.size() > 0){
                System.out.println("============================================================================");
                Log.d(TAG, "NEW_ORDERS: "+orders.size());
                Log.d(TAG, "ORDERS: "+orders);

                if(orderViewModel.getRunningOrder().getValue() == null){
                    Order latestOrder = orders.get(0);
                    orderViewModel.setOnGoingOrder(latestOrder);
                    Log.d(TAG, "ORDER_FOR_ACCEPT: " + latestOrder);
                }
                System.out.println("============================================================================");
            }
        });
        */
        FetchOrderService.mutableNewOrders.observe(this, orders -> {
            if(orders.size() > 0){
                Order order = orders.get(0);
                FetchOrderService.processedOrders.add(order);
                System.out.println("============================================================================");
                Log.d(TAG, "NEW_ORDER: "+order);
                Log.d(TAG, "ORDERS: "+order);

                if(orderViewModel.getRunningOrder().getValue() == null){
                    orderViewModel.setOnGoingOrder(order);
                    Log.d(TAG, "ORDER_FOR_ACCEPT: " + order);
                }
                System.out.println("============================================================================");
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mBinding = ActivityProcessOrderBinding.inflate(getLayoutInflater());
        View rootView = mBinding.getRoot();
        setContentView(rootView);
        UserSession userSession = new UserSession(this);
        isActivityOpen = true;

        NavHostFragment navHostFragment = (NavHostFragment)getSupportFragmentManager().findFragmentById(R.id.fragment);
        navController = Navigation.findNavController(this, R.id.fragment);
//        NavInflater inflater = navHostFragment.getNavController().getNavInflater();
//        NavGraph graph = inflater.inflate(R.navigation.nav_graph_process_order);

        Log.d(TAG, "Inside onCreate().................");
        if(getIntent() != null){
            if(getIntent().getAction().equalsIgnoreCase(REACH_DIRECTION_FRAGMENT.name())){
                Log.d(TAG, "ACTION: " + REACH_DIRECTION_FRAGMENT.name());
                //graph.setStartDestination(R.id.reachDirectionFragment);
                navController.navigate(R.id.reachDirectionFragment);
            }else if(getIntent().getAction().equalsIgnoreCase(PICK_ORDER_FRAGMENT.name())){
                Log.d(TAG, "ACTION: " + PICK_ORDER_FRAGMENT.name());
                //graph.setStartDestination(R.id.pickOrderFragment);
                navController.navigate(R.id.pickOrderFragment);
            }else if(getIntent().getAction().equalsIgnoreCase(DELIVER_ORDER_FRAGMENT.name())){
                Log.d(TAG, "ACTION: " + DELIVER_ORDER_FRAGMENT.name());
                //graph.setStartDestination(R.id.deliverOrderFragment);
                navController.navigate(R.id.reachDirectionFragment);
            }else{
                Log.d(TAG, "NO_ACTION_PASSED: " + DELIVER_ORDER_FRAGMENT.name());
                //graph.setStartDestination(R.id.acceptOrderFragment);
            }
        }else{
            Log.d(TAG, "ACTION_IS_NULL: ");
            //graph.setStartDestination(R.id.acceptOrderFragment);
        }
        //navController.setGraph(graph);

        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
        orderViewModel.init();

        subscribeToObserver();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == Constants.REQUEST_CALL_ACTIVITY){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // makePhoneCall
            }
        }
    }

    @Override
    public void onTaskDone(Object... values) {
        Log.d(TAG, "Inside onTaskDone()......");
        try{
            if(values[0] instanceof Direction){
                Log.d(TAG, "INSTANCE: Direction");
                Direction direction = (Direction) values[0];
                locationViewModel.setDirection(direction);
                Log.d(TAG, "DURATION: "+direction.getDuration());
            }else{
                Log.d(TAG, "INSTANCE: POLYLINE");
                orderViewModel.setPolyline((PolylineOptions) values[0]);

            }
        }catch (Exception  e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Activity is destroying");
        isActivityOpen = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Activity is resumed");
        isActivityOpen = true;
    }

    @Override
    public void onClickPhotoOfBill() {

    }

    @Override
    public void onClickHaveNoBill() {

    }

}