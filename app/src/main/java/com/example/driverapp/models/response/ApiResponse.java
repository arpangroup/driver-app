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
    public ApiResponse(boolean success, String messsage) {
        this.success = success;
        this.messsage = messsage;
    }
    public ApiResponse(boolean success, String messsage, T data) {
        this.success = success;
        this.messsage = messsage;
        this.data = data;
    }
}
