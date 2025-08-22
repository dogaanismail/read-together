package org.readtogether.feed.fixtures;

import lombok.experimental.UtilityClass;
import org.readtogether.feed.model.FeedItemResponse;
import org.readtogether.feed.model.FeedCommentResponse;
import org.readtogether.feed.common.enums.FeedItemType;

import java.time.Instant;
import java.util.UUID;

@UtilityClass
public class FeedResponseFixtures {

    public static FeedItemResponse createDefaultFeedItemResponse() {
        return createFeedItemResponse(
                FeedEntityFixtures.DEFAULT_FEED_ITEM_ID,
                FeedEntityFixtures.DEFAULT_USER_ID,
                "testuser",
                FeedItemType.SESSION,
                "Test Reading Session",
                "A practice reading session"
        );
    }

    public static FeedItemResponse createSessionFeedItemResponse() {
        return createFeedItemResponse(
                FeedEntityFixtures.SECONDARY_FEED_ITEM_ID,
                FeedEntityFixtures.SECONDARY_USER_ID,
                "anotheruser",
                FeedItemType.SESSION,
                "Another Reading Session",
                "Another practice reading session"
        );
    }

    public static FeedItemResponse createAchievementFeedItemResponse() {
        return createFeedItemResponse(
                UUID.randomUUID(),
                FeedEntityFixtures.DEFAULT_USER_ID,
                "testuser",
                FeedItemType.ACHIEVEMENT,
                "Achievement Unlocked: First Session",
                "Completed first reading session"
        );
    }

    public static FeedItemResponse createFeedItemResponse(
            UUID id,
            UUID userId,
            String username,
            FeedItemType itemType,
            String title,
            String description) {

        FeedItemResponse response = new FeedItemResponse();
        response.setId(id);
        response.setUserId(userId);
        response.setUsername(username);
        response.setUserProfilePicture("https://example.com/profile.jpg");
        response.setItemType(itemType);
        response.setReferenceId(FeedEntityFixtures.DEFAULT_REFERENCE_ID);
        response.setTitle(title);
        response.setDescription(description);
        response.setMediaUrl("https://example.com/media.mp4");
        response.setThumbnailUrl("https://example.com/thumbnail.jpg");
        response.setPublic(true);
        response.setViewCount(10L);
        response.setLikeCount(5L);
        response.setCommentCount(3L);
        response.setMetadata("{\"duration\": 300}");
        response.setCreatedAt(Instant.now());
        response.setTimeAgo("2 hours ago");
        response.setLiked(false);
        response.setFormattedEngagement("5 likes, 3 comments");
        return response;
    }

    public static FeedCommentResponse createDefaultFeedCommentResponse() {
        return createFeedCommentResponse(
                UUID.randomUUID(),
                FeedEntityFixtures.DEFAULT_FEED_ITEM_ID,
                FeedEntityFixtures.DEFAULT_USER_ID,
                "testuser",
                "This is a test comment"
        );
    }

    public static FeedCommentResponse createFeedCommentResponse(
            UUID id,
            UUID feedItemId,
            UUID userId,
            String username,
            String content) {

        FeedCommentResponse response = new FeedCommentResponse();
        response.setId(id);
        response.setFeedItemId(feedItemId);
        response.setUserId(userId);
        response.setUsername(username);
        response.setUserProfilePicture("https://example.com/profile.jpg");
        response.setContent(content);
        response.setParentCommentId(null);
        response.setDeleted(false);
        response.setCreatedAt(Instant.now());
        response.setTimeAgo("1 hour ago");
        return response;
    }

    public static FeedCommentResponse createReplyCommentResponse(UUID parentCommentId) {
        FeedCommentResponse response = createDefaultFeedCommentResponse();
        response.setId(UUID.randomUUID());
        response.setContent("This is a reply comment");
        response.setParentCommentId(parentCommentId);
        return response;
    }
}