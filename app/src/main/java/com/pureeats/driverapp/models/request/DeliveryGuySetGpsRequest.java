package com.pureeats.driverapp.models.request;

import android.location.Location;

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

    @SerializedName("bearing")
    private float bearing;


    public DeliveryGuySetGpsRequest(RequestToken requestToken, LatLng latLng) {
        setToken(requestToken.getToken());
        setUserId(requestToken.getUserId());
        //setDeliveryGuyId(requestToken.getDeliveryGuyId());
        this.deliveryLat = latLng.latitude;
        this.deliveryLong = latLng.longitude;
    }
    public DeliveryGuySetGpsRequest(RequestToken requestToken, Location location) {
        setToken(requestToken.getToken());
        setUserId(requestToken.getUserId());
        //setDeliveryGuyId(requestToken.getDeliveryGuyId());
        this.deliveryLat = location.getLatitude();
        this.deliveryLong = location.getLongitude();
        this.bearing = location.getBearing();
    }
}
