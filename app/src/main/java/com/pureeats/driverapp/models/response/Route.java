package com.pureeats.driverapp.models.response;

import java.util.Map;

import lombok.Data;

@Data
public class Route {
    private Map<String, String> source;
    private Map<String, String> destination;
    private String distance;
    private String distance_text;
    private String actual_time;
    private String actual_time_text;
    private String required_time;
    private String required_time_text;
}
