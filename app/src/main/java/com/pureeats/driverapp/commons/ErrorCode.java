package com.pureeats.driverapp.commons;

import java.util.Arrays;

public enum  ErrorCode {
    BAD_REQUEST("C0001", "Bad Request"),
    NO_INTERNET("C0002", "Internet not found"),
    DATA_CONVERTION_FAILURE("C0003", "Data Conversion Failed"),
    BAD_RESPONSE("C0004", "Bad Response"),
    NO_RECORD("C0005", "No records found"),
    MAX_ORDER_REACHED("", "Maximum Order Reached"),



    //Authentication
    INVALID_REQUEST_BODY("E1001", "E1001 : Invalid Request Body"),
    INCORRECT_DATA_TYPE("E1002", "E1002 : INCORRECT_DATA_TYPE"),
    INVALID_AUTH_TOKEN("E1003", "INVALID_AUTH_TOKEN"),
    PHONE_NOT_EXIST("E1004", "Phone not exist"), //IMPORTANT==>use To open Registration Activity
    DUPLICATE_MOBILE_NUMBER("E1005", "Mobile number already registered"),
    DUPLICATE_EMAIL_ID("E1006", "email already registered"),
    SUSPENDED_USER("E1007", "User suspended"),
    ACCOUNT_BLOCKED("E1008", "User blocked"),
    MAXIMUM_ATTEMPT_REACH("E1009", "E1009 : Maximum attempt reach"),
    INVALID_PUSH_TOKEN("E1010", "E1010 :Invalid Push Token"),
    REGISTRATION_NOT_POSSIBLE("E1011", "E1011 : Registration Not Possible"),
    TOKEN_GENERATION_FAILED("E1012", "E1012 : Failef to generate auth token");





    private final String code;
    private final String message;
    //private static Map<String, ErrorCode> errorCodeMap = new HashMap<String, ErrorCode>();

    private ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
        //this.errorCodeMap.put(code, this);
    }

    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return code + ": " + message;
    }


    public static ErrorCode getErrorCode(String codeStr){
        return Arrays.stream(ErrorCode.values()).filter(errorCode -> errorCode.code.equals(codeStr)).findFirst().orElse(BAD_REQUEST);
    }


}
