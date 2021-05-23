package com.pureeats.driverapp.sharedprefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.common.util.CollectionUtils;
import com.google.gson.Gson;
import com.pureeats.driverapp.models.ChartData;
import com.pureeats.driverapp.models.response.Dashboard;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class EarningSession {
    private static final String TAG = "EarningSession";
    private final String EARNING_SESSION = "EARNING_SESSION";
    private final String KEY_EARNING_DETAILS = "key_earning_details";
    private Context mContext;

    private String[] DAYS = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};


    public EarningSession(Context context){
        mContext = context.getApplicationContext();
    }

    private SharedPreferences getEarningPreference(){
        return mContext.getSharedPreferences(EARNING_SESSION,0);
    }


    private void setStr(String key,  String value) throws Exception{
        SharedPreferences.Editor editor = getEarningPreference().edit();
        editor.putString(key, value);
        editor.apply();//editor.commit();
    }
    private String getStr(String key){
        return getEarningPreference().getString(key, null);
    }


    public Boolean clear(){
        try{
            SharedPreferences.Editor editor = getEarningPreference().edit();
            editor.clear();
            editor.apply();//editor.commit();
            return true;
        }catch (Exception e){
            Log.e(TAG, "Error in clearSharedPreference() method in UserSession  "+e);
        }
        return false;
    }

    public void setEarningDetails(Dashboard dashboard) {
        try{
            if(dashboard == null) return;
            String dashboardJson = new Gson().toJson(dashboard);
            setStr(KEY_EARNING_DETAILS, dashboardJson);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Dashboard getEarningDetails(){
        Dashboard dashboard = new Dashboard();
        try {
            String dashboardJson = getStr(KEY_EARNING_DETAILS);
            if(dashboardJson != null){
                Dashboard dashboardObj = new Gson().fromJson(dashboardJson, Dashboard.class);
                return dashboardObj;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return dashboard;
    }


    public Dashboard addCommission(float commission){
        Dashboard dashboard = getEarningDetails();
        try {
            if(dashboard != null){
                dashboard.setTodaysEarningAmount(dashboard.getTodaysEarningAmount() + commission);
                dashboard.setThisWeekEarningAmount(dashboard.getThisWeekEarningAmount() + commission);
                dashboard.setThisMonthEarningAmount(dashboard.getThisMonthEarningAmount() + commission);

                dashboard.setTodaysOrderCount(dashboard.getTodaysOrderCount() + 1);
                dashboard.setThisWeekOrderCount(dashboard.getThisWeekOrderCount() + 1);
                dashboard.setThisMonthOrderCount(dashboard.getThisMonthOrderCount() + 1);

                try {
                    // update the graph data
                    Date date=new Date();
                    Calendar c = Calendar.getInstance();
                    c.setTime(date);
                    int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

                    if(CollectionUtils.isEmpty(dashboard.getChartData())){
                        List<ChartData> chartDataList = new ArrayList<>();
                        for(int i=0; i< 6; i++){
                            if(i == dayOfWeek -1){
                                chartDataList.add(new ChartData(DAYS[i], commission));
                            }else{
                                chartDataList.add(new ChartData(DAYS[i], 0));
                            }
                        }
                        dashboard.setChartData(chartDataList);
                    }else{
                        List<ChartData> chartDataList = dashboard.getChartData();
                        chartDataList.get(dayOfWeek - 1).setY(commission);
                        dashboard.setChartData(chartDataList);
                    }


                }catch (Exception e){
                    e.printStackTrace();
                }

                setEarningDetails(dashboard);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return getEarningDetails();
    }



}


