package com.example.driverapp.models.response;

import com.example.driverapp.models.Order;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class DeliveryOrderResponse {
    @SerializedName("accepted_orders")
    private List<Order> acceptedOrders;
    @SerializedName("new_orders")
    private List<Order> newOrders;
    @SerializedName("listed_orders")
    private List<Order> listedOrders;
}
