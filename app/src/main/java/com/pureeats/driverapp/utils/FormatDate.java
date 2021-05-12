package com.pureeats.driverapp.utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Date;

public class FormatDate {
    public static String[] MONTHS = {"Jan", "Feb", "March", "April", "May", "Jun", "July", "Aug", "Sept", "Oct", "Nov", "Dec"};

    public static String format1(String dateStr){//2020-07-23 07:03:37
        String[] dateParts = getDateParts(dateStr);
        if(dateParts.length >  0){
            String day    = dateParts[0];
            String month  = dateParts[1];
            String year   = dateParts[2];
            String hour   = dateParts[3];
            String minute = dateParts[4];
            String second = dateParts[5];
            String am     = dateParts[6];

            int monthIndex = Integer.parseInt(month) - 1;
            String str = day + " "+ MONTHS[monthIndex] + " "+year +" at "+ hour +":"+minute +" "+am;//10 May 2019 at 12:00 PM
            return str;
        }
        return dateStr;
    }

    public static String getTime(String timeStr){
        DateFormat dateFormat = new SimpleDateFormat("HH:mm aa");
        String dateString = dateFormat.format(timeStr).toString();
        return dateString;
    }

    public static String format2(String dateStr){
        String[] dateParts = getDateParts(dateStr);
        if(dateParts.length >  0){
            String day    = dateParts[0];
            String month  = dateParts[1];
            String year   = dateParts[2];
            String hour   = dateParts[3];
            String minute = dateParts[4];
            String second = dateParts[5];
            String am     = dateParts[6];

            int monthIndex = Integer.parseInt(month) - 1;
            String str = hour +":"+minute +" "+am;// 12:00 PM
            return str;
        }
        return dateStr;
    }
    public static String format3(String dateStr){
        String[] dateParts = getDateParts(dateStr);
        if(dateParts.length >  0){
            String day    = dateParts[0];
            String month  = dateParts[1];
            String year   = dateParts[2];
            String hour   = dateParts[3];
            String minute = dateParts[4];
            String second = dateParts[5];
            String am     = dateParts[6];
            if(month.length() == 1) month +="0"+month;

            int monthIndex = Integer.parseInt(month) - 1;
            String str = day +"/"+month +"/"+year;// 22/09/2020
            return str;
        }
        return dateStr;
    }

    public static String[] getDateParts(String dateStr){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat formatter = new SimpleDateFormat("dd,MM,yyyy,hh,mm,ss,a");
        try {
            Date date1 = sdf.parse(dateStr);

            String sDate = formatter.format(date1);
            String[] dateParts = sDate.split(",");

            return dateParts;
        } catch (ParseException e) {
            e.printStackTrace();
            return new String[]{};
        }
    }

