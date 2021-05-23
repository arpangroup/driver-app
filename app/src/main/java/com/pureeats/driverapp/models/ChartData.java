package com.pureeats.driverapp.models;

import lombok.Data;

@Data
public class ChartData {
    private String x;
    private float y;

    public ChartData(String x, float y) {
        this.x = x;
        this.y = y;
    }
}
