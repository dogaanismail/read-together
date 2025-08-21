package org.readtogether.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.readtogether.chat.model.response.ChatMessageResponse;
import org.readtogether.infrastructure.websocket.util.WebSocketUtils;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatWebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendMessageToRoom(UUID chatRoomId, ChatMessageResponse message) {
        log.debug("Sending message to room: {} via WebSocket", chatRoomId);
        
        try {
            messagingTemplate.convertAndSend(
                "/topic/chat/" + chatRoomId,
                createMessageNotification(message)
            );
            
            log.debug("Successfully sent message to room: {}", chatRoomId);
        } catch (Exception e) {
            log.error("Failed to send message to room: {}", chatRoomId, e);
        }
    }

    public void sendPrivateMessage(UUID userId, ChatMessageResponse message) {
        log.debug("Sending private message notification to user: {}", userId);
        
        try {
            messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/chat",
                createMessageNotification(message)
            );
            
            log.debug("Successfully sent private message notification to user: {}", userId);
        } catch (Exception e) {
            log.error("Failed to send private message notification to user: {}", userId, e);
        }
    }

    public void notifyTyping(UUID chatRoomId, UUID userId, String username, boolean isTyping) {
        log.debug("Sending typing notification for room: {} from user: {}", chatRoomId, userId);
        
        Map<String, Object> typingNotification = Map.of(
            "type", "TYPING",
            "chatRoomId", chatRoomId.toString(),
            "userId", userId.toString(),
            "username", username,
            "isTyping", isTyping,
            "timestamp", System.currentTimeMillis()
        );

        try {
            messagingTemplate.convertAndSend(
                "/topic/chat/" + chatRoomId + "/typing",
                typingNotification
            );
        } catch (Exception e) {
            log.error("Failed to send typing notification to room: {}", chatRoomId, e);
        }
    }

    public void notifyUserJoined(UUID chatRoomId, UUID userId, String username) {
        log.debug("Sending user joined notification for room: {}", chatRoomId);
        
        Map<String, Object> joinNotification = Map.of(
            "type", "USER_JOINED",
            "chatRoomId", chatRoomId.toString(),
            "userId", userId.toString(),
            "username", username,
            "message", username + " joined the chat",
            "timestamp", System.currentTimeMillis()
        );

        try {
            messagingTemplate.convertAndSend(
                "/topic/chat/" + chatRoomId + "/events",
                joinNotification
            );
        } catch (Exception e) {
            log.error("Failed to send user joined notification to room: {}", chatRoomId, e);
        }
    }

    public void notifyUserLeft(UUID chatRoomId, UUID userId, String username) {
        log.debug("Sending user left notification for room: {}", chatRoomId);
        
        Map<String, Object> leaveNotification = Map.of(
            "type", "USER_LEFT",
            "chatRoomId", chatRoomId.toString(),
            "userId", userId.toString(),
            "username", username,
            "message", username + " left the chat",
            "timestamp", System.currentTimeMillis()
        );

        try {
            messagingTemplate.convertAndSend(
                "/topic/chat/" + chatRoomId + "/events",
                leaveNotification
            );
        } catch (Exception e) {
            log.error("Failed to send user left notification to room: {}", chatRoomId, e);
        }
    }

    public void notifyMessageRead(UUID chatRoomId, UUID userId, UUID messageId) {
        log.debug("Sending message read notification for room: {}", chatRoomId);
        
        Map<String, Object> readNotification = Map.of(
            "type", "MESSAGE_READ",
            "chatRoomId", chatRoomId.toString(),
            "userId", userId.toString(),
            "messageId", messageId.toString(),
            "timestamp", System.currentTimeMillis()
        );

        try {
            messagingTemplate.convertAndSend(
                "/topic/chat/" + chatRoomId + "/events",
                readNotification
            );
        } catch (Exception e) {
            log.error("Failed to send message read notification to room: {}", chatRoomId, e);
        }
    }

    private Map<String, Object> createMessageNotification(ChatMessageResponse message) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "NEW_MESSAGE");
        notification.put("message", message);
        notification.put("timestamp", System.currentTimeMillis());
        return notification;
    }
}