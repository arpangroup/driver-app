package com.pureeats.driverapp.views.order;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.Gson;
import com.pureeats.driverapp.commons.Actions;
import com.pureeats.driverapp.commons.OrderStatus;
import com.pureeats.driverapp.databinding.ActivityDialogBinding;
import com.pureeats.driverapp.models.Order;
import com.pureeats.driverapp.receivers.OrderArrivedReceiver;
import com.pureeats.driverapp.sharedprefs.UserSession;

public class DialogActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    private ActivityDialogBinding mBinding;
    private UserSession userSession;
    public static String INTENT_EXTRA_ORDER = "extra_order";
    Order mOrder = null;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive........");
            Log.d(TAG, "EXTRA: " + intent.getStringExtra(INTENT_EXTRA_ORDER));
            if(intent.hasExtra(INTENT_EXTRA_ORDER)){
                String orderJson = intent.getStringExtra(INTENT_EXTRA_ORDER);
                Order order = new Gson().fromJson(orderJson, Order.class);
                Log.d(TAG, "ORDER_ID: " + order.getId());
                Log.d(TAG, "mORDER_ID: " + mOrder.getId());
                Log.d(TAG, "ORDER_STATUS: " + order.getOrderStatusId());
                if (order.getId() != mOrder.getId()) return;
                OrderStatus orderStatus = OrderStatus.getStatus(order.getOrderStatusId());
                if (orderStatus == null) return;
                Log.d(TAG, "ORDER_STATUS: " + orderStatus.name());

                switch (orderStatus){
                    case CANCELED:
                        showAlert("Order Cancelled by the user");
                        break;
                }
            }
        }
    };

    private void showAlert(String title){
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, i) -> finish()).show();
    }


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
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(OrderArrivedReceiver.class.getName());
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mReceiver);
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