package com.pureeats.driverapp.models;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.google.gson.annotations.SerializedName;
import com.pureeats.driverapp.models.response.Meta;

import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Route {
    private RouteLocation source;
    private RouteLocation destination;
    private String distance_text;
    private String actual_time_text;
    private String required_time_text;

}
