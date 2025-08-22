package org.readtogether.feed.factory;

import lombok.experimental.UtilityClass;
import org.readtogether.common.utils.TimeUtils;
import org.readtogether.feed.entity.FeedCommentEntity;
import org.readtogether.feed.model.FeedCommentResponse;

@UtilityClass
public class FeedCommentResponseFactory {

    public static FeedCommentResponse createFromEntity(FeedCommentEntity comment) {
        FeedCommentResponse response = new FeedCommentResponse();
        response.setId(comment.getId());
        response.setFeedItemId(comment.getFeedItemId());
        response.setUserId(comment.getUserId());
        response.setContent(comment.getContent());
        response.setParentCommentId(comment.getParentCommentId());
        response.setCreatedAt(comment.getCreatedAt());
        response.setUpdatedAt(comment.getUpdatedAt());
        response.setDeleted(comment.isDeleted());

        // Set computed fields
        response.setTimeAgo(TimeUtils.formatTimeAgo(comment.getCreatedAt()));
        response.setReplyCount(0L); // This will be set by the service if needed
        response.setCanEdit(false); // This will be set by the service based on current user

        return response;
    }

    public static FeedCommentResponse createFromEntity(FeedCommentEntity comment, String username, String userProfilePicture, boolean canEdit) {
        FeedCommentResponse response = createFromEntity(comment);
        response.setUsername(username);
        response.setUserProfilePicture(userProfilePicture);
        response.setCanEdit(canEdit);
        return response;
    }
}