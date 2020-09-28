package com.example.driverapp.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.driverapp.R;
import com.example.driverapp.commons.Constants;
import com.example.driverapp.databinding.ActivityProcessOrderBinding;
import com.example.driverapp.directionhelpers.TaskLoadedCallback;
import com.example.driverapp.firebase.MessagingService;
import com.example.driverapp.models.Order;
import com.example.driverapp.sharedprefs.UserSession;
import com.example.driverapp.viewmodels.OrderViewModel;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import java.util.List;

public class ProcessOrderActivity extends AppCompatActivity implements TaskLoadedCallback {
    private final String TAG = this.getClass().getSimpleName();
    ActivityProcessOrderBinding mBinding;
    private Polyline currentPolyline;
    OrderViewModel orderViewModel;

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
        //setContentView(R.layout.activity_process_order);
        System.out.println("INSIDE onCreate(0 of ProcessOrderActivity============================");

        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        orderViewModel.init();


        try{
            String orderJson = getIntent().getStringExtra(MessagingService.INTENT_EXTRA_ORDER_STATUS);
            Order order = new Gson().fromJson(orderJson, Order.class);
            orderViewModel.setOrder(order);
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "INTENT: EXCEPTION", Toast.LENGTH_SHORT).show();
        }
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
        Log.d(TAG, "INSTANCE: POLYLINE");
        orderViewModel.setPolyline((PolylineOptions) values[0]);
    }
}