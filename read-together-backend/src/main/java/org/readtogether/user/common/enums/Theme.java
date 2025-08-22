package org.readtogether.user.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Theme {
    LIGHT,
    DARK,
    SYSTEM;

    @JsonCreator
    public static Theme fromString(String value) {
        return value == null ? LIGHT : Theme.valueOf(value.trim().toUpperCase());
    }
}
