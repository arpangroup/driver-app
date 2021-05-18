package com.pureeats.driverapp.models;

import android.util.Log;

import com.pureeats.driverapp.commons.Constants;
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
            notification.setTitle(data.get(Constants.STR_TITLE));
            notification.setMessage(data.get(Constants.STR_MESSAGE));
            notification.setUniqueOrderId(data.get(Constants.STR_UNIQUE_ORDER_ID));
            notification.setBadge(data.get(Constants.STR_BADGE));
            notification.setIcon(data.get(Constants.STR_ICON));
            notification.setClickAction(data.get(Constants.STR_CLICK_ACTION));

            // set OrderId:
            try{
                String orderId = data.get(Constants.STR_ORDER_ID);
                if(orderId != null) notification.setOrderId(Integer.parseInt(orderId));
            }catch (Exception e){
                e.printStackTrace();
            }

            // set NotificationType:
            try{
                String notificationTypeStr = data.get(Constants.STR_NOTIFICATION_TYPE);
                NotificationType notificationType = NotificationType.getType(notificationTypeStr);
                if(notificationType == null) notificationType = NotificationType.DEFAULT;
                notification.setNotificationType(notificationType);
            }catch (Exception e){e.printStackTrace();}

            // set OrderStatus:
            try{
                String orderStatusStr = data.get(Constants.STR_ORDER_STATUS_ID);
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
