package org.readtogether.feedback.fixtures;

import lombok.experimental.UtilityClass;
import org.readtogether.feedback.common.enums.BugReportSeverity;
import org.readtogether.feedback.common.enums.BugReportStatus;
import org.readtogether.feedback.entity.BugReportEntity;

import java.util.UUID;

@UtilityClass
public class BugReportEntityFixtures {

    public static final UUID DEFAULT_BUG_REPORT_ID = UUID.fromString("c3d4e5f6-a7b8-9012-cdef-012345678902");
    public static final UUID DEFAULT_REPORTER_ID = UUID.fromString("d4e5f6a7-b8c9-0123-defa-123456789003");

    public static BugReportEntity createDefaultBugReportEntity() {

        return BugReportEntity.builder()
                .id(DEFAULT_BUG_REPORT_ID)
                .title("Login button not working")
                .severity(BugReportSeverity.HIGH)
                .stepsToReproduce("1. Go to login page 2. Enter credentials 3. Click login button")
                .expectedVsActualBehavior("Expected: User should be logged in. Actual: Nothing happens.")
                .browserDeviceInfo("Chrome 120, Windows 11")
                .status(BugReportStatus.SUBMITTED)
                .reporterId(DEFAULT_REPORTER_ID)
                .build();
    }

    public static BugReportEntity createBugReportEntity(
            String title,
            BugReportSeverity severity,
            String stepsToReproduce,
            String expectedVsActualBehavior) {

        return BugReportEntity.builder()
                .title(title)
                .severity(severity)
                .stepsToReproduce(stepsToReproduce)
                .expectedVsActualBehavior(expectedVsActualBehavior)
                .browserDeviceInfo("Chrome 120, Windows 11")
                .status(BugReportStatus.SUBMITTED)
                .reporterId(DEFAULT_REPORTER_ID)
                .build();
    }

    public static BugReportEntity createBugReportEntity(
            String title,
            BugReportSeverity severity,
            String stepsToReproduce,
            String expectedVsActualBehavior,
            String browserDeviceInfo,
            BugReportStatus status) {

        return BugReportEntity.builder()
                .title(title)
                .severity(severity)
                .stepsToReproduce(stepsToReproduce)
                .expectedVsActualBehavior(expectedVsActualBehavior)
                .browserDeviceInfo(browserDeviceInfo)
                .status(status)
                .reporterId(DEFAULT_REPORTER_ID)
                .build();
    }

}