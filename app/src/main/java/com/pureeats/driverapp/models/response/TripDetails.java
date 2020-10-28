package com.pureeats.driverapp.models.response;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.pureeats.driverapp.models.Earning;

import java.util.Map;

import lombok.Data;

@Data
public class TripDetails {
    private int id;
    private int order_id;
    private int customer_id;
    private int restaurant_id;
    private int rider_id;
    private int delivery_collection_id;
    private String distance_travelled;
    private String rider_earning;
    private String restaurant_earning;
    private String cash_collected_from_customer;
    private String cash_on_hold;
    private Route route;
    private Map<String, String> meta;
    private String created_at;
    private String update_at;



    public static DiffUtil.ItemCallback<TripDetails> itemCallback = new DiffUtil.ItemCallback<TripDetails>() {
        @Override
        public boolean areItemsTheSame(@NonNull TripDetails oldItem, @NonNull TripDetails newItem) {
            //return oldItem.getId() == newItem.getId() && oldItem.getRestaurant().getDeliveryTime().equals(newItem.getRestaurant().getDeliveryTime());
            //return oldItem.getId() == newItem.getId() ;
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull TripDetails oldItem, @NonNull TripDetails newItem) {
            return oldItem.equals(newItem);
        }
    };
}
