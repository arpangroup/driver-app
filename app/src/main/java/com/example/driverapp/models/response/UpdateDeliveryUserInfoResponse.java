package com.example.driverapp.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
public class UpdateDeliveryUserInfoResponse {
   private String id;
   @SerializedName("auth_token")
   private String authToken;
   private String name;
   private String email;
   @SerializedName("wallet_balance")
   private String walletBalance;
   private String onGoingCount;
   private String completedCount;
   private List<OrderView> orders;
   private List<Earning> earnings;
   private double totalEarnings;
}
