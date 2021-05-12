package com.pureeats.driverapp.commons;

public enum OrderStatus {
    ORDER_PLACED(1), // WaitingForRestaurantToAcceptOrder           [NEW_ORDER]
    ORDER_RECEIVED(2),//Restaurant is preparing your order          [PREPARING]
    DELIVERY_GUY_ASSIGNED(3), //On the way to pickup you order      [PREPARING]
    ON_THE_WAY(4),// Order is pickedUp and is on its way to deliver [PICKED]
    DELIVERED(5),
    CANCELED(6),
    ORDER_READY(7),//Order ready to deliver to  DeliveryGuy         [READY]
    AWAITING_PAYMENT(8),
    PAYMENT_FAILED(9),
    REACHED_PICKUP_LOCATION(10),//NEW
    REACHED_DROP_LOCATION(11),//NEW
    ORDER_READY_AND_DELIVERY_ASSIGNED(73),//NEW
    ORDER_READY_AND_DELIVERY_REACHED_TO_PICKUP(710);//NEW


    private final int statusId;
    OrderStatus(int statusId) {
        this.statusId = statusId;
    }

    public int status(){
        return statusId;
    }

    public static OrderStatus getStatus(int statusId){
        for (OrderStatus status : values()){
            if(status.statusId == statusId){
                return status;
            }
        }
        return null;
    }


}
