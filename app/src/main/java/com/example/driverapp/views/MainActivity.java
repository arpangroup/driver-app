package com.example.driverapp.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.driverapp.R;
import com.example.driverapp.commons.Actions;
import com.example.driverapp.databinding.ActivityMainBinding;
import com.example.driverapp.databinding.ActivityProcessOrderBinding;
import com.example.driverapp.firebase.MessagingService;
import com.example.driverapp.services.EndlessService;
import com.example.driverapp.sharedprefs.ServiceTracker;
import com.example.driverapp.sharedprefs.UserSession;
import com.example.driverapp.utils.GpsUtils;
import com.example.driverapp.viewmodels.LocationViewModel;
import com.example.driverapp.viewmodels.OrderViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.messaging.FirebaseMessaging;

import static com.example.driverapp.sharedprefs.ServiceTracker.getServiceState;

public class MainActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    ActivityMainBinding mBinding;

    boolean isGpsEnabled = false;
    private FusedLocationProviderClient mFusedLocationClient;
    LocationViewModel locationViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        View rootView = mBinding.getRoot();
        setContentView(rootView);
        new GpsUtils(this).turnGPSOn(isGPSEnable -> isGpsEnabled = isGPSEnable);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //setContentView(R.layout.activity_main);
//        Intent intent = new Intent(this, MessagingService.class);
//        startService(intent);
        FirebaseMessaging.getInstance().setAutoInitEnabled(false);
        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
        getCurrentLocation();


        ServiceTracker.ServiceState currentServiceState = getServiceState(this);
        if(currentServiceState == ServiceTracker.ServiceState.STARTED){
            mBinding.toolbar.dutyStatusToggleSwitch.setChecked(true);
            mBinding.toolbar.dutyStatusToggleSwitch.setEnabled(true);
        }else{
            mBinding.toolbar.dutyStatusToggleSwitch.setChecked(false);
            mBinding.toolbar.dutyStatusToggleSwitch.setEnabled(true);
        }

        mBinding.toolbar.dutyStatusToggleSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if(isChecked){
                Log.d(TAG, "START THE FOREGROUND SERVICE ON DEMAND");
                ServiceTracker.setServiceState(this, ServiceTracker.ServiceState.STARTED);
                actionOnService(Actions.START);
            }else{
                Log.d(TAG, "STOP THE FOREGROUND SERVICE ON DEMAND");
                ServiceTracker.setServiceState(this, ServiceTracker.ServiceState.STOPPED);
                actionOnService(Actions.STOP);
            }
        });


    }

    private void actionOnService(Actions action) {
        if (getServiceState(this) == ServiceTracker.ServiceState.STOPPED && action == Actions.STOP) return;
        Intent intent = new Intent(this, EndlessService.class);
        intent.setAction(action.name());
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            Log.d(TAG, "Starting the service in >=26 Mode");
            ContextCompat.startForegroundService(this, intent);
            return;
        }else{
            Log.d(TAG, "Starting the service in < 26 Mode");
            startService(intent);
        }
    }
    private void getCurrentLocation() {
        Log.i(TAG, "All permissions available");
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
            Location location = task.getResult();
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                LatLng latLng = new LatLng(latitude, longitude);
                locationViewModel.setCurrentLocation(latLng);
            } else {
                Log.i(TAG, "Location is null");
            }
        });
    }
}