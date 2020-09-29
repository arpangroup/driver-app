package com.example.driverapp.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Service;
import android.content.Intent;
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
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.messaging.FirebaseMessaging;

import static com.example.driverapp.sharedprefs.ServiceTracker.getServiceState;

public class MainActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        View rootView = mBinding.getRoot();
        setContentView(rootView);
        //setContentView(R.layout.activity_main);
//        Intent intent = new Intent(this, MessagingService.class);
//        startService(intent);
        FirebaseMessaging.getInstance().setAutoInitEnabled(false);


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
}