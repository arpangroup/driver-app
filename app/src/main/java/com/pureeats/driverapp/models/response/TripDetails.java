package com.pureeats.driverapp.models.response;

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
    private String route;
    private String meta;
}
