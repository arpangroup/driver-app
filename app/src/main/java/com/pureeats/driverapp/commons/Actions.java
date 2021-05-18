package com.pureeats.driverapp.commons;

public enum Actions {
    START("START"),
    STOP("STOP"),

    ORDER_ARRIVED("ORDER_ARRIVED"),
    DELIVERY_ASSIGNED("DELIVERY_ASSIGNED"),
    DELIVERY_RE_ASSIGNED("DELIVERY_RE_ASSIGNED"),
    ORDER_CANCELLED("ORDER_CANCELLED"),
    ORDER_TRANSFERRED("ORDER_TRANSFERRED"),
    DISMISS_ORDER_NOTIFICATION("DISMISS_ORDER_NOTIFICATION"),
    ORDER_ACCEPTED("ORDER_ACCEPTED"),

    REACH_DIRECTION_FRAGMENT("REACH_DIRECTION_FRAGMENT"),
    PICK_ORDER_FRAGMENT("PICK_ORDER_FRAGMENT"),
    DELIVER_ORDER_FRAGMENT("DELIVER_ORDER_FRAGMENT"),
    ACCEPT_ORDER_FRAGMENT("ACCEPT_ORDER_FRAGMENT");



    private final String actionName;
    Actions(String actionName) {
        this.actionName = actionName;
    }

    public String actionName(){
        return actionName;
    }

    public static Actions getAction(String actionName){
        for (Actions action : values()){
            if(action.actionName == actionName){
                return action;
            }
        }
        return null;
    }


    }
