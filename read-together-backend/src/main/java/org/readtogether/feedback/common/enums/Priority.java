package org.readtogether.feedback.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Priority {

    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High");

    private final String displayName;

}