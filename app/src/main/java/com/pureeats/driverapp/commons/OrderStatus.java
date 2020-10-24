package com.pureeats.driverapp.commons;

public enum OrderStatus {
    ORDER_PLACED(1), // WaitingForRestaurantToAcceptOrder           [NEW_ORDER]
    ORDER_RECEIVED(2),//Restaurant is preparing your order          [PREPARING]
    DELIVERY_GUY_ASSIGNED(3), //On the way to pickup you order      [PREPARING]
    ORDER_READY(7),//Order ready to deliver to  DeliveryGuy               [READY]
    ON_THE_WAY(4),// Order is pickedUp and is on its way to deliver [PICKED]
    DELIVERED(5),
    CANCELED(6),
    //REACHED_PICKUP_LOCATION(8),
    //REACHED_DELIVERY_LOCATION(9),
    ORDER_READY_TO_PICKUP(10);


    private final int value;
    OrderStatus(int value) {
        this.value = value;
    }

    public int value(){
        return value;
    }


}
