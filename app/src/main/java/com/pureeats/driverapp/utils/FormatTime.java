package com.pureeats.driverapp.utils;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class FormatTime {

    public static Long getTimeFromDateString(String dateStr){
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+05:30"));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");


        try {
            Date date = sdf.parse(dateStr);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0L;
        }

    }


    public static String formatTime(String timeStr){
        System.out.println("======================");
        System.out.println(timeStr);
        DateFormat inputDateFormat = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat outputFormatter = new SimpleDateFormat("hh:mm aa");
        try {
            Date date = inputDateFormat.parse(timeStr);
            return outputFormatter.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeStr;
    }


    public static Map<Integer, String> getOpeningTime(long currentTime, long openingTime, long closingTime){
        Map<Integer, String> resultMap = new HashMap<>();
        String res = "";
        boolean isCurrentlyOpen = false;

        if(currentTime >= openingTime && currentTime < closingTime){
            res += "open now";
            isCurrentlyOpen = true;
            if(closingTime - 30 > currentTime){
                int minutes = (int) ((closingTime - currentTime) / 1000) /60;
                if(minutes <=30)res += ", will close in "+ minutes +" minutes";
            }
        }
        else if(currentTime >= closingTime){
            res += "closed";
            isCurrentlyOpen = false;
        }
        else if(currentTime > openingTime && currentTime > closingTime){
            res += "will open tomorrow";
            isCurrentlyOpen = false;
        }
        else if(currentTime < openingTime && currentTime < closingTime){
            res += "will open in ";
            int minutes = (int) ((closingTime - currentTime) / 1000) /60;
            if (minutes >= 60 ){
                int hour = minutes / 60;
                res +=  hour +  " hours";
            }else{
                res += minutes + " minutes";
            }
            isCurrentlyOpen = false;
        }else{
            res +="some thing else";
            isCurrentlyOpen = false;
        }


//        System.out.println("=================================");
//        System.out.println("CURRENT_TIME: "+currentTime);
//        System.out.println("OPENING_TIME: "+openingTime);
//        System.out.println("CLOSING_TIME: "+closingTime);
//        System.out.println("---------------------------");
//        System.out.println(res);
//        System.out.println("=================================");
        resultMap.put(1, res);
        resultMap.put(2, "false");
        if(isCurrentlyOpen)resultMap.put(2, "true");
        return resultMap;

    }
    public static Map<Integer, String>  handleOpenCloseTime(String currentTimeStr, String openingTimeStr, String closingTimeStr){
        long currentTime = getTimeFromDateString(currentTimeStr);
        long openingTime = getTimeFromDateString(openingTimeStr);
        long closingTime = getTimeFromDateString(closingTimeStr);
        return getOpeningTime(currentTime, openingTime, closingTime);
    }
    public static String getCurrentDate(){
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+05:30"));
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String strDate= formatter.format(date);
        return strDate;
    }
    public static String getCurrentTime(){
//        TimeZone.setDefault(TimeZone.getTimeZone("IST"));
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+05:30"));
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd G 'at' HH:mm:ss");
        Date date = new Date();
        //sd.setTimeZone(TimeZone.getTimeZone("IST"));
        String timeStamp = sd.format(date);

        Log.d("CURRENT_FULL_TIME: ", "TIME: "+timeStamp);

        String str[] = timeStamp.split(" ");

        return str[3];
    }

    public static long getCurrentTimeInLong(){
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+05:30"));
        String currentTimeStr = getCurrentDate() + " " + getCurrentTime();
        long currentTime = getTimeFromDateString(currentTimeStr);
        return currentTime;
    }
    public static String getCurrentDateTime(){
        String currentTimeStr = getCurrentDate() + " " + getCurrentTime();
        return currentTimeStr;
    }



    public static void mainTest(){

        // Restaurant Opening and closing time...

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String todayDate= formatter.format(date);



        // String currentTimeStr = "2020-08-03 22:50:00";
        // String openingTimeStr = "2020-08-03 07:00:00";
        // String closingTimeStr = "2020-08-03 23:00:00";

        //String currentTimeStr = todayDate + " 22:50:00";
        //String openingTimeStr = todayDate + " 07:00:00";
        //String closingTimeStr = todayDate + " 23:00:00";


        String currentTimeStr = getCurrentDate() + " " + getCurrentTime();
        String openingTimeStr = todayDate + " 07:00:00";
        String closingTimeStr = todayDate + " 23:00:00";

        handleOpenCloseTime(currentTimeStr, openingTimeStr, closingTimeStr);


    }


}


