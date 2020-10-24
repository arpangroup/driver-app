package com.pureeats.driverapp.models.request;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeliveryGuySetGpsRequest extends RequestToken {
    @SerializedName("delivery_lat")
    private double deliveryLat;
    @SerializedName("delivery_long")
    private double deliveryLong;
    private String heading;

    public DeliveryGuySetGpsRequest(RequestToken requestToken, LatLng latLng) {
        setToken(requestToken.getToken());
        setUserId(requestToken.getUserId());
        setDeliveryGuyId(requestToken.getDeliveryGuyId());
        this.deliveryLat = latLng.latitude;
        this.deliveryLong = latLng.longitude;
    }
}
