package com.example.driverapp.models;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class User {
    private int id;
    @SerializedName("auth_token")
    String authToken;
    private String name;

    @SerializedName("nick_name")
    private String nickName;
    private String email;
    @SerializedName("wallet_balance")
    private double walletBalance;
    @SerializedName("onGoingCount")
    private int onGoingCount;
    @SerializedName("completedCount")
    private int completedCount;
    private String age;
    private String gender;
    private String description;
    private String phone;
    private String photo;

    @SerializedName("vehicle_number")
    private String vehicleNumber;

    @SerializedName("commission_rate")
    private String commissionRate;

    @SerializedName("is_notifiable")
    private int isNotifiable;

    @SerializedName("max_accept_delivery_limit")
    private int maxAcceptDeliveryLimit;

    @SerializedName("delivery_pin")
    private String deliveryPin;

    @SerializedName("push_token")
    String pushToken;

    private Address address;

}
