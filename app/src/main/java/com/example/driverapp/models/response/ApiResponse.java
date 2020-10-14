package com.example.driverapp.models.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponse <T>{
    private boolean success;
    private String messsage;
    private T data;

    public ApiResponse() {
    }
}
