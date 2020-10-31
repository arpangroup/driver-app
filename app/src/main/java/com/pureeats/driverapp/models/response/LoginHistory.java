package com.pureeats.driverapp.models.response;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import java.util.Map;

import lombok.Data;

@Data
public class LoginHistory {
    private int id;
    private int user_id;
    private String location;
    private String login_at;
    private String last_checkout_at;
    private String logout_at;
    private String created_at;
    private String update_at;



    public static DiffUtil.ItemCallback<LoginHistory> itemCallback = new DiffUtil.ItemCallback<LoginHistory>() {
        @Override
        public boolean areItemsTheSame(@NonNull LoginHistory oldItem, @NonNull LoginHistory newItem) {
            //return oldItem.getId() == newItem.getId() && oldItem.getRestaurant().getDeliveryTime().equals(newItem.getRestaurant().getDeliveryTime());
            //return oldItem.getId() == newItem.getId() ;
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull LoginHistory oldItem, @NonNull LoginHistory newItem) {
            return oldItem.equals(newItem);
        }
    };
}
