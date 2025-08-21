package org.readtogether.infrastructure.websocket.factory;

import lombok.experimental.UtilityClass;
import org.readtogether.session.entity.SessionEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@UtilityClass
public class WebSocketNotificationFactory {

    public static Map<String, Object> createSessionStatusNotification(SessionEntity session) {

        Map<String, Object> notification = new HashMap<>();
        notification.put("sessionId", session.getId().toString());
        notification.put("status", session.getProcessingStatus().toString());
        notification.put("title", session.getTitle());
        notification.put("timestamp", System.currentTimeMillis());

        if (session.getMediaUrl() != null) {
            notification.put("mediaUrl", session.getMediaUrl());
        }

        if (session.getProcessingError() != null) {
            notification.put("error", session.getProcessingError());
        }

        return notification;
    }

    public static Map<String, Object> createUploadProgressNotification(
            UUID sessionId,
            int progressPercentage) {

        return Map.of(
                "sessionId", sessionId.toString(),
                "type", "UPLOAD_PROGRESS",
                "progress", progressPercentage,
                "timestamp", System.currentTimeMillis()
        );
    }

    public static Map<String, Object> createSessionCompletedNotification(SessionEntity session) {

        return Map.of(
                "sessionId", session.getId().toString(),
                "type", "SESSION_COMPLETED",
                "title", session.getTitle(),
                "mediaUrl", session.getMediaUrl() != null ? session.getMediaUrl() : "",
                "message", "Your session '" + session.getTitle() + "' has been uploaded successfully!",
                "timestamp", System.currentTimeMillis()
        );
    }

    public static Map<String, Object> createSessionFailedNotification(
            SessionEntity session,
            String errorMessage) {

        return Map.of(
                "sessionId", session.getId().toString(),
                "type", "SESSION_FAILED",
                "title", session.getTitle(),
                "message", "Upload failed for '" + session.getTitle() + "': " + errorMessage,
                "error", errorMessage,
                "timestamp", System.currentTimeMillis()
        );
    }

    public static Map<String, Object> createNewFollowerNotification(
            UUID followerUserId,
            String followerUsername) {

        return Map.of(
                "type", "NEW_FOLLOWER",
                "followerUserId", followerUserId.toString(),
                "followerUsername", followerUsername,
                "message", followerUsername + " started following you!",
                "timestamp", System.currentTimeMillis()
        );
    }

    public static Map<String, Object> createSessionLikedNotification(
            SessionEntity session,
            UUID likerUserId) {

        return Map.of(
                "type", "SESSION_LIKED",
                "sessionId", session.getId().toString(),
                "sessionTitle", session.getTitle(),
                "likerUserId", likerUserId.toString(),
                "message", "Someone liked your session '" + session.getTitle() + "'!",
                "timestamp", System.currentTimeMillis()
        );
    }

    public static Map<String, Object> createLiveStreamStartedNotification(
            UUID streamerId,
            String streamerUsername,
            String streamTitle) {

        return Map.of(
                "type", "LIVE_STREAM_STARTED",
                "streamerId", streamerId.toString(),
                "streamerUsername", streamerUsername,
                "streamTitle", streamTitle,
                "message", streamerUsername + " started a live reading session: " + streamTitle,
                "timestamp", System.currentTimeMillis()
        );
    }

    public static Map<String, Object> createGeneralNotification(
            String type,
            String title,
            String message) {

        return Map.of(
                "type", type,
                "title", title,
                "message", message,
                "timestamp", System.currentTimeMillis()
        );
    }
}
