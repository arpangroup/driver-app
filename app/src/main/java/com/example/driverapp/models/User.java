package com.example.driverapp.models;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class User {
    int id;
    @SerializedName("auth_token")
    String authToken;
    private String name;
    private String email;
    @SerializedName("email_verified_at")
    private String emailVerifiedAt;
    private String phone;
    @SerializedName("default_address_id")
    private int defaultAddressId;
    @SerializedName("default_address")
    private Address address;
    @SerializedName("delivery_pin")
    private String deliveryPin;
    @SerializedName("wallet_balance")
    private double walletBalance;
    private String photo;
    private String password;

    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("push_token")
    private String pushToken;
}
