package com.pureeats.driverapp.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.google.gson.Gson;
import com.pureeats.driverapp.R;
import com.pureeats.driverapp.commons.Actions;
import com.pureeats.driverapp.commons.Constants;
import com.pureeats.driverapp.models.Order;
import com.pureeats.driverapp.services.EndlessService;
import com.pureeats.driverapp.views.App;
import com.pureeats.driverapp.views.order.DialogActivity;


public class OrderArrivedReceiver extends BroadcastReceiver {
    private static final String TAG = "OrderArrivedReceiver";

    public static Intent getBroadcastIntent(Context context, Actions action, Order order){
        Intent broadcastIntent = new Intent(context, OrderArrivedReceiver.class);
        broadcastIntent.setAction(action.name());
        broadcastIntent.putExtra(DialogActivity.INTENT_EXTRA_ORDER, new Gson().toJson(order));
        Log.d(TAG, "SEND_BROADCAST: " + Actions.NEW_ORDER_ARRIVED.name());
        context.sendBroadcast(broadcastIntent);
        return broadcastIntent;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Inside onReceive()............." + intent.getAction());

        switch (intent.getAction()){
            case Constants.ACTION_NEW_ORDER_ARRIVED:
                String orderJson = intent.getStringExtra(DialogActivity.INTENT_EXTRA_ORDER);
                showOrderArriveNotification(context, new Gson().fromJson(orderJson, Order.class));
                EndlessService.playRingtone(context, Actions.NEW_ORDER_ARRIVED);
                break;
            case Constants.ACTION_ORDER_CANCELLED:
                EndlessService.playRingtone(context, Actions.ORDER_CANCELLED);
                break;
            case Constants.DISMISS_ORDER_NOTIFICATION:
            case Constants.ACTION_ORDER_ACCEPTED:
                EndlessService.playRingtone(context, Actions.DISMISS_ORDER_NOTIFICATION);
                break;
            default:
                break;
        }
    }



    private void showOrderArriveNotification(Context context, Order order) {
        System.out.println("showOrderArriveNotification...");
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

            Log.d(TAG, "Opening DialogActivity from Notification......");
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.notify(order.getId(), notification);
        }else{
            Intent intent = new Intent(context, DialogActivity.class);
            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
            taskStackBuilder.addNextIntentWithParentStack(intent);
            intent.putExtra(DialogActivity.INTENT_EXTRA_ORDER, new Gson().toJson(order));
            intent.setAction(Actions.ACCEPT_ORDER_FRAGMENT.name());
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            Log.d(TAG, "Starting DialogActivity......");
            context.startActivity(intent);
        }

    }
}
