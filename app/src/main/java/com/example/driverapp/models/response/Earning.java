package com.example.driverapp.models.response;

import com.example.driverapp.models.Order;
import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Earning {
    private int id;
    @SerializedName("payable_type")
    private String payableType;
    @SerializedName("payable_id")
    private int payableId;
    @SerializedName("wallet_id")
    private int walletId;
    private String type;
    private double amount;
    private boolean confirmed;
    private Meta meta;
    private String uuid;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("updated_at")
    private String updatedAt;

}
