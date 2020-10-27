package com.pureeats.driverapp.models.response;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
public class Dashboard {
    @SerializedName("todays_order_count")
    private int todaysOrderCount;
    @SerializedName("todays_earning_amount")
    private double todaysEarningAmount;


    @SerializedName("yesterday_order_count")
    private int yesterdayOrder_Count;
    @SerializedName("yesterday_earning_amount")
    private double yesterdayEarningAmount;

    @SerializedName("this_week_order_count")
    private int thisWeekOrderCount;
    @SerializedName("this_week_earning_amount")
    private double thisWeekEarningAmount;


    @SerializedName("this_month_order_count")
    private int thisMonthOrderCount;
    @SerializedName("this_month_earning_amount")
    private double thisMonthEarningAmount;


    @SerializedName("cash_in_hold")
    private double cashInHold;
    @SerializedName("last_payment")
    private double last_payment;
}
