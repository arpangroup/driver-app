package com.pureeats.driverapp.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class ApiResponse {
    private boolean success;
    private String message;
}
