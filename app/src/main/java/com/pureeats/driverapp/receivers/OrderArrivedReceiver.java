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

import java.util.Objects;


public class OrderArrivedReceiver extends BroadcastReceiver {
    private static final String TAG = "OrderArrivedReceiver";
    private App app;

    private final String ORDER_CANCELLED_TITLE = "Order Cancelled";
    private final String ORDER_CANCELLED_MESSAGE = "##UNIQUE_ORDER_ID## is cancelled, please ignore this message if already notified";

    public static Intent getBroadcastIntent(final Context context, final Actions action, final Order order){
        Intent broadcastIntent = new Intent(context, OrderArrivedReceiver.class);
        broadcastIntent.setAction(action.name());
        broadcastIntent.putExtra("extra_order", new Gson().toJson(order));
        Log.d(TAG, "SEND_BROADCAST: " + action.name() + " :: OrderID: " + order.getId());
        context.sendBroadcast(broadcastIntent);
        new Handler().postDelayed(() ->context.sendBroadcast(broadcastIntent), 2000);
        return broadcastIntent;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Inside onReceive()............." + intent.getAction());
        app = App.getInstance();
        String orderJson = intent.getStringExtra("extra_order");
        Order order = new Gson().fromJson(orderJson, Order.class);

        switch (Objects.requireNonNull(Actions.getStatus(intent.getAction()))){
            case NEW_ORDER_ARRIVED:
                showOrderArriveNotification(context, order);
                app.playOrderArrivedTone(order.getId());
                //EndlessService.playOrderArrivedRingtone(context, order.getId(), Actions.NEW_ORDER_ARRIVED);
                break;
            case ORDER_CANCELLED:
                // First cancel if any ongoing notification is showing in statusbar
                //EndlessService.stopOrderArrivedRingtone(context, order.getId());
                //CommonUtils.cancelNotification(context, order.getId());
                app.stopOrderArrivedRingTone(order.getId());

                String message = ORDER_CANCELLED_MESSAGE.replace("##UNIQUE_ORDER_ID##", order.getUniqueOrderId());
                CommonUtils.displayNotification(context, ORDER_CANCELLED_TITLE, message, NotificationSoundType.ORDER_CANCELED);
                break;
            case DISMISS_ORDER_NOTIFICATION:
            case ORDER_ACCEPTED:
                EndlessService.stopOrderArrivedRingtone(context, order.getId());
                break;
            default:
                break;
        }
    }



    private void showOrderArriveNotification(Context context, Order order) {
        //Log.d(TAG, "showOrderArriveNotification...");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, App.CHANNEL_ID_PUSH_NOTIFICATION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Intent fullScreenIntent = new Intent(context, DialogActivity.class);
            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
            taskStackBuilder.addNextIntentWithParentStack(fullScreenIntent);
            fullScreenIntent.setAction(Actions.ACCEPT_ORDER_FRAGMENT.name());
            fullScreenIntent.putExtra(DialogActivity.INTENT_EXTRA_ORDER, new Gson().toJson(order));
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            builder.setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle("New Order Arrive")
                    .setContentText("Order # " + order.getUniqueOrderId())
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_CALL)
                    .setFullScreenIntent(pendingIntent, true)
                    .setAutoCancel(true)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setOngoing(true);
            Notification notification = builder.build();

            //Log.d(TAG, "Opening DialogActivity from Notification......");
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.notify(order.getId(), notification);
        }else{
            Intent intent = new Intent(context, DialogActivity.class);
            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
            taskStackBuilder.addNextIntentWithParentStack(intent);
            intent.putExtra(DialogActivity.INTENT_EXTRA_ORDER, new Gson().toJson(order));
            intent.setAction(Actions.ACCEPT_ORDER_FRAGMENT.name());
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            //Log.d(TAG, "Starting DialogActivity......");
            context.startActivity(intent);
        }

    }

}
