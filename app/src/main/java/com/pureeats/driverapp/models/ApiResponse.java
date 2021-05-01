package com.pureeats.driverapp.models;

import com.pureeats.driverapp.commons.ErrorCode;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private String code;

    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }


    public ApiResponse(ErrorCode errorCode) {
        this.success = false;
        this.message = errorCode.getMessage();
        this.code = errorCode.getCode();
    }


    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
}
