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

import com.google.gson.Gson;
import com.pureeats.driverapp.commons.Actions;
import com.pureeats.driverapp.commons.Constants;
import com.pureeats.driverapp.commons.OrderStatus;
import com.pureeats.driverapp.databinding.ActivityDialogBinding;
import com.pureeats.driverapp.models.Order;
import com.pureeats.driverapp.sharedprefs.UserSession;
import com.pureeats.driverapp.utils.CommonUiUtils;

public class DialogActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    private ActivityDialogBinding mBinding;
    private UserSession userSession;
    public static String INTENT_EXTRA_ORDER = "extra_order";
    int mOrderId;
    String mUniqueOrderId = null;

    public static Intent getIntent(Context context, int orderId, String uniqueOrderId){
        Intent intent = new Intent(context, DialogActivity.class);
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addNextIntentWithParentStack(intent);
        intent.setAction(Actions.ACCEPT_ORDER_FRAGMENT.name());
        intent.putExtra(Constants.STR_ORDER_ID, orderId);
        intent.putExtra(Constants.STR_UNIQUE_ORDER_ID, uniqueOrderId);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }
    public static PendingIntent getPendingIntent(Context context, int orderId, String uniqueOrderId){
        Intent intent = getIntent(context, orderId, uniqueOrderId);
        return PendingIntent.getActivity(context, orderId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive........");
            int orderId = intent.getIntExtra(Constants.STR_ORDER_ID, -1);
            String uniqueOrderId = intent.getStringExtra(Constants.STR_UNIQUE_ORDER_ID);
            String actionName = intent.getAction();
            String title = intent.getStringExtra(Constants.STR_TITLE);
            String message = intent.getStringExtra(Constants.STR_MESSAGE);
            Log.d(TAG, "ORDER_ID: " + orderId + ", ACTION: "+ actionName +", UNIQUE_ORDER_ID: " + uniqueOrderId );
            Actions action = Actions.getAction(actionName);

            if(orderId != -1 && action != null && uniqueOrderId.equals(mUniqueOrderId) ){
                switch (action){
                    case ORDER_TRANSFERRED:
                    case ORDER_CANCELLED:
                        new android.app.AlertDialog.Builder(DialogActivity.this)
                                .setTitle(title)
                                .setMessage(message)
                                .setCancelable(false)
                                .setPositiveButton("OK", (d, i) -> finish())
                                .show();
                        break;
                    case DELIVERY_ASSIGNED:
                        CommonUiUtils.showSnackBar(getWindow().getDecorView(), "Delivery guy assigned");
                        break;
                }
            }
        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityDialogBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        userSession = new UserSession(getApplicationContext());
        mUniqueOrderId = getIntent().getStringExtra(Constants.STR_UNIQUE_ORDER_ID);
        mOrderId = getIntent().getIntExtra(Constants.STR_ORDER_ID, -1);
        Log.d(TAG, "UNIQUE_ORDER_ID: " + mUniqueOrderId);
        Log.d(TAG, "ORDER_ID: " + mOrderId);
        AcceptOrderDialog.newInstance(mOrderId, mUniqueOrderId).show(getSupportFragmentManager(), AcceptOrderDialog.class.getName());
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter2 = new IntentFilter(Actions.ORDER_TRANSFERRED.name());
        IntentFilter intentFilter1 = new IntentFilter(Actions.ORDER_CANCELLED.name());
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mReceiver, intentFilter1);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mReceiver, intentFilter2);
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