package org.readtogether.notification.factory;

import lombok.experimental.UtilityClass;
import org.readtogether.notification.entity.NotificationEntity;
import org.readtogether.session.entity.SessionEntity;

import java.util.UUID;

@UtilityClass
public class NotificationEntityFactory {

    public static NotificationEntity createSessionUploadStartedNotification(
            UUID userId,
            SessionEntity session,
            String metadata) {

        String message = String.format("Your session '%s' upload has started. We'll notify you when it's ready!",
                session.getTitle());

        return NotificationEntity.builder()
                .userId(userId)
                .sessionId(session.getId())
                .type(NotificationEntity.NotificationType.SESSION_UPLOAD_STARTED)
                .title("Upload Started")
                .message(message)
                .metadata(metadata)
                .build();
    }

    public static NotificationEntity createSessionUploadProgressNotification(
            UUID userId,
            UUID sessionId,
            int progress,
            String metadata) {

        String message = String.format("Upload progress: %d%% complete", progress);

        return NotificationEntity.builder()
                .userId(userId)
                .sessionId(sessionId)
                .type(NotificationEntity.NotificationType.SESSION_UPLOAD_PROGRESS)
                .title("Upload Progress")
                .message(message)
                .metadata(metadata)
                .build();
    }

    public static NotificationEntity createSessionUploadCompletedNotification(
            UUID userId,
            SessionEntity session,
            String metadata) {

        String message = String.format("Great! Your session '%s' has been uploaded successfully and is now available.",
                session.getTitle());

        return NotificationEntity.builder()
                .userId(userId)
                .sessionId(session.getId())
                .type(NotificationEntity.NotificationType.SESSION_UPLOAD_COMPLETED)
                .title("Upload Complete")
                .message(message)
                .metadata(metadata)
                .build();
    }

    public static NotificationEntity createSessionUploadFailedNotification(
            UUID userId,
            SessionEntity session,
            String errorMessage,
            String metadata) {

        String message = String.format("Upload failed for '%s'. %s Please try again.",
                session.getTitle(), errorMessage);

        return NotificationEntity.builder()
                .userId(userId)
                .sessionId(session.getId())
                .type(NotificationEntity.NotificationType.SESSION_UPLOAD_FAILED)
                .title("Upload Failed")
                .message(message)
                .metadata(metadata)
                .build();
    }

    public static NotificationEntity createSessionProcessingStartedNotification(
            UUID userId,
            SessionEntity session,
            String metadata) {

        String message = String.format("Processing your session '%s'. This may take a few minutes depending on file size.",
                session.getTitle());

        return NotificationEntity.builder()
                .userId(userId)
                .sessionId(session.getId())
                .type(NotificationEntity.NotificationType.SESSION_PROCESSING_STARTED)
                .title("Processing Started")
                .message(message)
                .metadata(metadata)
                .build();
    }

    public static NotificationEntity createSessionLikedNotification(
            UUID sessionOwnerId,
            SessionEntity session,
            String metadata) {

        String message = String.format("Someone liked your session '%s'!", session.getTitle());

        return NotificationEntity.builder()
                .userId(sessionOwnerId)
                .sessionId(session.getId())
                .type(NotificationEntity.NotificationType.GENERAL_INFO)
                .title("Session Liked")
                .message(message)
                .metadata(metadata)
                .build();
    }

    public static NotificationEntity createSessionCommentedNotification(
            UUID sessionOwnerId,
            SessionEntity session,
            String metadata) {

        String message = String.format("Someone commented on your session '%s'!", session.getTitle());

        return NotificationEntity.builder()
                .userId(sessionOwnerId)
                .sessionId(session.getId())
                .type(NotificationEntity.NotificationType.GENERAL_INFO)
                .title("New Comment")
                .message(message)
                .metadata(metadata)
                .build();
    }

    public static NotificationEntity createNewFollowerNotification(
            UUID followedUserId,
            String followerUsername,
            String metadata) {

        String message = String.format("%s started following you!", followerUsername);

        return NotificationEntity.builder()
                .userId(followedUserId)
                .type(NotificationEntity.NotificationType.GENERAL_INFO)
                .title("New Follower")
                .message(message)
                .metadata(metadata)
                .build();
    }

    public static NotificationEntity createLiveStreamStartedNotification(
            UUID userId,
            String streamerUsername,
            String streamTitle,
            String metadata) {

        String message = String.format("%s started a live reading session: %s", streamerUsername, streamTitle);

        return NotificationEntity.builder()
                .userId(userId)
                .type(NotificationEntity.NotificationType.GENERAL_INFO)
                .title("Live Stream Started")
                .message(message)
                .metadata(metadata)
                .build();
    }
}
