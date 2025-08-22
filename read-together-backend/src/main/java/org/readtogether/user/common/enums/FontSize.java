package org.readtogether.user.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum FontSize {
    SMALL,
    MEDIUM,
    LARGE,
    EXTRA_LARGE;

    @JsonCreator
    public static FontSize fromString(String value) {
        if (value == null) return MEDIUM;
        String v = value.trim().toUpperCase().replace('-', '_');
        return FontSize.valueOf(v);
    }
}
