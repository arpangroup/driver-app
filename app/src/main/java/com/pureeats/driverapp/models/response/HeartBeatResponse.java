package com.pureeats.driverapp.models.response;

import com.google.gson.annotations.SerializedName;
import com.pureeats.driverapp.models.Order;

import java.util.List;

import lombok.Data;

@Data
public class HeartBeatResponse {
    @SerializedName("new_orders")
    private List<Order> newOrders;
    @SerializedName("accepted_orders")
    private List<Order> acceptedOrders;
    @SerializedName("pickedup_orders")
    private List<Order> pickedupOrders;
}