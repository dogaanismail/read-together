package org.readtogether.user.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum VideoQuality {
    LOW,       // 480p
    MEDIUM,    // 720p
    HIGH,      // 1080p
    AUTO;

    @JsonCreator
    public static VideoQuality fromString(String value) {
        return value == null ? HIGH : VideoQuality.valueOf(value.trim().toUpperCase());
    }
}