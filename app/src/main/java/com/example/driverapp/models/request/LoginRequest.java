package com.example.driverapp.models.request;

import com.example.driverapp.models.Address;
import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LoginRequest {
    @NonNull
    @SerializedName("login_type")
    private String loginType;
    private String name;
    private  String email;
    private String password;
    private String accessToken;
    private String phone;
    private String otp;
    private String provider;
    private Address address;
    @SerializedName("push_token")
    private String pushToken;

    public LoginRequest(String phone, String otp) {
        this.phone = phone;
        this.otp = otp;
    }
}
