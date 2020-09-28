package com.example.driverapp.models;

import lombok.Data;
import lombok.Getter;

@Data
public class Location {
    private String address;
    private String  house;
    private String lat;
    private String lng;
}
