package com.automatic.irrigation.constants;

public enum Status {
    CONFIGURED("CONFIGURED"),
    DONE("DONE");

    private String value;

    Status(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }
}
