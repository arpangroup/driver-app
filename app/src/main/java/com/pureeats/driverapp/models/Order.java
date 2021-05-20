package com.pureeats.driverapp.models;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.pureeats.driverapp.commons.OrderStatus;
import com.pureeats.driverapp.utils.FormatDate;
import com.google.gson.annotations.SerializedName;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Order implements Comparable{
    private int id;
    @SerializedName("unique_order_id")
    private String uniqueOrderId;
    @SerializedName("orderstatus_id")
    private int orderStatusId; //[1=>ORDER_PLACED, 2=>ORDER_RECEIVED, 3=>DELIVERY_GUY_ASSIGNED, 4=>ON_THE_WAY, 5=>DELIVERED, 6=>CANCELED, 7=>SELF_PICKUP]
//    @SerializedName("extra_status")
//    private String extraStatus;
    private int user_id;
//    private String coupon_name;
    private String location;
    private String address;
//    private String tax;
//    @SerializedName("restaurant_charge")
//    private int restaurantCharge;
    @SerializedName("delivery_charge")
    private String deliveryCharge;
    private double total;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("updated_at")
    private String updatedAt;
    @SerializedName("payment_mode")
    private String paymentMode;
    @SerializedName("order_comment")
    private String orderComment;
    @SerializedName("restaurant_id")
    private int restaurantId;
    //    @SerializedName("transaction_id")
//    private String transactionId;
    @SerializedName("delivery_type")
    private int deliveryType;
    @SerializedName("delivery_pin")
    private String deliveryPin;
    @SerializedName("payable")
    private String payable;
    @SerializedName("delivery_distance")
    private double deliveryDistance;
    @SerializedName("is_order_reached_message_send")
    private int isOrderReachedMessageSend;

    @SerializedName("already_accepted")
    private boolean alreadyAccepted;
    @SerializedName("customer_phone")
    private String customerPhone;
    private String commission;

    @SerializedName("restaurant_accept_at")
    private String restaurantAcceptAt;
    @SerializedName("restaurant_ready_at")
    private String restaurantReadyAt;
    @SerializedName("rider_accept_at")
    private String riderAcceptAt;
    @SerializedName("rider_reached_pickup_location_at")
    private String riderReachedPickupLocationAt;
    @SerializedName("rider_picked_at")
    private String riderPickedAt;
    @SerializedName("rider_reached_drop_location_at")
    private String riderReachedDropLocationAt;
    @SerializedName("rider_deliver_at")
    private String riderDeliverAt;


    @SerializedName("restaurant")
    private Restaurant restaurant;

//    @SerializedName("resturant_details")
//    private Restaurant restaurantDetails;

//    @SerializedName("coupon_details")
//    private CouponDetails couponDetails;

    @SerializedName("orderitems")
    private List<Dish> orderitems;

    @SerializedName("prepare_time")
    private int prepareTime;

    @SerializedName("delivery_details")
    private User deliveryDetails;

    @SerializedName("user")
    private User user;

    @SerializedName("max_order")
    private boolean maxOrder;

    private OrderStatus orderStatus;

    private Direction direction;

    public Order(int id, String uniqueOrderId) {
        this.id = id;
        this.uniqueOrderId = uniqueOrderId;
    }

    /*========================================================================*/
    private int toggle = 1;
    private boolean isAutoCancelled = false;



    public String pickUpByTime(){
        long createdTimeInLong = FormatDate.getTimeFromDateString(this.restaurantAcceptAt);
        int deliveryTimeInMin = Integer.parseInt(restaurant.getDeliveryTime());
        //long targetTime = createdTimeInLong + this.prepareTime;
        int deliveryTime = this.getRestaurant() != null ? Integer.parseInt(this.getRestaurant().getDeliveryTime()) : 0;
        int prepareTime = this.prepareTime + deliveryTime;
        long targetTime = createdTimeInLong + (prepareTime * 1000 * 60);//30 min

        DateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
        Date date = new Date(targetTime);
        System.out.println("================================");
        System.out.println("Approx. Delivery Time: "+this.restaurant.getDeliveryTime());
        System.out.println("PrepareTime: "+this.prepareTime);
        System.out.println("CreatedAt: "+this.restaurantAcceptAt);
        System.out.println("TARGET_TIME: " + targetTime);
        //System.out.println("DATE: "+ date);
        String time = dateFormat.format(targetTime);
        return time;

    }

    @Override
    public int compareTo(Object obj) {
        int compareId=((Order)obj).id;

        /* For Ascending order*/
        //return this.id-compareId;

        /* For Descending order do like this */
        return compareId-this.id;
    }


    public static DiffUtil.ItemCallback<Order> itemCallback = new DiffUtil.ItemCallback<Order>() {
        @Override
        public boolean areItemsTheSame(@NonNull Order oldItem, @NonNull Order newItem) {
            //return oldItem.getId() == newItem.getId() && oldItem.getRestaurant().getDeliveryTime().equals(newItem.getRestaurant().getDeliveryTime());
            //return oldItem.getId() == newItem.getId() ;
            return oldItem.getId() == newItem.getId() && oldItem.getOrderStatusId() == newItem.getOrderStatusId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Order oldItem, @NonNull Order newItem) {
            return oldItem.equals(newItem);
        }
    };


}