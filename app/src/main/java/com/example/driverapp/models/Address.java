package com.example.driverapp.models;


import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Address {
    private int id;
    @SerializedName("user_id")
    private int userId;
    private String address;
    private String house;
    private String landmark;
    private String tag;
    private String latitude;
    private String longitude;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("updated_at")
    private String updatedAt;

    private boolean isDefault = false;

    @SerializedName("is_operational")
    private int isOperational;

    public Address() {
    }

    public Address(String address, String house, String landmark, String tag, String latitude, String longitude) {
        this.address = address;
        this.house = house;
        this.landmark = landmark;
        this.tag = tag;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getConstructedAddress(){
        return this.address;
    }
}
