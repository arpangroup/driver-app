package com.pureeats.driverapp.models.response;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class DeliveryGuyGpsResponse {
    private int id;
    @SerializedName("order_id")
    private int orderId;

    @SerializedName("user_lat")
    private String userLat;
    @SerializedName("user_long")
    private String userLong;

    @SerializedName("delivery_lat")
    private String deliveryLat;
    @SerializedName("delivery_long")
    private String deliveryLong;

    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("updated_at")
    private String updatedAt;
    
    private String heading;

}
