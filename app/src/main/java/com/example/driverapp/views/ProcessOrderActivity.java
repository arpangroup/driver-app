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
import com.example.driverapp.commons.CommonUtils;
import com.example.driverapp.commons.Constants;
import com.example.driverapp.databinding.ActivityProcessOrderBinding;
import com.example.driverapp.directionhelpers.ConstructDirectionUrl;
import com.example.driverapp.directionhelpers.FetchURL;
import com.example.driverapp.directionhelpers.TaskLoadedCallback;
import com.example.driverapp.firebase.MessagingService;
import com.example.driverapp.models.Direction;
import com.example.driverapp.models.Order;
import com.example.driverapp.sharedprefs.UserSession;
import com.example.driverapp.viewmodels.LocationViewModel;
import com.example.driverapp.viewmodels.OrderViewModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import java.net.URL;
import java.util.Calendar;
import java.util.List;

public class ProcessOrderActivity extends AppCompatActivity implements TaskLoadedCallback {
    private final String TAG = this.getClass().getSimpleName();
    ActivityProcessOrderBinding mBinding;
    private Polyline currentPolyline;
    OrderViewModel orderViewModel;
    LocationViewModel locationViewModel;

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

        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
        orderViewModel.init();



        try{
            String orderJson = getIntent().getStringExtra(MessagingService.INTENT_EXTRA_ORDER_STATUS);
            Order order = new Gson().fromJson(orderJson, Order.class);
            orderViewModel.setOrder(order);

            LatLng place1  = CommonUtils.getRestaurantLocation(order.getRestaurant());
            LatLng place2  = CommonUtils.getUserLocation(order.getLocation());
            String url = ConstructDirectionUrl.getUrl(place1, place2, "driving", Constants.GOOGLE_MAP_AUTH_KEY);
            Log.d(TAG, "REQUEST FOR POLYLINE");
            Log.d(TAG, "URL: "+ url);
            new FetchURL(this, FetchURL.DISTANCE_PARSER).execute(url, "driving");
            new FetchURL(this, FetchURL.POINT_PARSER).execute(url, "driving");
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
}