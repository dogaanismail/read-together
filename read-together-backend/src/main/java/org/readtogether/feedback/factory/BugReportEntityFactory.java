package org.readtogether.feedback.factory;

import lombok.experimental.UtilityClass;
import org.readtogether.feedback.entity.BugReportEntity;
import org.readtogether.feedback.model.request.BugReportSubmitRequest;

import java.util.UUID;

@UtilityClass
public class BugReportEntityFactory {

    public static BugReportEntity createBugReportEntity(
            BugReportSubmitRequest request,
            UUID reporterId) {

        return BugReportEntity.builder()
                .title(request.getTitle())
                .severity(request.getSeverity())
                .stepsToReproduce(request.getStepsToReproduce())
                .expectedVsActualBehavior(request.getExpectedVsActualBehavior())
                .browserDeviceInfo(request.getBrowserDeviceInfo())
                .reporterId(reporterId)
                .build();
    }

}