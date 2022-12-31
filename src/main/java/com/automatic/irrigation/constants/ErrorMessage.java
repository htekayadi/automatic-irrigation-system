package com.automatic.irrigation.constants;

public enum ErrorMessage {

    NOT_FOUND("not found"),
    UPDATE_FAILED("update failed"),
    CREATE_FAILED("creation failed");

    private final String value;

    ErrorMessage(String value){
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
