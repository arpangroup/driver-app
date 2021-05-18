package com.pureeats.driverapp.models.request;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;
import com.pureeats.driverapp.models.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HeartbeatRequest extends RequestToken {
    @SerializedName("lat")
    private String deliveryLat = null;
    @SerializedName("lng")
    private String deliveryLong = null;
    private String heading;
    private int count = 0;

    @SerializedName("bearing")
    private float bearing;

    @SerializedName("processing_orders")
    private List<Integer> processingOrders = new ArrayList<>();


    public HeartbeatRequest(RequestToken requestToken, LatLng latLng) {
        setToken(requestToken.getToken());
        setUserId(requestToken.getUserId());
        //setDeliveryGuyId(requestToken.getDeliveryGuyId());
        if(latLng != null){
            this.deliveryLat = String.valueOf(latLng.latitude);
            this.deliveryLong = String.valueOf(latLng.longitude);
        }
    }

    public HeartbeatRequest(RequestToken requestToken) {
        setToken(requestToken.getToken());
        setUserId(requestToken.getUserId());
    }



    public void addProcessdOrders(List<Order> orders){
        orders.forEach(order -> processingOrders.add(order.getId()));
    }
}
