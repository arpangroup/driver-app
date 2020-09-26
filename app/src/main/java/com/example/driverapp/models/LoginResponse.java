package com.example.driverapp.models;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@NoArgsConstructor
@ToString
public class LoginResponse<T> {
    private boolean success;
    private String message;
    private T data;
    @SerializedName("running_order")
    private T runningOrder;
}
