package com.pureeats.driverapp.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.Gson;
import com.pureeats.driverapp.R;
import com.pureeats.driverapp.commons.Actions;
import com.pureeats.driverapp.commons.NotificationSoundType;
import com.pureeats.driverapp.models.Order;
import com.pureeats.driverapp.services.EndlessService;
import com.pureeats.driverapp.utils.CommonUtils;
import com.pureeats.driverapp.views.App;
import com.pureeats.driverapp.views.order.DialogActivity;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


public class OrderSyncBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "OrderArrivedReceiver";
    private App app;
    private  Order order;

    private final String ORDER_CANCELLED_TITLE = "Order Cancelled";
    private final String ORDER_CANCELLED_MESSAGE = "##UNIQUE_ORDER_ID## is cancelled, please ignore this message if already notified";

    public static Intent getIntent(Actions action, int orderId, String uniqueOrderId, Context context){
        Intent broadcastIntent = new Intent(context, OrderSyncBroadcastReceiver.class);
        broadcastIntent.setAction(action.name());
        broadcastIntent.putExtra("extra_order_id", orderId);
        broadcastIntent.putExtra("extra_unique_order_id", orderId);
        Log.d(TAG, "SEND_BROADCAST_INTENT: " + action.name() + " :: ORDER_ID: " + orderId);
        return broadcastIntent;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        //Log.d(TAG, "Inside onReceive()............." + intent.getAction());
        if(app == null)app = App.getInstance();

        switch (Objects.requireNonNull(Actions.getStatus(intent.getAction()))){
            case NEW_ORDER_ARRIVED:
                order = new Gson().fromJson(intent.getStringExtra("extra_order"), Order.class);
                app.showOrderArriveNotification(order);
                break;
            case ORDER_CANCELLED:
                order = new Gson().fromJson(intent.getStringExtra("extra_order"), Order.class);
                app.stopOrderArrivedRingTone(order.getId());//remove any ongoing notification is showing in statusbar
                String message = ORDER_CANCELLED_MESSAGE.replace("##UNIQUE_ORDER_ID##", order.getUniqueOrderId());
                CommonUtils.displayNotification(context, ORDER_CANCELLED_TITLE, message, NotificationSoundType.ORDER_CANCELED);

                intent.setAction(Actions.ORDER_CANCELLED.name());
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                break;
            case DISMISS_ORDER_NOTIFICATION:
            case ORDER_ACCEPTED:
                order = new Gson().fromJson(intent.getStringExtra("extra_order"), Order.class);
                app.stopOrderArrivedRingTone(order.getId());
                break;
            case ORDER_TRANSFERRED:
                CommonUtils.displayNotification(context, intent.getStringExtra("title"), intent.getStringExtra("message"), NotificationSoundType.ORDER_CANCELED);
                int orderId = Integer.parseInt(intent.getStringExtra("order_id"));
                app.stopOrderArrivedRingTone(orderId);

                intent.setAction(Actions.ORDER_TRANSFERRED.name());
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                break;
        }
    }




}
