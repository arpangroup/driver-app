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
    @SerializedName("delivery_guy_id")
    private int deliveryGuyId;
    @SerializedName("user_id")
    private int userId;
    private String token;

    public RequestToken() {
        try{
            User user = UserSession.getUserData();
            this.deliveryGuyId = UserSession.getUserData().getId();
            this.userId = UserSession.getUserData().getId();
            this.token  = user.getAuthToken();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public RequestToken(Context context) {
        try{
            User user = UserSession.getUserData(context);
            if(user != null){
                this.deliveryGuyId = user.getId();
                this.userId = this.deliveryGuyId;
                this.token  = user.getAuthToken();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
