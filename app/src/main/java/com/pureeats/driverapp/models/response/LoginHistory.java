package com.pureeats.driverapp.models.response;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

import lombok.Data;

@Data
public class LoginHistory {
    private int id;
    @SerializedName("login_at")
    private String loginAt;
    @SerializedName("logout_at")
    private String logoutAt;
    @SerializedName("last_seen")
    private String lastSeen;
    @SerializedName("is_online")
    private boolean isOnline;
}
