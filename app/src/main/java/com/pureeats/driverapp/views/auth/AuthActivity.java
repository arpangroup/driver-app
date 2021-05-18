package com.pureeats.driverapp.views.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.pureeats.driverapp.databinding.ActivityAuthBinding;
import com.pureeats.driverapp.views.App;

public class AuthActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    private ActivityAuthBinding mBinding;
    App app = App.getInstance();

    public static Intent getIntent(Context context){
        return new Intent(context, AuthActivity.class);
    }
    public static void start(Context context){
        context.startActivity(getIntent(context));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        // In Activity's onCreate() for instance
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

    }
}