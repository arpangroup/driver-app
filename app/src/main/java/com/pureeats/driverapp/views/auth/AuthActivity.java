package com.pureeats.driverapp.views.auth;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.pureeats.driverapp.databinding.ActivityAuthBinding;
import com.pureeats.driverapp.R;
import com.pureeats.driverapp.viewmodels.AuthenticationViewModelOld;

public class AuthActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    ActivityAuthBinding mBinding;
    AuthenticationViewModelOld authenticationViewModel;
    NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        authenticationViewModel = new ViewModelProvider(this).get(AuthenticationViewModelOld.class);
        authenticationViewModel.init();

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_container);
        //NavigationUI.setupActionBarWithNavController(this, navController);
    }

}