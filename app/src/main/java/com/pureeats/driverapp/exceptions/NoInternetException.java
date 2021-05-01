package com.pureeats.driverapp.exceptions;

import java.io.IOException;

public class NoInternetException extends IOException {
    public NoInternetException(String message){
        super(message);
    }
}
