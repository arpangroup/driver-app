package com.example.driverapp.models;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.example.driverapp.models.Order;
import com.example.driverapp.models.response.Meta;
import com.example.driverapp.models.response.OrderDetailsView;
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


    public static DiffUtil.ItemCallback<Earning> itemCallback = new DiffUtil.ItemCallback<Earning>() {
        @Override
        public boolean areItemsTheSame(@NonNull Earning oldItem, @NonNull Earning newItem) {
            //return oldItem.getId() == newItem.getId() && oldItem.getRestaurant().getDeliveryTime().equals(newItem.getRestaurant().getDeliveryTime());
            //return oldItem.getId() == newItem.getId() ;
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Earning oldItem, @NonNull Earning newItem) {
            return oldItem.equals(newItem);
        }
    };


}
