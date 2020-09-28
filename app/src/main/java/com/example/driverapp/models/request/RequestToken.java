package com.example.driverapp.models.request;

import android.content.Context;

import com.example.driverapp.models.User;
import com.example.driverapp.sharedprefs.UserSession;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestToken {
    @SerializedName("delivery_guy_id")
    private int deliveryGuyId;
    private String token;

    public RequestToken() {
        try{
            User user = UserSession.getUserData();
            this.deliveryGuyId = UserSession.getUserData().getId();
            this.token  = user.getAuthToken();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
