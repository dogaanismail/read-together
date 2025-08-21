package org.readtogether.infrastructure.websocket.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Map;
import java.util.UUID;

@Slf4j
@UtilityClass
public class WebSocketUtils {

    public static void sendToUserQueue(
            SimpMessagingTemplate messagingTemplate,
            UUID userId,
            String queueDestination,
            Map<String, Object> notification) {

        try {
            messagingTemplate.convertAndSendToUser(
                    userId.toString(),
                    queueDestination,
                    notification
            );

            log.debug("Sent notification to user {} on queue {}: {}",
                    userId, queueDestination, notification.get("type"));

        } catch (Exception e) {
            log.error("Failed to send notification to user {} on queue {}",
                    userId, queueDestination, e);
        }
    }

    public static void sendSessionStatusNotification(
            SimpMessagingTemplate messagingTemplate,
            UUID userId,
            Map<String, Object> notification) {

        sendToUserQueue(messagingTemplate, userId, "/queue/session-status", notification);
        log.info("Sent session status notification to user {} for session {}: {}",
                userId, notification.get("sessionId"), notification.get("status"));
    }

    public static void sendUploadProgressNotification(
            SimpMessagingTemplate messagingTemplate,
            UUID userId,
            Map<String, Object> notification) {

        sendToUserQueue(messagingTemplate, userId, "/queue/upload-progress", notification);
    }

    public static void sendGeneralNotification(
            SimpMessagingTemplate messagingTemplate,
            UUID userId,
            Map<String, Object> notification) {

        sendToUserQueue(messagingTemplate, userId, "/queue/notifications", notification);
    }
}
