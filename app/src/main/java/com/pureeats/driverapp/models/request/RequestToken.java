package com.pureeats.driverapp.models.request;

import android.content.Context;

import com.pureeats.driverapp.models.User;
import com.pureeats.driverapp.sharedprefs.UserSession;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestToken {
    @SerializedName("user_id")
    private String userId;

    private String token;

    public RequestToken(String userId, String token){
        this.userId = userId;
        this.token = token;
    }

    public RequestToken(){
    }
}
