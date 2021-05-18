package com.pureeats.driverapp.firebase;

import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.pureeats.driverapp.commons.Actions;
import com.pureeats.driverapp.models.Notification;
import com.pureeats.driverapp.receivers.OrderSyncBroadcastReceiver;
import com.pureeats.driverapp.views.App;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MessagingService extends FirebaseMessagingService {
    private final String TAG = this.getClass().getSimpleName();
    Intent broadcastIntent;
    public static final String SERVICE_MESSAGE = "MessagingService";
    public static final String MESSAGE_ORDER_STATUS = "MESSAGE_ORDER_STATUS";
    public static final String INTENT_EXTRA_ORDER_STATUS = "INTENT_EXTRA_ORDER_STATUS";
    enum PUSH_NOTIFICATION_SOURCE{
        CONSOLE,
        API_WITH_NOTIFICATION,
        API_WITHOUT_NOTIFICATION,
        UNKNOWN_SOURCE

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()...................");
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "onMessageReceived() called....");
        super.onMessageReceived(remoteMessage);
        PUSH_NOTIFICATION_SOURCE notificationSource = getNotificationSource(remoteMessage);
        Map<String, String> data = remoteMessage.getData();
        Log.d(TAG, "#################### MEsSAGE ##########################################");
        data.forEach((k, v) -> System.out.println(k + " : " + v));
        Log.d(TAG, "#######################################################################");
        Notification notification = Notification.mapFrom(data);


        switch (notification.getNotificationType()){
            case ORDER_ARRIVED:
                broadcastIntent = OrderSyncBroadcastReceiver.getIntent(Actions.NEW_ORDER_ARRIVED, notification.getOrderId(), notification.getUniqueOrderId(), getApplicationContext());
                sendBroadcast(broadcastIntent);
                break;
            case DELIVERY_ASSIGNED:
                broadcastIntent = OrderSyncBroadcastReceiver.getIntent(Actions.NEW_ORDER_ARRIVED, notification.getOrderId(), notification.getUniqueOrderId(), getApplicationContext());
                sendBroadcast(broadcastIntent);
                break;
            case DELIVERY_RE_ASSIGNED:
                broadcastIntent = OrderSyncBroadcastReceiver.getIntent(Actions.NEW_ORDER_ARRIVED, notification.getOrderId(), notification.getUniqueOrderId(), getApplicationContext());
                sendBroadcast(broadcastIntent);
                break;
            case ORDER_CANCELLED:
                broadcastIntent = OrderSyncBroadcastReceiver.getIntent(Actions.NEW_ORDER_ARRIVED, notification.getOrderId(), notification.getUniqueOrderId(), getApplicationContext());
                sendBroadcast(broadcastIntent);
                break;
            case ORDER_TRANSFERRED:
                broadcastIntent = OrderSyncBroadcastReceiver.getIntent(Actions.NEW_ORDER_ARRIVED, notification.getOrderId(), notification.getUniqueOrderId(), getApplicationContext());
                sendBroadcast(broadcastIntent);
                break;
            default:
                break;
        }


        if(notification.getNotificationType() != null){


            String type = data.get("type");
            if(Actions.ORDER_TRANSFERRED.name().equals(type)){
                Intent broadcastIntent = new Intent(getApplicationContext(), OrderSyncBroadcastReceiver.class);
                broadcastIntent.setAction(Actions.ORDER_TRANSFERRED.name());
                broadcastIntent.putExtra("title", data.get("title"));
                broadcastIntent.putExtra("unique_order_id", data.get("unique_order_id"));
                broadcastIntent.putExtra("order_id", data.get("order_id"));
                broadcastIntent.putExtra("message", data.get("message"));
                Log.d(TAG, "SEND_BROADCAST: " + Actions.ORDER_TRANSFERRED.name() + " :: UniqueOrderID: " + data.get("unique_order_id"));
                sendBroadcast(broadcastIntent);
                return;
            }
        }


        int orderStatusId = notification.getOrderStatus().status();
        if(orderStatusId == 2 || orderStatusId == 7){//ORDER_ACCEPTED_BY_RESTAURANT AND ORDER_READY_BY_RESTAURANT
            Log.d(TAG, "#######################################################################");
            Log.d(TAG, "TITLE: "+data.get("title"));
            Log.d(TAG, "MESSAGE: "+data.get("message"));
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            Log.d(TAG, "Source: "+notificationSource.toString());
            Log.d(TAG, "UNIQUE_ORDER_ID: " + data.get("unique_order_id"));
            Log.d(TAG, "ORDER_ID: " + data.get("order_id"));
            Log.d(TAG, "ORDER_STATUS_ID: " + data.get("order_status_id"));
            Log.d(TAG, "#######################################################################");

            switch (notificationSource){
                case CONSOLE:
                    break;
                case API_WITH_NOTIFICATION:
                    break;
                case API_WITHOUT_NOTIFICATION:
                    if(remoteMessage.getData().get("order") != null){
                        String orderJson = remoteMessage.getData().get("order");
                        String title = data.get("title");
                        String message = data.get("message");
                        if(orderStatusId == 2){
                            openAcceptOrderDialog(orderJson, title, message);
                            //sendMessageToBroadCastReceiver(orderJson);
                        }
                        if(orderStatusId == 7){
                            sendMessageToBroadCastReceiver(orderJson);
                        }
                    }
                    break;
                case UNKNOWN_SOURCE:
                    break;
            }
        }

    }

    private void openAcceptOrderDialog(String orderJson, String  title, String message){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, App.CHANNEL_ID_NEW_ORDER);
        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            Intent fullScreenIntent = new Intent(this, ProcessOrderActivity.class);
            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(getApplicationContext());
            taskStackBuilder.addNextIntentWithParentStack(fullScreenIntent);
            fullScreenIntent.putExtra(INTENT_EXTRA_ORDER_STATUS, orderJson);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            builder.setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_CALL)
                    .setFullScreenIntent(pendingIntent, true)
                    .setAutoCancel(true)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setOngoing(true);
            Notification notification = builder.build();

            Log.d(TAG, "Opening DialogActivity from Notification......");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.notify(App.NOTIFICATION_ID_NEW_ORDER, notification);
        }else{
            Intent intent = new Intent(this, ProcessOrderActivity.class);
            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(getApplicationContext());
            taskStackBuilder.addNextIntentWithParentStack(intent);
            intent.putExtra(INTENT_EXTRA_ORDER_STATUS, orderJson);
            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            Log.d(TAG, "Starting DialogActivity......");
            startActivity(intent);
        }
        */


    }
    private void sendMessageToBroadCastReceiver(String orderJson){
        Intent intent = new Intent(SERVICE_MESSAGE);
        intent.putExtra("INTENT_EXTRA_OUTPUT_NEW_ORDERS", orderJson);
        // local broadcast receiver
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();

        }
        Log.d(TAG, "SENDING: " + orderJson);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

    }

    private PUSH_NOTIFICATION_SOURCE getNotificationSource(RemoteMessage remoteMessage){
        PUSH_NOTIFICATION_SOURCE notificationSource;

        RemoteMessage.Notification notification =  remoteMessage.getNotification();
        Map<String, String> data = remoteMessage.getData();

        if(notification != null && data != null){
            if(data.size() == 0){
                notificationSource = PUSH_NOTIFICATION_SOURCE.CONSOLE;
            }else {
                notificationSource = PUSH_NOTIFICATION_SOURCE.API_WITH_NOTIFICATION;
            }
        }else if(remoteMessage.getData() != null){// used to notify the app without user being made aware of it
            notificationSource = PUSH_NOTIFICATION_SOURCE.API_WITHOUT_NOTIFICATION;
        }else{
            notificationSource = PUSH_NOTIFICATION_SOURCE.UNKNOWN_SOURCE;
        }
        return notificationSource;
    }



}
