package org.readtogether.feed.model;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class FeedCommentResponse {

    private UUID id;
    private UUID feedItemId;
    private UUID userId;
    private String username;
    private String userProfilePicture;
    private String content;
    private UUID parentCommentId;
    private Instant createdAt;
    private Instant updatedAt;
    private boolean isDeleted;

    // Computed fields
    private String timeAgo;
    private long replyCount;
    private boolean canEdit; // Will be set based on current user
}