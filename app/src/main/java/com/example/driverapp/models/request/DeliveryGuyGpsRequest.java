package com.example.driverapp.models.request;

import com.example.driverapp.models.request.RequestToken;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeliveryGuyGpsRequest extends RequestToken {
    @SerializedName("delivery_lat")
    private String deliveryLat;
    @SerializedName("delivery_long")
    private String deliveryLong;
    private String heading;

    public DeliveryGuyGpsRequest(String deliveryLat, String deliveryLong) {
        super();
        this.deliveryLat = deliveryLat;
        this.deliveryLong = deliveryLong;
    }
}
