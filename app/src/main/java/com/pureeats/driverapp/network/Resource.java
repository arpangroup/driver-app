package com.pureeats.driverapp.network;

import androidx.annotation.NonNull;

import com.pureeats.driverapp.commons.ErrorCode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/* S generic class that contains data and status about this data*/
public class Resource<T> {
    @NotNull public final Status status;
    @Nullable public final T data;
    @Nullable public final String message;
    @Nullable public final String code;
    public boolean isNetworkError;

    //private boolean success;
    //private String code;


    public Resource(@NotNull Status status, @Nullable T data, @Nullable String message, String code, boolean isNetworkError) {
        this.status = status;
        this.data = data;
        this.message = message;
        this.code = code;
        this.isNetworkError = isNetworkError;
    }

    public static <T>Resource<T> success(@NonNull T data){
        return new Resource<>(Status.SUCCESS, data, null, null, false);
    }

    public static <T> Resource<T> error(String errorCode, String msg){
        return new Resource<>(Status.ERROR, null, msg, errorCode, false);
    }
    public static <T> Resource<T> networkError(){
       return new Resource<>(Status.ERROR, null, ErrorCode.NO_INTERNET.getMessage(), ErrorCode.NO_INTERNET.getCode(), true);
    }
    public static <T> Resource<T> authError(){
        return new Resource<>(Status.ERROR, null, ErrorCode.INVALID_AUTH_TOKEN.getMessage(), ErrorCode.INVALID_AUTH_TOKEN.getCode(), false);
    }

    public static <T> Resource<T> emptyRecord(){
        return new Resource<>(Status.ERROR, null, ErrorCode.NO_RECORD.getMessage(), ErrorCode.NO_RECORD.getCode(), false);
    }

    public static <T> Resource<T> loading(){
        return new Resource<>(Status.LOADING, null, null, null, false);
    }

    public enum Status {SUCCESS, ERROR, LOADING}

}
