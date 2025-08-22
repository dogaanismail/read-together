package org.readtogether.feed.factory;

import lombok.experimental.UtilityClass;
import org.readtogether.feed.entity.FeedCommentEntity;

import java.util.UUID;

@UtilityClass
public class FeedCommentEntityFactory {

    public static FeedCommentEntity createFeedComment(
            UUID feedItemId,
            UUID userId,
            String content,
            UUID parentCommentId) {

        return FeedCommentEntity.builder()
                .feedItemId(feedItemId)
                .userId(userId)
                .content(content)
                .parentCommentId(parentCommentId)
                .isDeleted(false)
                .build();
    }
}