package com.pureeats.driverapp.views.order;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.TaskStackBuilder;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.Navigation;

import com.google.gson.Gson;
import com.pureeats.driverapp.R;
import com.pureeats.driverapp.commons.Actions;
import com.pureeats.driverapp.commons.Constants;
import com.pureeats.driverapp.commons.OrderStatus;
import com.pureeats.driverapp.databinding.ActivityDialogBinding;
import com.pureeats.driverapp.models.Order;
import com.pureeats.driverapp.sharedprefs.UserSession;
import com.pureeats.driverapp.utils.CommonUiUtils;

public class DialogActivity extends AbstractProcessOrderActivity {
    private final String TAG = getClass().getSimpleName();
    private ActivityDialogBinding mBinding;
    private UserSession userSession;
    public static String INTENT_EXTRA_ORDER = "extra_order";




    public static PendingIntent getPendingIntent(Context context, int orderId, String uniqueOrderId){
        Intent intent = getIntent(context, orderId, uniqueOrderId);
        return PendingIntent.getActivity(context, orderId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityDialogBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        navController = Navigation.findNavController(this, R.id.navHostFragmentProcessOrder); // important
        userSession = new UserSession(getApplicationContext());
        resolveDestination(getIntent());

        /*
        mUniqueOrderId = getIntent().getStringExtra(Constants.STR_UNIQUE_ORDER_ID);
        mOrderId = getIntent().getIntExtra(Constants.STR_ORDER_ID, -1);
        Log.d(TAG, "UNIQUE_ORDER_ID: " + mUniqueOrderId);
        Log.d(TAG, "ORDER_ID: " + mOrderId);
        AcceptOrderDialog.newInstance(mOrderId, mUniqueOrderId).show(getSupportFragmentManager(), AcceptOrderDialog.class.getName());
        */
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