package org.readtogether.feedback.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.readtogether.feedback.common.enums.BugReportSeverity;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BugReportSubmitRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Severity is required")
    private BugReportSeverity severity;

    @NotBlank(message = "Steps to reproduce is required")
    private String stepsToReproduce;

    @NotBlank(message = "Expected vs actual behavior is required")
    private String expectedVsActualBehavior;

    private String browserDeviceInfo;

}