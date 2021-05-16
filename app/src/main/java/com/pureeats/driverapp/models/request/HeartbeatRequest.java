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
    private double deliveryLat;
    @SerializedName("lng")
    private double deliveryLong;
    private String heading;

    @SerializedName("bearing")
    private float bearing;

    @SerializedName("processing_orders")
    private List<Integer> processingOrders = new ArrayList<>();


    public HeartbeatRequest(RequestToken requestToken, LatLng latLng) {
        setToken(requestToken.getToken());
        setUserId(requestToken.getUserId());
        //setDeliveryGuyId(requestToken.getDeliveryGuyId());
        this.deliveryLat = latLng.latitude;
        this.deliveryLong = latLng.longitude;
    }

    public HeartbeatRequest(RequestToken requestToken, double lat, double lng) {
        setToken(requestToken.getToken());
        setUserId(requestToken.getUserId());
        //setDeliveryGuyId(requestToken.getDeliveryGuyId());
        this.deliveryLat = lat;
        this.deliveryLong = lng;
    }

    public HeartbeatRequest(RequestToken requestToken, Location location) {
        setToken(requestToken.getToken());
        setUserId(requestToken.getUserId());
        //setDeliveryGuyId(requestToken.getDeliveryGuyId());
        this.deliveryLat = location.getLatitude();
        this.deliveryLong = location.getLongitude();
        this.bearing = location.getBearing();
    }

    public void addProcessdOrders(List<Order> orders){
        orders.forEach(order -> processingOrders.add(order.getId()));
    }
}
