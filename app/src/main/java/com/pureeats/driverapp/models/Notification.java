package com.pureeats.driverapp.models;

import android.util.Log;

import com.pureeats.driverapp.commons.NotificationType;
import com.pureeats.driverapp.commons.OrderStatus;

import java.util.Map;

import lombok.Data;

@Data
public class Notification {
    private NotificationType notificationType;
    private String title;
    private String message;
    private int orderId;
    private String uniqueOrderId;

    private String badge;
    private String icon;
    private String clickAction;
    private OrderStatus orderStatus;




    public static Notification mapFrom(Map<String, String> data){
        Notification notification = new Notification();
        try{
            notification.setTitle(data.get("title"));
            notification.setMessage(data.get("message"));
            notification.setUniqueOrderId(data.get("unique_order_id"));
            notification.setBadge(data.get("badge"));
            notification.setIcon(data.get("icon"));
            notification.setClickAction("click_action");

            // set OrderId:
            try{
                String orderId = data.get("order_id");
                if(orderId != null) notification.setOrderId(Integer.parseInt(orderId));
            }catch (Exception e){
                e.printStackTrace();
            }

            // set NotificationType:
            try{
                String notificationTypeStr = data.get("notification_type");
                NotificationType notificationType = NotificationType.getType(notificationTypeStr);
                if(notificationType == null) notificationType = NotificationType.DEFAULT;
                notification.setNotificationType(notificationType);
            }catch (Exception e){e.printStackTrace();}

            // set OrderStatus:
            try{
                String orderStatusStr = data.get("order_status_id");
                OrderStatus orderStatus = null;
                if(orderStatusStr != null){
                    orderStatus = OrderStatus.getStatus(Integer.parseInt(orderStatusStr));
                }
                notification.setOrderStatus(orderStatus);
            }catch (Exception e){e.printStackTrace();}


        }catch (Exception e){
            e.printStackTrace();
        }
        return notification;
    }
}
