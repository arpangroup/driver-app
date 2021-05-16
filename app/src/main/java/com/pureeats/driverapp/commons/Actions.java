package com.pureeats.driverapp.commons;

public enum Actions {
    START("START"),
    STOP("STOP"),

    NEW_ORDER_ARRIVED("NEW_ORDER_ARRIVED"),
    ORDER_CANCELLED("ORDER_CANCELLED"),
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

    public static Actions getStatus(String actionName){
        for (Actions action : values()){
            if(action.actionName == actionName){
                return action;
            }
        }
        return null;
    }


    }
