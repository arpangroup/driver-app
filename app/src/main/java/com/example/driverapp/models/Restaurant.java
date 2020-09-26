package com.example.driverapp.models;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@ToString
@Accessors(chain = true)
public class Restaurant {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("contact_number")
    private String contactNumber;

    @SerializedName("opening_time")
    private String openingTime;

    @SerializedName("closing_time")
    private String closingTime;

    @SerializedName("msg")
    private String msg;

    @SerializedName("location_id")
    private String locationId;

    @SerializedName("image")
    private String image;

    @SerializedName("rating")
    private String rating;

    @SerializedName("delivery_time")
    private String deliveryTime;


    private Long deliveryTimeVal = 0l; // will be updated by google direction API
    private String deliveryTimeText; // will be updated by google direction API

    @SerializedName("price_range")
    private String priceRange;

    @SerializedName("is_pureveg")
    private int isPureVeg;

    @SerializedName("slug")
    private String slug;

    @SerializedName("placeholder_image")
    private String placeholderImage;

    @SerializedName("latitude")
    private String latitude;

    @SerializedName("longitude")
    private String longitude;

    @SerializedName("certificate")
    private String certificate;

    @SerializedName("restaurant_charges")
    private String restaurantCharges;

    @SerializedName("delivery_charges")
    private String deliveryCharges;

    @SerializedName("address")
    private String address;

    @SerializedName("pincode")
    private String pinCode;

    @SerializedName("landmark")
    private String landmark;

    @SerializedName("sku")
    private String sku;

    @SerializedName("is_active")
    private int isActive;

    @SerializedName("is_accepted")
    private int isAccepted;

    @SerializedName("is_featured")
    private int isFeatured;

    @SerializedName("commission_rate")
    private String commissionRate;

    @SerializedName("delivery_type")
    private int deliveryType;

    @SerializedName("delivery_radius")
    private String deliveryRadius;

    @SerializedName("delivery_charge_type")
    private String deliveryChargeType; //[DYNAMIC]

    @SerializedName("base_delivery_charge")
    private String baseDeliveryCharge;

    @SerializedName("base_delivery_distance")
    private int baseDeliveryDistance;

    @SerializedName("extra_delivery_charge")
    private String extraDeliveryCharge;

    @SerializedName("extra_delivery_distance")
    private int extraDeliveryDistance;

    @SerializedName("min_order_price")
    private String minOrderPrice;

    @SerializedName("is_notifiable")
    private int isNotifiable;

    @SerializedName("auto_acceptable")
    private int autoAcceptable;

    @SerializedName("schedule_data")
    private String scheduleData;

    @SerializedName("is_schedulable")
    private int isSchedulable;

    private boolean filtered = false;
}
