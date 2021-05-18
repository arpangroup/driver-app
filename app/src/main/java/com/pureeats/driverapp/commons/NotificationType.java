package com.pureeats.driverapp.commons;

public enum NotificationType {
    DEFAULT("DEFAULT"),
    ORDER_ARRIVED("ORDER_ARRIVED"),
    DELIVERY_ASSIGNED("DELIVERY_ASSIGNED"),
    DELIVERY_RE_ASSIGNED("DELIVERY_RE_ASSIGNED"),
    ORDER_CANCELLED("ORDER_CANCELLED"),
    ORDER_TRANSFERRED("ORDER_TRANSFERRED");


    private final String name;
    NotificationType(String name) {
        this.name = name;
    }

    public String getExpandedForm(){
        return name;
    }

    public static NotificationType getType(String notificationType){
        for (NotificationType type : values()){
            if(type.name.equalsIgnoreCase(notificationType)){
                return type;
            }
        }
        return null;
    }


    }
