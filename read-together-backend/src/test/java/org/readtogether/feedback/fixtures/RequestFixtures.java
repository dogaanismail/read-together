package org.readtogether.feedback.fixtures;

import lombok.experimental.UtilityClass;
import org.readtogether.feedback.common.enums.BugReportSeverity;
import org.readtogether.feedback.common.enums.FeatureRequestCategory;
import org.readtogether.feedback.common.enums.Priority;
import org.readtogether.feedback.model.request.BugReportSubmitRequest;
import org.readtogether.feedback.model.request.FeatureRequestSubmitRequest;

import static org.readtogether.feedback.common.enums.BugReportSeverity.HIGH;
import static org.readtogether.feedback.common.enums.FeatureRequestCategory.UI_UX_IMPROVEMENTS;
import static org.readtogether.feedback.common.enums.Priority.MEDIUM;

@UtilityClass
public class RequestFixtures {

    public static FeatureRequestSubmitRequest createDefaultFeatureRequestSubmitRequest() {

        return FeatureRequestSubmitRequest.builder()
                .title("Dark mode for better accessibility")
                .description("Add a dark theme option to reduce eye strain during long practice sessions.")
                .category(UI_UX_IMPROVEMENTS)
                .priority(MEDIUM)
                .build();
    }

    public static FeatureRequestSubmitRequest createFeatureRequestSubmitRequest(
            String title,
            String description,
            FeatureRequestCategory category,
            Priority priority) {

        return FeatureRequestSubmitRequest.builder()
                .title(title)
                .description(description)
                .category(category)
                .priority(priority)
                .build();
    }

    public static BugReportSubmitRequest createDefaultBugReportSubmitRequest() {

        return BugReportSubmitRequest.builder()
                .title("Login button not working")
                .severity(HIGH)
                .stepsToReproduce("1. Go to login page 2. Enter credentials 3. Click login button")
                .expectedVsActualBehavior("Expected: User should be logged in. Actual: Nothing happens.")
                .browserDeviceInfo("Chrome 120, Windows 11")
                .build();
    }

    public static BugReportSubmitRequest createBugReportSubmitRequest(
            String title,
            BugReportSeverity severity,
            String stepsToReproduce,
            String expectedVsActualBehavior) {

        return BugReportSubmitRequest.builder()
                .title(title)
                .severity(severity)
                .stepsToReproduce(stepsToReproduce)
                .expectedVsActualBehavior(expectedVsActualBehavior)
                .browserDeviceInfo("Chrome 120, Windows 11")
                .build();
    }

}