package com.pureeats.driverapp.models.response;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.pureeats.driverapp.models.Order;
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


    public static DiffUtil.ItemCallback<OrderDetailsView> itemCallback = new DiffUtil.ItemCallback<OrderDetailsView>() {
        @Override
        public boolean areItemsTheSame(@NonNull OrderDetailsView oldItem, @NonNull OrderDetailsView newItem) {
            //return oldItem.getId() == newItem.getId() && oldItem.getRestaurant().getDeliveryTime().equals(newItem.getRestaurant().getDeliveryTime());
            //return oldItem.getId() == newItem.getId() ;
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull OrderDetailsView oldItem, @NonNull OrderDetailsView newItem) {
            return oldItem.equals(newItem);
        }
    };

}
