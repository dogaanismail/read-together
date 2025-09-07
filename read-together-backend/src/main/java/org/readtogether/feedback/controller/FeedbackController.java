package org.readtogether.feedback.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.readtogether.common.model.response.CustomResponse;
import org.readtogether.feedback.common.enums.FeatureRequestCategory;
import org.readtogether.feedback.common.enums.FeatureRequestStatus;
import org.readtogether.feedback.model.request.BugReportSubmitRequest;
import org.readtogether.feedback.model.request.FeatureRequestSubmitRequest;
import org.readtogether.feedback.model.response.BugReportResponse;
import org.readtogether.feedback.model.response.FeatureRequestResponse;
import org.readtogether.feedback.model.response.FeedbackStatisticsResponse;
import org.readtogether.feedback.service.FeedbackService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/feedback")
@RequiredArgsConstructor
@Slf4j
@Validated
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping("/feature-requests")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public CustomResponse<FeatureRequestResponse> submitFeatureRequest(
            @RequestBody @Valid FeatureRequestSubmitRequest request) {

        log.info("Received request to submit feature request with title: {}", request.getTitle());
        FeatureRequestResponse response = feedbackService.submitFeatureRequest(request);
        log.info("Feature request submitted successfully with ID: {}", response.getId());

        return CustomResponse.createdOf(response);
    }

    @PostMapping("/bug-reports")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public CustomResponse<BugReportResponse> submitBugReport(
            @RequestBody @Valid BugReportSubmitRequest request) {

        log.info("Received request to submit bug report with title: {}", request.getTitle());
        BugReportResponse response = feedbackService.submitBugReport(request);
        log.info("Bug report submitted successfully with ID: {}", response.getId());

        return CustomResponse.createdOf(response);
    }

    @GetMapping("/feature-requests")
    public CustomResponse<Page<FeatureRequestResponse>> getFeatureRequests(
            @RequestParam(required = false) FeatureRequestCategory category,
            @RequestParam(required = false) FeatureRequestStatus status,
            @PageableDefault(size = 20, sort = "votes") Pageable pageable) {

        log.info("Received request to get feature requests with category: {}, status: {}", category, status);
        Page<FeatureRequestResponse> responses = feedbackService.getFeatureRequests(category, status, pageable);
        log.info("Retrieved {} feature requests", responses.getTotalElements());

        return CustomResponse.successOf(responses);
    }

    @GetMapping("/bug-reports")
    public CustomResponse<Page<BugReportResponse>> getBugReports(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {

        log.info("Received request to get bug reports");
        Page<BugReportResponse> responses = feedbackService.getBugReports(pageable);
        log.info("Retrieved {} bug reports", responses.getTotalElements());

        return CustomResponse.successOf(responses);
    }

    @PutMapping("/feature-requests/{id}/vote")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public CustomResponse<Void> voteForFeatureRequest(@PathVariable UUID id) {

        log.info("Received request to vote for feature request ID: {}", id);
        feedbackService.voteForFeatureRequest(id);
        log.info("Vote recorded for feature request ID: {}", id);

        return CustomResponse.SUCCESS;
    }

    @GetMapping("/statistics")
    public CustomResponse<FeedbackStatisticsResponse> getFeedbackStatistics() {

        log.info("Received request to get feedback statistics");
        FeedbackStatisticsResponse response = feedbackService.getFeedbackStatistics();
        log.info("Retrieved feedback statistics successfully");

        return CustomResponse.successOf(response);
    }

}