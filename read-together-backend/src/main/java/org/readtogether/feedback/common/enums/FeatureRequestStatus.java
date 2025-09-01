package org.readtogether.feedback.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FeatureRequestStatus {

    SUBMITTED("Submitted"),
    UNDER_REVIEW("Under Review"),
    IN_PROGRESS("In Progress"),
    PLANNED("Planned"),
    IMPLEMENTED("Implemented"),
    REJECTED("Rejected");

    private final String displayName;

}