    private static Long getTimeDifference(String dateStr){
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        long diff = 0;
        //String time1 = "01:03:23";
        //String time2 = "02:32:00";


        Date date1 = new Date();
        try {
            Date date2 = formatter.parse(dateStr);
            diff = date1.getTime()  - date2.getTime();

            long diffMilliseconds = diff;
            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);

            return diff;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return diff;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Long getTimeDifferenceInMinute(String timeStr){
//        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
//        formatter.setTimeZone(TimeZone.getTimeZone("IST"));

//        long diff = 0;
        //String time1 = "01:03:23";
        //String time2 = "02:32:00";


//        Date date1 = new Date();
        // try {
        // Date date2 = formatter.parse(dateStr);
        // long time1 =  date1.getTime();
        // long time2 =  date2.getTime();

        // if(time1 > time2){ //current time is greater
        //     System.out.println("Current time is greater");
        // }else{
        //     System.out.println("opening time is greater");
        // }


        // diff = date1.getTime()  - date2.getTime();

        // long diffMilliseconds = diff;
        // long diffSeconds = (diff / 1000) % 60;
        // long diffMinutes = (diff / (60 * 1000)) % 60;
        // long diffHours = diff / (60 * 60 * 1000) % 24;
        // long diffDays = diff / (24 * 60 * 60 * 1000);

        // System.out.println("CurrentTime: "+formatter.format(date1));
        // System.out.println("PassedTime : "+formatter.format(date2));
        // System.out.println("Difference : "+diffHours +" hour");


        //String t1 = "23:05:38";
//        String t2 = "22:51:07";

        LocalTime time1_now = LocalTime.now();
        LocalTime time1_h = time1_now.plusHours(5);
        LocalTime time1 = time1_h.plusMinutes(30);

        //LocalTime time2_now = LocalTime.parse(t2);
        //LocalTime time2_h = time2_now.plusHours(5);
        //LocalTime time2 = time2_h.plusMinutes(30);
        LocalTime time2 = LocalTime.parse(timeStr);




        //System.out.println("NOW: "+time);
        //LocalTime t1 = LocalTime.parse(time1);
        //LocalTime t2 = LocalTime.parse(time2);
        Duration duration = Duration.between(time1, time2);
        long durationInHour = duration.toHours();
        long durationInMin = duration.toMinutes();
        long durationInMills = duration.toMillis();


        //System.out.println("CurrentTime: "+time1);
        //System.out.println("PassedTime : "+time2);
        //System.out.println("Difference : "+durationInMin +" minute");


        // return diff;
        // } catch (ParseException e) {
        //     e.printStackTrace();
        // }

        return durationInMin;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Long getTimeDifferenceInMinuteFromDateString(String dateStr){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");


        Date date1 = null;
        String timeStr = null;
        try {
            date1 = sdf.parse(dateStr);
            timeStr = formatter.format(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        LocalTime time1_now = LocalTime.now();
        LocalTime time1_h = time1_now.plusHours(5);
        LocalTime time1 = time1_h.plusMinutes(30);

        //LocalTime time2_now = LocalTime.parse(t2);
        //LocalTime time2_h = time2_now.plusHours(5);
        //LocalTime time2 = time2_h.plusMinutes(30);
        LocalTime time2 = LocalTime.parse(timeStr);




        //System.out.println("NOW: "+time);
        //LocalTime t1 = LocalTime.parse(time1);
        //LocalTime t2 = LocalTime.parse(time2);
        Duration duration = Duration.between(time1, time2);
        long durationInHour = duration.toHours();
        long durationInMin = duration.toMinutes();
        long durationInMills = duration.toMillis();


        //System.out.println("CurrentTime: "+time1);
        //System.out.println("PassedTime : "+time2);
        //System.out.println("Difference : "+durationInMin +" minute");


        // return diff;
        // } catch (ParseException e) {
        //     e.printStackTrace();
        // }

        return durationInMin;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Long getTimeDifferenceInMillisecondsFromDateString(String dateStr){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");


        Date date1 = null;
        String timeStr = null;
        try {
            date1 = sdf.parse(dateStr);
            timeStr = formatter.format(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        LocalTime time1_now = LocalTime.now();
        LocalTime time1_h = time1_now.plusHours(5);
        LocalTime time1 = time1_h.plusMinutes(30);

        //LocalTime time2_now = LocalTime.parse(t2);
        //LocalTime time2_h = time2_now.plusHours(5);
        //LocalTime time2 = time2_h.plusMinutes(30);
        LocalTime time2 = LocalTime.parse(timeStr);




        //System.out.println("NOW: "+time);
        //LocalTime t1 = LocalTime.parse(time1);
        //LocalTime t2 = LocalTime.parse(time2);
        Duration duration = Duration.between(time1, time2);
        long durationInHour = duration.toHours();
        long durationInMin = duration.toMinutes();
        long durationInMills = duration.toMillis();


        //System.out.println("CurrentTime: "+time1);
        //System.out.println("PassedTime : "+time2);
        //System.out.println("Difference : "+durationInMin +" minute");


        // return diff;
        // } catch (ParseException e) {
        //     e.printStackTrace();
        // }

        return durationInMills;
    }

    public static Long getTimeFromDateString(String dateStr){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Date date = sdf.parse(dateStr);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0L;
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getRestaurantOpeningTime(String openingTimeStr, String closingTimeStr){
        long diff1 = getTimeDifferenceInMinute(openingTimeStr);
        long diff2 = getTimeDifferenceInMinute(closingTimeStr);


        boolean willOpen = diff1 > 0; //opening time is greater than current time
        boolean willClose = diff2 > 0; //closing time is greater than current time



        String str = "";

        if(willOpen && willClose){
            str = "Will open";
            if(diff1> 60){
                long remainingHour = diff1 / 60;
                str += "in "+remainingHour + " hour";
            }else{
                str += "in "+diff1 + " minute";
            }


        }else if(!willOpen && willClose){// already opened and not closed yet
            if(Math.abs(diff1) > 720) {
                str = "will open tomorrow";
                return str;
            }
            str = "Open now";
            if(diff2 < 30)str = "Will close in "+diff2 +" minutes";
        }else{
            str = "Close now";
        }

        // else if(diff1 < 0 &&  willClose){
        //     str = "Will open tomorrow";
        // }

        // if(diff2 > diff1){// closing time is greater than opening time


        // }





        // if(isOpened && isClosed){ // i.e., CurrentTime > openingTime  && CurrentTime > closingTime ==>
        //     // Restaurant closed for today, will open on tomorrow
        //     str = "Will open tomorrow at "+ openingTimeStr;
        // }else if(isOpened && !isClosed){
        //     str = "Open now ";
        //     long diffMinutes = diff1 / (60 * 1000) % 60;
        //     str +=  "  Will close in "+diffMinutes +" Minutes";
        // }else if(!isOpened && isClosed){
        //     str = "Close now ";
        // }else if(!isOpened && !isClosed){
        //     str = "Will Open in ";
        //     long diffMinutes = diff1 / (60 * 1000) % 60;
        //     str += diffMinutes + " Minutes";
        // }
        return str;

    }





}
