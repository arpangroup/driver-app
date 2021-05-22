package com.pureeats.driverapp.views.order;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewbinding.ViewBinding;

import com.google.gson.Gson;
import com.pureeats.driverapp.R;
import com.pureeats.driverapp.commons.Actions;
import com.pureeats.driverapp.commons.Constants;
import com.pureeats.driverapp.commons.OrderStatus;
import com.pureeats.driverapp.models.Order;
import com.pureeats.driverapp.repositories.BaseRepository;
import com.pureeats.driverapp.viewmodels.BaseViewModel;
import com.pureeats.driverapp.views.base.BaseDialogFragment;

public abstract class AbstractOrderFragment<VM extends BaseViewModel, B extends ViewBinding, R extends BaseRepository > extends BaseDialogFragment<VM , B , R> {
    private final String TAG = "AbstractOrderDialog";
    NavController navController;
    protected int mOrderId;
    protected String mUniqueOrderId;
    private AlertDialog alertDialog;





    public void gotoNextActivity1(Order order){
        OrderStatus orderStatus = OrderStatus.getStatus(order.getOrderStatusId());
        mOrderId = order.getId();
        mUniqueOrderId = order.getUniqueOrderId();
        if(orderStatus == null) return;
        switch (orderStatus){
            case ORDER_RECEIVED:
            case ORDER_READY:
                AcceptOrderFragment.newInstance(order.getId(), order.getUniqueOrderId()).show(mContext.getSupportFragmentManager(), AcceptOrderFragment.class.getName());
                //removeCurrentFragment(AcceptOrderDialog.class.getName());
                break;
            case DELIVERY_GUY_ASSIGNED://On the way to pickup you order from restaurant
            case ORDER_READY_AND_DELIVERY_ASSIGNED://On the way to pickup you order from restaurant
                ReachDirectionFragment.newInstance(order, false).show(mContext.getSupportFragmentManager(), ReachDirectionFragment.class.getName());
                //removeCurrentFragment(ReachDirectionFragment.class.getName());
                removeCurrentFragment(AcceptOrderFragment.class.getName());
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
    public void gotoNextFragment(@IdRes int resId, Order order){
        Log.d(TAG, "gotoNextFragment...");
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.STR_ORDER_ID, order.getId());
        bundle.putString(Constants.STR_UNIQUE_ORDER_ID,  order.getUniqueOrderId());
        bundle.putString(Constants.STR_ORDER_JSON, new Gson().toJson(order));

        OrderStatus orderStatus = OrderStatus.getStatus(order.getOrderStatusId());
        Log.d(TAG, "ORDER_STATUS: " + orderStatus.name());
        if (orderStatus == OrderStatus.ON_THE_WAY) bundle.putBoolean(Constants.STR_IS_ORDER_PICKED, true);

        navController.navigate(resId, bundle);
    }
}
