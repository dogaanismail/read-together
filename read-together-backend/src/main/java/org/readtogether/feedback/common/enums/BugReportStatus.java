package org.readtogether.feedback.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BugReportStatus {

    SUBMITTED("Submitted"),
    INVESTIGATING("Investigating"),
    IN_PROGRESS("In Progress"),
    FIXED("Fixed"),
    CLOSED("Closed"),
    WONT_FIX("Won't Fix");

    private final String displayName;

}