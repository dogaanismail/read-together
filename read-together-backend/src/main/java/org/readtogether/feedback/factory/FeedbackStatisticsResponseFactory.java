package org.readtogether.feedback.factory;

import lombok.experimental.UtilityClass;
import org.readtogether.feedback.model.response.FeedbackStatisticsResponse;

@UtilityClass
public class FeedbackStatisticsResponseFactory {

    public static FeedbackStatisticsResponse createFeedbackStatisticsResponse(
            int totalFeatureRequests,
            int featuresImplemented,
            int totalBugReports,
            int bugsFixed,
            int criticalBugs,
            int bugsInProgress) {

        return FeedbackStatisticsResponse.builder()
                .totalFeatureRequests(totalFeatureRequests)
                .featuresImplemented(featuresImplemented)
                .totalBugReports(totalBugReports)
                .bugsFixed(bugsFixed)
                .criticalBugs(criticalBugs)
                .bugsInProgress(bugsInProgress)
                .build();
    }

}