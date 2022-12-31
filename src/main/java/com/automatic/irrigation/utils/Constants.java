package com.automatic.irrigation.utils;

public enum Constants {

    NOT_FOUND("not found"),
    UPDATE_FAILED("update failed"),
    CREATE_FAILED("creation failed");

    private final String value;

    Constants(String value){
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
