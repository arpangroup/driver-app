package com.pureeats.driverapp.models.request;

import com.pureeats.driverapp.models.Address;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

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
    private Map<String, String> meta;

    public LoginRequest(String phone, String otp, String pushToken, Map<String, String> meta) {
        this.phone = phone;
        this.otp = otp;
        this.pushToken = pushToken;
        this.meta = meta;
    }
}
