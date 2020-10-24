package com.pureeats.driverapp.directionhelpers.distance;

import android.util.Log;


import com.pureeats.driverapp.models.Direction;
import com.pureeats.driverapp.models.Distance;
import com.pureeats.driverapp.models.Duration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arpan on 14/07/2020.
 */

public class DistanceDataParser {
    private final String TAG = this.getClass().getSimpleName();
    public Direction parse(JSONObject jObject) {

        JSONArray jRoutes;
        JSONArray jLegs;


        Direction direction = null;
        try {
            jRoutes = jObject.getJSONArray("routes");
            /** Traversing all routes */
            for (int i = 0; i < jRoutes.length(); i++) {
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList<>();

                /** Traversing all legs */
                for (int j = 0; j < jLegs.length(); j++) {
                    String distanceText = "";
                    long distanceVal = 0l;
                    distanceText = (String) (((JSONObject) jLegs.get(j)).getJSONObject("distance")).get("text");
                    distanceVal = (int)(((JSONObject) jLegs.get(j)).getJSONObject("distance")).get("value");
                    Distance distance = new Distance(distanceText, distanceVal);
                    Log.d(TAG, "DISTANCE: "+distance);

                    String durationText = "";
                    long durationVal = 0l;
                    durationText = (String) (((JSONObject) jLegs.get(j)).getJSONObject("duration")).get("text");
                    durationVal = (int) (((JSONObject) jLegs.get(j)).getJSONObject("duration")).get("value");
                    Duration duration  = new Duration(durationText, durationVal);
                    Log.d(TAG, "DURATION: "+duration);

                    direction = new Direction(distance, duration);

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return direction;
    }
}
