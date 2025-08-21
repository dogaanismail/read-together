package org.readtogether.notification.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.readtogether.session.entity.SessionEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@UtilityClass
public class NotificationMetadataUtils {

    public static String createSessionMetadata(
            ObjectMapper objectMapper,
            SessionEntity session) {

        try {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("sessionId", session.getId().toString());
            metadata.put("title", session.getTitle());
            metadata.put("mediaType", session.getMediaType().toString());
            metadata.put("isPublic", session.isPublic());
            metadata.put("status", session.getProcessingStatus().toString());

            if (session.getMediaUrl() != null) {
                metadata.put("mediaUrl", session.getMediaUrl());
            }

            return objectMapper.writeValueAsString(metadata);
        } catch (Exception e) {
            log.warn("Failed to serialize session metadata for session {}", session.getId(), e);
            return "{}";
        }
    }

    public static String createProgressMetadata(
            ObjectMapper objectMapper,
            int progress) {

        try {
            Map<String, Object> metadata = Map.of(
                    "progress", progress,
                    "timestamp", System.currentTimeMillis()
            );
            return objectMapper.writeValueAsString(metadata);
        } catch (Exception e) {
            log.warn("Failed to serialize progress metadata", e);
            return "{}";
        }
    }

    public static String createErrorMetadata(
            ObjectMapper objectMapper,
            SessionEntity session,
            String error) {

        try {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("sessionId", session.getId().toString());
            metadata.put("error", error);
            metadata.put("retryable", isRetryableError(error));
            metadata.put("timestamp", System.currentTimeMillis());

            return objectMapper.writeValueAsString(metadata);
        } catch (Exception e) {
            log.warn("Failed to serialize error metadata for session {}", session.getId(), e);
            return "{}";
        }
    }

    public static String createLikeMetadata(
            ObjectMapper objectMapper,
            SessionEntity session,
            UUID likerUserId) {

        try {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("sessionId", session.getId().toString());
            metadata.put("sessionTitle", session.getTitle());
            metadata.put("likerUserId", likerUserId.toString());
            metadata.put("timestamp", System.currentTimeMillis());

            return objectMapper.writeValueAsString(metadata);
        } catch (Exception e) {
            log.warn("Failed to serialize like metadata", e);
            return "{}";
        }
    }

    public static String createFollowerMetadata(
            ObjectMapper objectMapper,
            UUID followerUserId,
            String followerUsername) {

        try {
            Map<String, Object> metadata = Map.of(
                    "followerUserId", followerUserId.toString(),
                    "followerUsername", followerUsername,
                    "timestamp", System.currentTimeMillis()
            );

            return objectMapper.writeValueAsString(metadata);
        } catch (Exception e) {
            log.warn("Failed to serialize follower metadata", e);
            return "{}";
        }
    }

    public static String createLiveStreamMetadata(
            ObjectMapper objectMapper,
            UUID streamerId,
            String streamerUsername,
            String streamTitle) {

        try {
            Map<String, Object> metadata = Map.of(
                    "streamerId", streamerId.toString(),
                    "streamerUsername", streamerUsername,
                    "streamTitle", streamTitle,
                    "timestamp", System.currentTimeMillis()
            );

            return objectMapper.writeValueAsString(metadata);
        } catch (Exception e) {
            log.warn("Failed to serialize live stream metadata", e);
            return "{}";
        }
    }

    private static boolean isRetryableError(String error) {

        return error != null && (
                error.contains("network") ||
                error.contains("timeout") ||
                error.contains("connection") ||
                error.contains("temporary")
        );
    }
}
