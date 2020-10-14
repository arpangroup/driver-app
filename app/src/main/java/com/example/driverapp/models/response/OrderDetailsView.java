package com.example.driverapp.models.response;

import com.example.driverapp.models.Order;
import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderDetailsView {
    private int id;
    @SerializedName("order_id")
    private int orderId;
    @SerializedName("user_id")
    private int userId;
    @SerializedName("customer_id")
    private int customerId;
    private int is_complete;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("updated_at")
    private String updatedAt;

    private Order order;

}
