package com.pureeats.driverapp.views;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.pureeats.driverapp.R;
import com.pureeats.driverapp.databinding.ActivitySplashScreenBinding;
import com.pureeats.driverapp.sharedprefs.UserSession;
import com.pureeats.driverapp.views.auth.AuthActivity;

public class SplashScreenActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    private ActivitySplashScreenBinding mBinding;;
    private static int SPLASH_TIME_OUT = 1000;

    Animation transitionAnimation;
    Animation topAnimation;
    Animation bottomAnimation;
    private UserSession userSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        // Make the screen as  full-screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        setContentView(mBinding.getRoot());
        userSession = new UserSession(this);
        //Initialize Animations:
        transitionAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_screen_transition);
        topAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_down);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        new Handler().postDelayed(this::goToNextActivity, SPLASH_TIME_OUT);
    }


    private void goToNextActivity() {
        if(userSession.isLoggedIn()){
            MainActivity.start(this);
            finishAffinity();
            finish();
        }else{
            AuthActivity.start(this);
            finishAffinity();
            finish();
        }
    }
}
