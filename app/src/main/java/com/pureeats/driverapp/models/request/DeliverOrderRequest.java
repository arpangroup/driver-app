package com.pureeats.driverapp.models.request;

import com.google.gson.annotations.SerializedName;
import com.pureeats.driverapp.models.Direction;
import com.pureeats.driverapp.models.Distance;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeliverOrderRequest extends RequestToken {
    @SerializedName("order_id")
    private int orderId;
    @SerializedName("delivery_pin")
    private String deliveryPin;

    @SerializedName("distance_travelled")
    private long distanceTravelled;
    @SerializedName("distance_travelled_text")
    private String distanceTravelledText;

    @SerializedName("duration_val")
    private long durationVal;
    @SerializedName("duration_text")
    private String durationText;

    public DeliverOrderRequest(int orderId, String deliveryPin) {
        super();
        this.orderId = orderId;
        this.deliveryPin = deliveryPin;
    }

//    public DeliverOrderRequest(int orderId, String deliveryPin, long distanceTravelled, String distanceTravelledText) {
//        super();
//        this.orderId = orderId;
//        this.deliveryPin = deliveryPin;
//        this.distanceTravelled = distanceTravelled;
//        this.distanceTravelledText = distanceTravelledText;
//    }


}
