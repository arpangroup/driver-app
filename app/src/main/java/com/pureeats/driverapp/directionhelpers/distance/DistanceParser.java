package com.pureeats.driverapp.directionhelpers.distance;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.pureeats.driverapp.directionhelpers.TaskLoadedCallback;
import com.pureeats.driverapp.models.Direction;

import org.json.JSONObject;

/**
 * Created by Arpan on 14/07/2020.
 */

public class DistanceParser extends AsyncTask<String, Integer, Direction> {
    private final String TAG = this.getClass().getSimpleName();
    TaskLoadedCallback taskCallback;
    String directionMode = "driving";
    String flag = null;

    public DistanceParser(Context mContext, String directionMode) {
        this.taskCallback = (TaskLoadedCallback) mContext;
        this.directionMode = directionMode;
    }

    public DistanceParser(Context mContext, String directionMode, String flag) {
        this.taskCallback = (TaskLoadedCallback) mContext;
        this.directionMode = directionMode;
        this.flag = flag;
    }

    // Parsing the data in non-ui thread
    @Override
    protected Direction doInBackground(String... jsonData) {

        JSONObject jObject;
        Direction direction = null;

        try {
            jObject = new JSONObject(jsonData[0]);
            Log.d(TAG, "JSON_DATA: "+jsonData[0].toString());
            DistanceDataParser parser = new DistanceDataParser();
            Log.d(TAG, "PARSER: "+parser.toString());

            // Starts parsing data
            direction = parser.parse(jObject);
            Log.d(TAG, "Executing routes");
            Log.d(TAG, "DIRECTION: "+direction);

        } catch (Exception e) {
            Log.d(TAG, e.toString());
            e.printStackTrace();
        }
        return direction;
    }

    // Executes in UI thread, after the parsing process
    @Override
    protected void onPostExecute(Direction result) {
        if (result != null) {
            //mMap.addPolyline(lineOptions);
            result.setFlag(flag);
            taskCallback.onTaskDone(result);

        } else {
            Log.d(TAG, "no direction");
        }
    }
}