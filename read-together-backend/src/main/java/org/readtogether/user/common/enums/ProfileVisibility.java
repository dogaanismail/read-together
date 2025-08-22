package org.readtogether.user.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ProfileVisibility {
    PUBLIC,
    FOLLOWERS,
    PRIVATE;

    @JsonCreator
    public static ProfileVisibility fromString(String value) {
        return value == null ? PUBLIC : ProfileVisibility.valueOf(value.trim().toUpperCase());
    }
}
