package org.readtogether.feedback.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BugReportSeverity {

    CRITICAL("Critical - App unusable"),
    HIGH("High - Major feature broken"),
    MEDIUM("Medium - Minor feature issue"),
    LOW("Low - Cosmetic issue");

    private final String displayName;

}