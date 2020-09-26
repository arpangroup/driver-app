package com.example.driverapp.views.auth;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.driverapp.databinding.ActivityAuthBinding;
import com.example.driverapp.R;
import com.example.driverapp.viewmodels.AuthenticationViewModel;

public class AuthActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    ActivityAuthBinding mBinding;
    AuthenticationViewModel authenticationViewModel;
    NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        authenticationViewModel = new ViewModelProvider(this).get(AuthenticationViewModel.class);
        authenticationViewModel.init();

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_container);
        //NavigationUI.setupActionBarWithNavController(this, navController);
    }

}