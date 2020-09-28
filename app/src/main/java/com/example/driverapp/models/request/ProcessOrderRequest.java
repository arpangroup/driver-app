package com.example.driverapp.models.request;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProcessOrderRequest extends RequestToken {
    @SerializedName("order_id")
    private int orderId;

    public ProcessOrderRequest(int orderId) {
        super();
        this.orderId = orderId;
    }
}
