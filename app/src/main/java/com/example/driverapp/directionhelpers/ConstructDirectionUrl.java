package com.example.driverapp.directionhelpers;

import com.google.android.gms.maps.model.LatLng;

public class ConstructDirectionUrl {
    public static String getUrl(LatLng origin, LatLng dest, String directionMode, String API_KEY) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + API_KEY;
        return url;
    }

}
