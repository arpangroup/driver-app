package com.example.driverapp.models.request;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeliverOrderRequest extends RequestToken {
    @SerializedName("order_id")
    private int orderId;
    @SerializedName("delivery_pin")
    private String deliveryPin;

    public DeliverOrderRequest(int orderId, String deliveryPin) {
        super();
        this.orderId = orderId;
        this.deliveryPin = deliveryPin;
    }
}
