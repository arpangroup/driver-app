package com.pureeats.driverapp.views;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.pureeats.driverapp.R;
import com.pureeats.driverapp.commons.Actions;
import com.pureeats.driverapp.commons.Constants;
import com.pureeats.driverapp.databinding.ActivityMainBinding;
import com.pureeats.driverapp.models.User;
import com.pureeats.driverapp.models.request.RequestToken;
import com.pureeats.driverapp.sharedprefs.ServiceTracker;
import com.pureeats.driverapp.sharedprefs.UserSession;
import com.pureeats.driverapp.utils.CommonUtils;
import com.pureeats.driverapp.views.auth.AuthActivity;

import static com.pureeats.driverapp.sharedprefs.ServiceTracker.getServiceState;

public class MainActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    private ActivityMainBinding mBinding;
    NavController navController;
    UserSession userSession;

    public static void start(Context context){
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        userSession = new UserSession(this);
        navController = Navigation.findNavController(this, R.id.navHostFragmentMain);
        NavigationUI.setupWithNavController(mBinding.navView, navController);
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> mBinding.toolbar.title.setText(destination.getLabel()));
        mBinding.toolbar.navMenu.setOnClickListener(view -> mBinding.drawerLayout.openDrawer(GravityCompat.START));
        initClicks();


    }


    private void initClicks() {
        ServiceTracker.ServiceState currentServiceState = getServiceState(this);
        NavigationView navigationView = (NavigationView) mBinding.navView;
        View headerView = navigationView.getHeaderView(0);

        /*====================================================================================*/
        Menu menuNav = navigationView.getMenu();
        MenuItem shareMenu = menuNav.findItem(R.id.nav_share);
        MenuItem logoutMenu = menuNav.findItem(R.id.nav_logout);
        MenuItem rateMenu = menuNav.findItem(R.id.nav_rate);
        shareMenu.setOnMenuItemClickListener(menuItem -> {
            CommonUtils.shareApp(this);
            return false;
        });
        rateMenu.setOnMenuItemClickListener(menuItem -> {
            CommonUtils.rateApp(this);
            return false;
        });
        logoutMenu.setOnMenuItemClickListener(menuItem -> {
            new AlertDialog.Builder(this)
                    .setTitle("Are you sure to logout?")
                    .setMessage("You will loss alll your settings, and tyou need to login again")
                    .setPositiveButton("YES", (dialogInterface, i) -> {
                        logout();
                    })
                    .setNegativeButton("NO", (dialogInterface, i) -> dialogInterface.dismiss())
                    .show();
            return false;
        });
        /*====================================================================================*/

        ImageView profileImage = headerView.findViewById(R.id.imgProfile);
        TextView profileName = headerView.findViewById(R.id.profile_name);
        SwitchCompat switchCompat = headerView.findViewById(R.id.dutyStatusToggleSwitch);

        User user = userSession.getUserData(this);
        if(user != null) {
            System.out.println(Constants.DELIVERY_IMAGE_URL + user.getPhoto());
            Glide.with(profileImage).load(Constants.DELIVERY_IMAGE_URL + user.getPhoto()).into(profileImage);
            profileName.setText(user.getNickName());
        }



        if (currentServiceState == ServiceTracker.ServiceState.STARTED) {
            switchCompat.setChecked(true);
        } else {
            switchCompat.setChecked(false);
        }

        switchCompat.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                Log.d(TAG, "START THE FOREGROUND SERVICE ON DEMAND");
                if (getServiceState(this) == ServiceTracker.ServiceState.STARTED) {
                    return;
                } else {
                    ServiceTracker.setServiceState(this, ServiceTracker.ServiceState.STARTED);
                    actionOnService(Actions.START);
                    mBinding.drawerLayout.close();
                }
            } else {
                Log.d(TAG, "STOP THE FOREGROUND SERVICE ON DEMAND");
                if (getServiceState(this) == ServiceTracker.ServiceState.STOPPED) {
                    return;
                } else {
                    ServiceTracker.setServiceState(this, ServiceTracker.ServiceState.STOPPED);
                    actionOnService(Actions.STOP);
                    mBinding.drawerLayout.close();
                }
                logout();
            }
        });

        headerView.setOnClickListener(view -> {
            navController.navigate(R.id.homeFragment);
            mBinding.drawerLayout.close();
        });





    }

    private void actionOnService(Actions action) {
        /*
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
         */
    }
    private void logout(){
        userSession.clear();
        AuthActivity.start(this);
        finishAffinity();
        finish();
    }
}