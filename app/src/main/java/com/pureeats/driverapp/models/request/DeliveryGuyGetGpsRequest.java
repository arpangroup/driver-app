package com.pureeats.driverapp.models.request;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeliveryGuyGetGpsRequest  {
    @SerializedName("order_id")
    private String orderId;

    public DeliveryGuyGetGpsRequest(String orderId) {
        this.orderId = orderId;
    }
}
