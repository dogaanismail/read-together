package org.readtogether.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.readtogether.chat.factory.ChatWebSocketNotificationFactory;
import org.readtogether.chat.model.response.ChatMessageResponse;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

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
                ChatWebSocketNotificationFactory.createNewMessageNotification(message)
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
                ChatWebSocketNotificationFactory.createNewMessageNotification(message)
            );
            
            log.debug("Successfully sent private message notification to user: {}", userId);
        } catch (Exception e) {
            log.error("Failed to send private message notification to user: {}", userId, e);
        }
    }

    public void notifyTyping(UUID chatRoomId, UUID userId, String username, boolean isTyping) {
        log.debug("Sending typing notification for room: {} from user: {}", chatRoomId, userId);

        try {
            messagingTemplate.convertAndSend(
                "/topic/chat/" + chatRoomId + "/typing",
                ChatWebSocketNotificationFactory.createTypingNotification(chatRoomId, userId, username, isTyping)
            );
        } catch (Exception e) {
            log.error("Failed to send typing notification to room: {}", chatRoomId, e);
        }
    }

    public void notifyUserJoined(UUID chatRoomId, UUID userId, String username) {
        log.debug("Sending user joined notification for room: {}", chatRoomId);

        try {
            messagingTemplate.convertAndSend(
                "/topic/chat/" + chatRoomId + "/events",
                ChatWebSocketNotificationFactory.createUserJoinedNotification(chatRoomId, userId, username)
            );
        } catch (Exception e) {
            log.error("Failed to send user joined notification to room: {}", chatRoomId, e);
        }
    }

    public void notifyUserLeft(UUID chatRoomId, UUID userId, String username) {
        log.debug("Sending user left notification for room: {}", chatRoomId);

        try {
            messagingTemplate.convertAndSend(
                "/topic/chat/" + chatRoomId + "/events",
                ChatWebSocketNotificationFactory.createUserLeftNotification(chatRoomId, userId, username)
            );
        } catch (Exception e) {
            log.error("Failed to send user left notification to room: {}", chatRoomId, e);
        }
    }

    public void notifyMessageRead(UUID chatRoomId, UUID userId, UUID messageId) {
        log.debug("Sending message read notification for room: {}", chatRoomId);

        try {
            messagingTemplate.convertAndSend(
                "/topic/chat/" + chatRoomId + "/events",
                ChatWebSocketNotificationFactory.createMessageReadNotification(chatRoomId, userId, messageId)
            );
        } catch (Exception e) {
            log.error("Failed to send message read notification to room: {}", chatRoomId, e);
        }
    }

    public String getUsernameFromAuth(Authentication auth) {
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            return jwt.getClaim("username");
        }
        return "Unknown";
    }
}