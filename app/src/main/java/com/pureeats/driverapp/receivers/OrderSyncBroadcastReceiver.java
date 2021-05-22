package com.pureeats.driverapp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.pureeats.driverapp.commons.Actions;
import com.pureeats.driverapp.commons.Constants;
import com.pureeats.driverapp.commons.NotificationSoundType;
import com.pureeats.driverapp.models.Notification;
import com.pureeats.driverapp.utils.CommonUtils;
import com.pureeats.driverapp.views.App;


public class OrderSyncBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "OrderSyncBR";
    private App app;

    private final String ORDER_CANCELLED_TITLE = "Order Cancelled";
    private final String ORDER_CANCELLED_MESSAGE = "##UNIQUE_ORDER_ID## is cancelled, please ignore this message if already notified";
    private final String ORDER_TRANSFERRED_TITLE = "Order Transferred";
    private final String ORDER_TRANSFERRED_MESSAGE = "##UNIQUE_ORDER_ID## is transferred to other delivery";

    public static Intent getIntent(Context context, Notification notification){
        Log.d(TAG, "getIntent: " + notification.getNotificationType().name());
        Intent broadcastIntent = new Intent(context, OrderSyncBroadcastReceiver.class);
        Actions action = Actions.getAction(notification.getNotificationType().name());
        if(action == null) return null;
        broadcastIntent.setAction(action.name());
        broadcastIntent.putExtra(Constants.STR_ORDER_ID, notification.getOrderId());
        broadcastIntent.putExtra(Constants.STR_UNIQUE_ORDER_ID, notification.getUniqueOrderId());
        Log.d(TAG, "SEND_BROADCAST_INTENT: " + action.name() + " :: ORDER_ID: " + notification.getOrderId());
        return broadcastIntent;
    }
    public static Intent getIntent(Context context, Actions action, int orderId, String uniqueOrderId){
        Intent broadcastIntent = new Intent(context, OrderSyncBroadcastReceiver.class);
        if(action == null) return null;
        broadcastIntent.setAction(action.name());
        broadcastIntent.putExtra(Constants.STR_ORDER_ID, orderId);
        broadcastIntent.putExtra(Constants.STR_UNIQUE_ORDER_ID, uniqueOrderId);
        Log.d(TAG, "SEND_BROADCAST_INTENT: " + action.name() + " :: ORDER_ID: " + orderId);
        return broadcastIntent;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Inside onReceive()............." + intent.getAction());
        if(app == null)app = App.getInstance();
        int orderId = intent.getIntExtra(Constants.STR_ORDER_ID, -1);
        String uniqueOrderId = intent.getStringExtra(Constants.STR_UNIQUE_ORDER_ID);
        String actionName = intent.getAction();
        Log.d(TAG, "ORDER_ID: " + orderId + ", ACTION: "+ actionName +", UNIQUE_ORDER_ID: " + uniqueOrderId );
        Actions action = Actions.getAction(actionName);
        if(orderId == -1 || action == null || uniqueOrderId == null) return;

        switch (action){
            case ORDER_ARRIVED:// Trigger when Store accepted the order
            case DELIVERY_RE_ASSIGNED:// Triggered when a DeliveryGuy is re-assigned inplace of old DeliveryGuy
                CommonUtils.showOrderArriveNotification(context, orderId, uniqueOrderId);
                app.playOrderArrivedTone(orderId);
                break;
            case DELIVERY_ASSIGNED:// Trigger when DeliveryGuy Accept the order
                // Trigger local broadcast to indicate the order is received, it may be received from the Admin
                // if user is currently in AcceptOrder dialog , and the notification fired
                // show alert message to user that the order is already accepted
                app.stopOrderArrivedRingTone(orderId);//remove any ongoing notification is showing in statusbar
                CommonUtils.cancelNotification(context, orderId);

                sendOrderStatusChangedLocalBroadcast(context, action, intent, "Delivery  assigned", "Delivery Guy already assigned for the order "+ uniqueOrderId);
                break;
            case ORDER_TRANSFERRED:
            case ORDER_CANCELLED:
                //step1: stop the arriving tone, if it is ringing
                app.stopOrderArrivedRingTone(orderId);//remove any ongoing notification is showing in statusbar

                //step2: Show cancelled order message with tone
                String title = ORDER_CANCELLED_TITLE;
                String message = ORDER_CANCELLED_MESSAGE.replace("##UNIQUE_ORDER_ID##", uniqueOrderId);
                if(action == Actions.ORDER_TRANSFERRED) {
                    title = ORDER_TRANSFERRED_TITLE;
                    message = ORDER_TRANSFERRED_MESSAGE.replace("##UNIQUE_ORDER_ID##", uniqueOrderId);
                }
                CommonUtils.displayNotification(context, title, message, NotificationSoundType.ORDER_CANCELED);

                //step3: Send the broadcat for any further action
                sendOrderStatusChangedLocalBroadcast(context, action, intent, title, message);
                break;
        }
    }

    private void sendOrderStatusChangedLocalBroadcast(Context context, Actions action, Intent intent, String title, String message){
        Log.d(TAG, "sendOrderStatusChangedLocalBroadcast");
        Log.d(TAG, "ACTION: "+ action.name());
        intent.setAction(action.name());
        intent.putExtra(Constants.STR_TITLE, title);
        intent.putExtra(Constants.STR_MESSAGE, message);
        Log.d(TAG, "SENDING LOCAL BROADCAST.....: ");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
