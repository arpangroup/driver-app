package com.example.driverapp.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.driverapp.R;
import com.example.driverapp.commons.Actions;
import com.example.driverapp.commons.Constants;
import com.example.driverapp.databinding.ActivityMainBinding;
import com.example.driverapp.models.Order;
import com.example.driverapp.models.User;
import com.example.driverapp.services.FetchOrderService;
import com.example.driverapp.sharedprefs.ServiceTracker;
import com.example.driverapp.sharedprefs.UserSession;
import com.example.driverapp.utils.GpsUtils;
import com.example.driverapp.viewmodels.LocationViewModel;
import com.example.driverapp.viewmodels.OrderViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import static com.example.driverapp.sharedprefs.ServiceTracker.getServiceState;

public class MainActivity extends AppCompatActivity{
    private final String TAG = this.getClass().getSimpleName();
    ActivityMainBinding mBinding;
    NavController mNavController;

    boolean isGpsEnabled = false;
    private FusedLocationProviderClient mFusedLocationClient;
    LocationViewModel locationViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        View rootView = mBinding.getRoot();
        setContentView(rootView);

        mNavController = Navigation.findNavController(this, R.id.navHostFragment);
        NavigationUI.setupWithNavController(mBinding.navView, mNavController);
        mNavController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            mBinding.toolbar.title.setText(destination.getLabel());
        });






        new GpsUtils(this).turnGPSOn(isGPSEnable -> isGpsEnabled = isGPSEnable);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        FirebaseMessaging.getInstance().setAutoInitEnabled(false);
        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
        getCurrentLocation();

        initClicks();

        mBinding.toolbar.navMenu.setOnClickListener(view -> {
            mBinding.drawerLayout.openDrawer(GravityCompat.START);
        });

//        NavigationView navigationView = (NavigationView)findViewById(R.id.navHostFragment);
//        View headerView = navigationView.getHeaderView(0);
//        headerView.findViewById(R.id.dutyStatusToggleSwitch).setOnClickListener(view -> {
//            Toast.makeText(this, "CLICKED", Toast.LENGTH_SHORT).show();
//        });


    }

    private void initClicks() {
        ServiceTracker.ServiceState currentServiceState = getServiceState(this);
        NavigationView navigationView = (NavigationView) mBinding.navView;
        View headerView = navigationView.getHeaderView(0);

        ImageView profileImage = headerView.findViewById(R.id.imgProfile);
        TextView profileName = headerView.findViewById(R.id.profile_name);
        SwitchCompat switchCompat = headerView.findViewById(R.id.dutyStatusToggleSwitch);

        User user = UserSession.getUserData(this);
        Picasso.get().load(Constants.DELIVERY_IMAGE_URL + user.getPhoto()).into(profileImage);
        profileName.setText(user.getNickName());

        if(currentServiceState == ServiceTracker.ServiceState.STARTED){
            switchCompat.setChecked(true);
        }else{
            switchCompat.setChecked(false);
        }

        switchCompat.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if(isChecked){
                Log.d(TAG, "START THE FOREGROUND SERVICE ON DEMAND");
                if(ServiceTracker.getServiceState(this) == ServiceTracker.ServiceState.STARTED){
                    return;
                }
                else{
                    ServiceTracker.setServiceState(this, ServiceTracker.ServiceState.STARTED);
                    actionOnService(Actions.START);
                }
            }else{
                Log.d(TAG, "STOP THE FOREGROUND SERVICE ON DEMAND");
                if(ServiceTracker.getServiceState(this) == ServiceTracker.ServiceState.STOPPED){
                    return;
                }
                else{
                    ServiceTracker.setServiceState(this, ServiceTracker.ServiceState.STOPPED);
                    actionOnService(Actions.STOP);
                }
            }
        });

        headerView.setOnClickListener(view -> {
            mNavController.navigate(R.id.dashboardFragment);
            mBinding.drawerLayout.close();
        });

    }

    private void actionOnService(Actions action) {
        //if (getServiceState(this) == ServiceTracker.ServiceState.STOPPED && action == Actions.STOP) return;
        Intent intent = new Intent(this, FetchOrderService.class);
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