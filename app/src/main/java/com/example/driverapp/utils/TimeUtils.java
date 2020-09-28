package com.example.driverapp.utils;

public class TimeUtils {
    static final long SECOND = 1000;        // no. of ms in a second
    static final long MINUTE = SECOND * 60; // no. of ms in a minute
    static final long HOUR = MINUTE * 60;   // no. of ms in an hour
    static final long DAY = HOUR * 24;      // no. of ms in a day
    static final long WEEK = DAY * 7;       // no. of ms in a week
    static final long MONTH = DAY * 30;       // no. of ms in a month
    static final long YEAR = MONTH * 12;       // no. of ms in a year

    public static String getRoundUpRemainingTime(long timeInMills){
        String text = "";
        if(timeInMills >= YEAR){
            long val =  Math.round(timeInMills / YEAR);
            if(val == 1) text = val + " year";
            else text = val + " years";
            return text;
        }
        else if(timeInMills >= MONTH){
            long val =  Math.round(timeInMills / MONTH);
            if(val == 1) text = val + " month";
            else text = val + " months";
            return text;
        }
        else if(timeInMills >= WEEK){
            long val =  Math.round(timeInMills / WEEK);
            if(val == 1) text = val + " week";
            else text = val + " weeks";
            return text;
        }
        else if(timeInMills >= DAY){
            long val =  Math.round(timeInMills / DAY);
            if(val == 1) text = val + " day";
            else text = val + "  days";
            return text;
        }
        else if(timeInMills >= HOUR){
            long val =  Math.round(timeInMills / HOUR);
            if(val == 1) text = val + " hour";
            else text = val + " hours";
            return text;
        }
        else if(timeInMills >= MINUTE){
            long val =  Math.round(timeInMills / MINUTE);
            if(val == 1) text = val + " min";
            else text = val + " mins";
            return text;
        }
//        else if(timeInMills < MINUTE){
//            long val =  Math.round(timeInMills / SECOND);
//            if(val == 1) text = val + " sec";
//            else text = val + " secs";
//            return text;
//        }else{
//            return null;
//        }
        return text;
    }
}
