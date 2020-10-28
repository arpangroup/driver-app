package com.pureeats.driverapp.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RouteLocation {
    private String lat;
    private String lng;
    private String address;
}
