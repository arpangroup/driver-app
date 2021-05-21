package com.pureeats.driverapp.views.order;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewbinding.ViewBinding;

import com.google.android.gms.maps.model.MarkerOptions;
import com.pureeats.driverapp.commons.Actions;
import com.pureeats.driverapp.commons.Constants;
import com.pureeats.driverapp.commons.OrderStatus;
import com.pureeats.driverapp.models.Order;
import com.pureeats.driverapp.repositories.BaseRepository;
import com.pureeats.driverapp.utils.CommonUiUtils;
import com.pureeats.driverapp.viewmodels.BaseViewModel;
import com.pureeats.driverapp.viewmodels.OrderViewModel;
import com.pureeats.driverapp.views.base.BaseDialogFragment;

public abstract class AbstractOrderDialog<VM extends BaseViewModel, B extends ViewBinding, R extends BaseRepository > extends BaseDialogFragment<VM , B , R> {
    private final String TAG = "AbstractOrderDialog";
    protected int mOrderId;
    protected String mUniqueOrderId;
    private AlertDialog alertDialog;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive........");
            Log.d(TAG, "mOrderID: " + mOrderId);
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
                    case DELIVERY_ASSIGNED:
                        showAlert(title, message);
                }
            }
        }
    };

    private void showAlert(String title, String message){
        alertDialog = new android.app.AlertDialog.Builder(mContext)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    dismissOrderDialog();
                })
                .create();
        alertDialog.show();
    }

    @Override
    public void dismissOrderDialog() {
        if (alertDialog != null){
            alertDialog.hide();
        }
        super.dismissOrderDialog();

    }

    public void gotoNextActivity(Order order){
        OrderStatus orderStatus = OrderStatus.getStatus(order.getOrderStatusId());
        mOrderId = order.getId();
        mUniqueOrderId = order.getUniqueOrderId();
        if(orderStatus == null) return;
        switch (orderStatus){
            case ORDER_RECEIVED:
            case ORDER_READY:
                AcceptOrderDialog.newInstance(order.getId(), order.getUniqueOrderId()).show(mContext.getSupportFragmentManager(), AcceptOrderDialog.class.getName());
                //removeCurrentFragment(AcceptOrderDialog.class.getName());
                break;
            case DELIVERY_GUY_ASSIGNED://On the way to pickup you order from restaurant
            case ORDER_READY_AND_DELIVERY_ASSIGNED://On the way to pickup you order from restaurant
                ReachDirectionFragment.newInstance(order, false).show(mContext.getSupportFragmentManager(), ReachDirectionFragment.class.getName());
                //removeCurrentFragment(ReachDirectionFragment.class.getName());
                removeCurrentFragment(AcceptOrderDialog.class.getName());
                break;
            case ORDER_READY_AND_DELIVERY_REACHED_TO_PICKUP:
            case REACHED_PICKUP_LOCATION:
                PickOrderFragment.newInstance(order).show(mContext.getSupportFragmentManager(), PickOrderFragment.class.getName());
                //removeCurrentFragment(PickOrderFragment.class.getName());
                removeCurrentFragment(ReachDirectionFragment.class.getName());
                break;
            case ON_THE_WAY:// Order is pickedUp and is on its way to deliver [PICKED]
                ReachDirectionFragment.newInstance(order, true).show(mContext.getSupportFragmentManager(), ReachDirectionFragment.class.getName());
                removeCurrentFragment(PickOrderFragment.class.getName());
                break;
            case REACHED_DROP_LOCATION:
                DeliverOrderFragment.newInstance(order).show(mContext.getSupportFragmentManager(), DeliverOrderFragment.class.getName());
                removeCurrentFragment(ReachDirectionFragment.class.getName());
                break;
            case DELIVERED:
                TripDetailsFragment.newInstance(order).show(mContext.getSupportFragmentManager(), TripDetailsFragment.class.getName());
                removeCurrentFragment(DeliverOrderFragment.class.getName());
                break;

            case CANCELED:
                break;
        }

    }



    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart......");
        IntentFilter[] intentFilters = {
                new IntentFilter(Actions.DELIVERY_ASSIGNED.name()),
                new IntentFilter(Actions.ORDER_CANCELLED.name()),
                new IntentFilter(Actions.ORDER_TRANSFERRED.name())
        };

        for (IntentFilter intentFilter : intentFilters) {
            LocalBroadcastManager.getInstance(mContext).registerReceiver(mReceiver, intentFilter);
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop......");
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mReceiver);
    }
}
