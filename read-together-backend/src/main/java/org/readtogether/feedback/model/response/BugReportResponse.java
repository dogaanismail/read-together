package org.readtogether.feedback.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.readtogether.feedback.common.enums.BugReportSeverity;
import org.readtogether.feedback.common.enums.BugReportStatus;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BugReportResponse {

    private String id;

    private String title;

    private BugReportSeverity severity;

    private String stepsToReproduce;

    private String expectedVsActualBehavior;

    private String browserDeviceInfo;

    private BugReportStatus status;

    private String reporterId;

    private Instant createdAt;

}