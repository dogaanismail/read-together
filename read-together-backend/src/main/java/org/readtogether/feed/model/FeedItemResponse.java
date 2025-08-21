package org.readtogether.feed.model;

import lombok.Data;
import org.readtogether.feed.entity.FeedItemEntity;

import java.time.Instant;
import java.util.UUID;

@Data
public class FeedItemResponse {

    private UUID id;
    private UUID userId;
    private String username;
    private String userProfilePicture;
    private FeedItemEntity.FeedItemType itemType;
    private UUID referenceId;
    private String title;
    private String description;
    private String mediaUrl;
    private String thumbnailUrl;
    private boolean isPublic;
    private long viewCount;
    private long likeCount;
    private long commentCount;
    private String metadata;
    private Instant createdAt;

    // Computed fields
    private String timeAgo;
    private boolean isLiked; // Will be set based on current user
    private String formattedEngagement;
}
