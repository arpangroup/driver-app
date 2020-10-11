package com.example.driverapp.utils;

import java.text.DecimalFormat;

public class FormatPrice {

    public static String format(float price){
        DecimalFormat decimalFormat  = new DecimalFormat("#.##");
        float twoDigitsF = Float.parseFloat(decimalFormat.format(price));
        return String.valueOf(twoDigitsF);
    }
    public static String format(String price){
       double priceInDouble = Double.parseDouble(price);
       return format(priceInDouble);
    }
    public static String format(double price){
        DecimalFormat decimalFormat  = new DecimalFormat("#.##");
        float twoDigitsF = Float.parseFloat(decimalFormat.format(price));
        return String.valueOf(twoDigitsF);
    }

    public static String calculatePercentageOff(String oldPrice, String newPrice){
        double previousPrice = Double.parseDouble(oldPrice);
        double currentPrice = Double.parseDouble(newPrice);

        double discount = previousPrice - currentPrice;
        double disPercent = (discount / previousPrice) *100 ;

        return format(disPercent);
    }

    public static String formatDecimalPoint(double d)
    {
        if(d == (long) d)
            return String.format("%d",(long)d);
        else
            return String.format("%s",d);
    }
    public static String formatDecimalPoint(String d)
    {
        double decimal =Double.parseDouble(d);
        return formatDecimalPoint(decimal);
    }
}
