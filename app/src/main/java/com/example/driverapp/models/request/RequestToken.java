package com.example.driverapp.models.request;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestToken {
    @SerializedName("user_id")
    private int userId ;
    @SerializedName("delivery_guy_id")
    private int deliveryGuyId;
    private String token;
}
