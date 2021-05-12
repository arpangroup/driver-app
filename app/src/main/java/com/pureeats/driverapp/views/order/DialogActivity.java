package com.pureeats.driverapp.views.order;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.pureeats.driverapp.databinding.ActivityDialogBinding;
import com.pureeats.driverapp.models.Order;
import com.pureeats.driverapp.sharedprefs.UserSession;

public class DialogActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    private ActivityDialogBinding mBinding;
    private UserSession userSession;
    public static String INTENT_EXTRA_ORDER = "extra_order";
    Order mOrder = null;


    public static void start(Context context, Order order){
        Intent intent = new Intent(context, DialogActivity.class);
        intent.putExtra(INTENT_EXTRA_ORDER, new Gson().toJson(order));
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityDialogBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        userSession = new UserSession(getApplicationContext());
        String orderJson = getIntent().getStringExtra(INTENT_EXTRA_ORDER);
        mOrder = new Gson().fromJson(orderJson, Order.class);


        AcceptOrderDialog.newInstance(orderJson).show(getSupportFragmentManager(), AcceptOrderDialog.class.getName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        // skip......Disable the back button
        return;
    }
}