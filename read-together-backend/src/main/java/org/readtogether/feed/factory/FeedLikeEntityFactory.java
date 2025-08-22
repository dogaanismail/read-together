package org.readtogether.feed.factory;

import lombok.experimental.UtilityClass;
import org.readtogether.feed.entity.FeedLikeEntity;

import java.util.UUID;

@UtilityClass
public class FeedLikeEntityFactory {

    public static FeedLikeEntity createFeedLike(UUID feedItemId, UUID userId) {
        return FeedLikeEntity.builder()
                .feedItemId(feedItemId)
                .userId(userId)
                .build();
    }
}