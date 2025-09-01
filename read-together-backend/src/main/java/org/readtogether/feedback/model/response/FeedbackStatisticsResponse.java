package org.readtogether.feedback.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackStatisticsResponse {

    private int totalFeatureRequests;

    private int featuresImplemented;

    private int totalBugReports;

    private int bugsFixed;

    private int criticalBugs;

    private int bugsInProgress;

}