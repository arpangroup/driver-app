package com.pureeats.driverapp.models.response;

import com.google.gson.annotations.SerializedName;
import com.pureeats.driverapp.models.Order;

import java.util.List;

import lombok.Data;

@Data
public class HeartBeatResponse {
    private boolean success;
    @SerializedName("new_orders")
    private List<Order> newOrders;
    @SerializedName("accepted_orders")
    private List<Order> acceptedOrders;
    @SerializedName("pickedup_orders")
    private List<Order> pickedupOrders;
    @SerializedName("cancelled_orders")
    private List<Order> cancelledOrders;
    @SerializedName("transferred_orders")
    private List<Order> transferredOrders;

    @SerializedName("on_going_deliveries_count")
    private int onGoingDeliveriesCount;
    @SerializedName("completed_deliveries_count")
    private int completedDeliveriesCount;
}
