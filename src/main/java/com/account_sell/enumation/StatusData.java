package com.account_sell.enumation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum StatusData {
    ACTIVE,
    INACTIVE;

    @JsonCreator
    public static StatusData fromString(String value) {
        return StatusData.valueOf(value.toUpperCase());
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}
