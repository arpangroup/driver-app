package com.pureeats.driverapp.models.response;

import com.pureeats.driverapp.models.Earning;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

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
   private List<OrderDetailsView> orders;
   private List<Earning> earnings;
   private double totalEarnings;
}
