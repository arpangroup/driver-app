package com.pureeats.driverapp.views;

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
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pureeats.driverapp.R;
import com.pureeats.driverapp.commons.Actions;
import com.pureeats.driverapp.commons.Constants;
import com.pureeats.driverapp.commons.UpdateHelper;
import com.pureeats.driverapp.databinding.ActivityMainBinding;
import com.pureeats.driverapp.models.User;
import com.pureeats.driverapp.services.FetchOrderService;
import com.pureeats.driverapp.sharedprefs.ServiceTracker;
import com.pureeats.driverapp.sharedprefs.UserSession;
import com.pureeats.driverapp.utils.GpsUtils;
import com.pureeats.driverapp.viewmodels.LocationViewModel;
import com.pureeats.driverapp.views.auth.AuthActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import static com.pureeats.driverapp.sharedprefs.ServiceTracker.getServiceState;

public class MainActivity extends AppCompatActivity implements UpdateHelper.OnUpdateCheckListener{
    private final String TAG = this.getClass().getSimpleName();
    ActivityMainBinding mBinding;
    NavController mNavController;

    boolean isGpsEnabled = false;
    LocationViewModel locationViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        View rootView = mBinding.getRoot();
        setContentView(rootView);
        UserSession userSession = new UserSession(this);
        mBinding.toolbar.menuRightLayout.setVisibility(View.GONE);

        mNavController = Navigation.findNavController(this, R.id.navHostFragment);
        NavigationUI.setupWithNavController(mBinding.navView, mNavController);
        mNavController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            mBinding.toolbar.title.setText(destination.getLabel());
        });

        if (!UserSession.isLoggedIn()) {
            Intent intent = new Intent(this, AuthActivity.class);
            finishAffinity();
            startActivity(intent);
            finish();
        }


        new GpsUtils(this).turnGPSOn(isGPSEnable -> isGpsEnabled = isGPSEnable);
        FirebaseMessaging.getInstance().setAutoInitEnabled(false);

        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
        //getCurrentLocation();

        initClicks();

        // Check if service is running or not
        System.out.println("#################### FETCH_ORDER_SERVICE ######################");
        if (isFetchOrderServiceRunning(FetchOrderService.class)) {
            System.out.println("Service is running in background");
            if (ServiceTracker.getServiceState(this) == ServiceTracker.ServiceState.STARTED) {
                System.out.println("As service is already running so no action need to handle");
            }
        } else {
            System.out.println("Service is not running in background");
            if (ServiceTracker.getServiceState(this) == ServiceTracker.ServiceState.STARTED) {
                Intent intent = new Intent(this, FetchOrderService.class);
                intent.setAction(Actions.START.name());
                System.out.println("Trying to run the service");
                ContextCompat.startForegroundService(this, intent);
            }
        }
        System.out.println("#############################################################");

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

        if (currentServiceState == ServiceTracker.ServiceState.STARTED) {
            switchCompat.setChecked(true);
        } else {
            switchCompat.setChecked(false);
        }

        switchCompat.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                Log.d(TAG, "START THE FOREGROUND SERVICE ON DEMAND");
                if (ServiceTracker.getServiceState(this) == ServiceTracker.ServiceState.STARTED) {
                    return;
                } else {
                    ServiceTracker.setServiceState(this, ServiceTracker.ServiceState.STARTED);
                    actionOnService(Actions.START);
                    mBinding.drawerLayout.close();
                }
            } else {
                Log.d(TAG, "STOP THE FOREGROUND SERVICE ON DEMAND");
                if (ServiceTracker.getServiceState(this) == ServiceTracker.ServiceState.STOPPED) {
                    return;
                } else {
                    ServiceTracker.setServiceState(this, ServiceTracker.ServiceState.STOPPED);
                    actionOnService(Actions.STOP);
                    mBinding.drawerLayout.close();
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d(TAG, "Starting the service in >=26 Mode");
            ContextCompat.startForegroundService(this, intent);
            return;
        } else {
            Log.d(TAG, "Starting the service in < 26 Mode");
            startService(intent);
        }
    }


    public boolean isFetchOrderServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //getLastLocation();
            //checkSettingsAndStartLocationUpdate();

        } else {
            askLocationPermissions();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }



    private void askLocationPermissions() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                Log.d(TAG, "You should show a alert dialog");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.LOCATION_PERMISSION_REQUEST_CODE);
            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        UpdateHelper.with(this)
                .onUpdateCheck(this)
                .check();
    }

    @Override
    public void onUpdateCheckListener(String urlApp) {
        Log.d(TAG, "Inside onUpdateCheckListener............................") ;
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("New Version Available")
                .setMessage("Please update to new new version to continue")
                .setPositiveButton("UPDATE", (dialogInterface, i) -> {
                    try {
                        String url = "market://details?id=\" + \"com.pureeats.restaurant";
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    }catch (ActivityNotFoundException e){
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlApp)));
                    }
                }).create();
//                .setNegativeButton("CANCEL", (dialogInterface, i) -> {
//                    dialogInterface.dismiss();
//                }).create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }
}