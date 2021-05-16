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

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

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


public class OrderArrivedReceiver extends BroadcastReceiver {
    private static final String TAG = "OrderArrivedReceiver";
    private Set<Integer> orderSet = new HashSet<>();
    private App app;

    private final String ORDER_CANCELLED_TITLE = "Order Cancelled";
    private final String ORDER_CANCELLED_MESSAGE = "##UNIQUE_ORDER_ID## is cancelled, please ignore this message if already notified";

    public static Intent getBroadcastIntent(final Context context, final Actions action, final Order order){
        Intent broadcastIntent = new Intent(context, OrderArrivedReceiver.class);
        broadcastIntent.setAction(action.name());
        broadcastIntent.putExtra("extra_order", new Gson().toJson(order));
        Log.d(TAG, "SEND_BROADCAST: " + action.name() + " :: OrderID: " + order.getId());
        context.sendBroadcast(broadcastIntent);
        return broadcastIntent;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //Log.d(TAG, "Inside onReceive()............." + intent.getAction());
        if(app == null)app = App.getInstance();
        String orderJson = intent.getStringExtra("extra_order");
        Order order = new Gson().fromJson(orderJson, Order.class);
        if(orderSet.contains(order.getId())) return;
        orderSet.add(order.getId());

        switch (Objects.requireNonNull(Actions.getStatus(intent.getAction()))){
            case NEW_ORDER_ARRIVED:
                app.showOrderArriveNotification(order);
                break;
            case ORDER_CANCELLED:
                // First cancel if any ongoing notification is showing in statusbar
                //EndlessService.stopOrderArrivedRingtone(context, order.getId());
                //CommonUtils.cancelNotification(context, order.getId());
                //app.stopOrderArrivedRingTone(order.getId());

                String message = ORDER_CANCELLED_MESSAGE.replace("##UNIQUE_ORDER_ID##", order.getUniqueOrderId());
                CommonUtils.displayNotification(context, ORDER_CANCELLED_TITLE, message, NotificationSoundType.ORDER_CANCELED);
                break;
            case DISMISS_ORDER_NOTIFICATION:
            case ORDER_ACCEPTED:
                app.stopOrderArrivedRingTone(order.getId());
                break;
        }
    }




}
