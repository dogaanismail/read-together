package org.readtogether.feedback.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.readtogether.feedback.common.enums.FeatureRequestCategory;
import org.readtogether.feedback.common.enums.FeatureRequestStatus;
import org.readtogether.feedback.common.enums.Priority;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeatureRequest {

    private String id;
    private String title;
    private String description;
    private FeatureRequestCategory category;
    private Priority priority;
    private FeatureRequestStatus status;
    private int votes;
    private String authorId;
    private Instant createdAt;
    private Instant updatedAt;

}