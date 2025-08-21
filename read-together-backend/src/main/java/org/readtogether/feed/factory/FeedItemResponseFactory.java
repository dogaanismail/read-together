package org.readtogether.feed.factory;

import lombok.experimental.UtilityClass;
import org.readtogether.common.utils.EngagementUtils;
import org.readtogether.common.utils.TimeUtils;
import org.readtogether.feed.entity.FeedItemEntity;
import org.readtogether.feed.model.FeedItemResponse;

@UtilityClass
public class FeedItemResponseFactory {

    public static FeedItemResponse createFromEntity(FeedItemEntity feedItem) {

        FeedItemResponse response = new FeedItemResponse();
        response.setId(feedItem.getId());
        response.setUserId(feedItem.getUserId());
        response.setItemType(feedItem.getItemType());
        response.setReferenceId(feedItem.getReferenceId());
        response.setTitle(feedItem.getTitle());
        response.setDescription(feedItem.getDescription());
        response.setMediaUrl(feedItem.getMediaUrl());
        response.setThumbnailUrl(feedItem.getThumbnailUrl());
        response.setPublic(feedItem.isPublic());
        response.setViewCount(feedItem.getViewCount());
        response.setLikeCount(feedItem.getLikeCount());
        response.setCommentCount(feedItem.getCommentCount());
        response.setMetadata(feedItem.getMetadata());
        response.setCreatedAt(feedItem.getCreatedAt());

        response.setTimeAgo(TimeUtils.formatTimeAgo(feedItem.getCreatedAt()));
        response.setFormattedEngagement(EngagementUtils.formatEngagement(
            feedItem.getViewCount(), feedItem.getLikeCount(), feedItem.getCommentCount()));

        return response;
    }
}
