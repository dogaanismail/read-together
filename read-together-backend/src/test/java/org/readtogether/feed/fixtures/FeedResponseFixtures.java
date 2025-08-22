package org.readtogether.feed.fixtures;

import lombok.experimental.UtilityClass;
import org.readtogether.feed.model.FeedCommentResponse;

import java.time.Instant;
import java.util.UUID;

@UtilityClass
public class FeedResponseFixtures {

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

}