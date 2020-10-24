package com.pureeats.driverapp.directionhelpers;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.pureeats.driverapp.directionhelpers.direction.PointsParser;
import com.pureeats.driverapp.directionhelpers.distance.DistanceParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Arpan on 14/07/2020.
 */

public class FetchURL extends AsyncTask<String, Void, String> {
    private final String TAG = this.getClass().getSimpleName();
    public static final String POINT_PARSER = "PointParser";
    public static final String DISTANCE_PARSER = "DistanceParser";

    private Context mContext;
    private String directionMode = "driving";
    private String parser = POINT_PARSER;// default is PointParser
    private String flag = null;

    public FetchURL(Context mContext, String parser) {
        this.mContext = mContext;
        this.parser =  parser;
    }

    public FetchURL(Context mContext, String parser, String flag) {
        this.mContext = mContext;
        this.parser =  parser;
        this.flag = flag;
    }

    @Override
    protected String doInBackground(String... strings) {
        // For storing data from web service
        String data = "";
        directionMode = strings[1];
        try {
            // Fetching the data from web service
            data = downloadUrl(strings[0]);
            Log.d(TAG, "Background task data " + data.toString());
        } catch (Exception e) {
            Log.d(TAG,"Background Task"+ e.toString());
        }
        return data;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(parser.equalsIgnoreCase(DISTANCE_PARSER)){
            DistanceParser parserTask = new DistanceParser(mContext, directionMode, flag);
            // Invokes the thread for parsing the JSON data
            parserTask.execute(s);
        }else{
            PointsParser parserTask = new PointsParser(mContext, directionMode);
            // Invokes the thread for parsing the JSON data
            parserTask.execute(s);
        }

    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            // Connecting to url
            urlConnection.connect();
            // Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            Log.d(TAG, "Downloaded URL: " + data.toString());
            br.close();
        } catch (Exception e) {
            Log.d(TAG, "Exception downloading URL: " + e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
}