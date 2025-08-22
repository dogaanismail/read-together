package org.readtogether.user.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ReadingSpeed {
    SLOW,      // 0.75x
    NORMAL,    // 1x
    FAST,      // 1.25x
    FASTER;     // 1.5x

    @JsonCreator
    public static ReadingSpeed fromString(String value) {
        return value == null ? NORMAL : ReadingSpeed.valueOf(value.trim().toUpperCase());
    }
}
