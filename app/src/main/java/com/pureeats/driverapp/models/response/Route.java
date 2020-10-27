package com.pureeats.driverapp.models.response;

import java.util.Map;

import lombok.Data;

@Data
public class Route {
    private Map<String, String> source;
    private Map<String, String> destination;
    private long distance;
    private String distance_text;
    private long actual_time;
    private String actual_time_text;
    private long required_time;
    private String required_time_text;
}
