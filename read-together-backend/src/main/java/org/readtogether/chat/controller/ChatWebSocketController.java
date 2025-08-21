package org.readtogether.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.readtogether.chat.model.request.ChatMessageWebSocketRequest;
import org.readtogether.chat.model.response.ChatMessageResponse;
import org.readtogether.chat.service.ChatService;
import org.readtogether.chat.service.ChatWebSocketService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatService chatService;
    private final ChatWebSocketService chatWebSocketService;

    @MessageMapping("/chat/send")
    public void sendMessage(
            @Payload ChatMessageWebSocketRequest request,
            SimpMessageHeaderAccessor headerAccessor) {
        
        log.debug("Received WebSocket message for room: {}", request.getChatRoomId());
        
        try {
            UUID userId = getUserIdFromAuth(headerAccessor);
            
            // Send message through service
            ChatMessageResponse message = chatService.sendMessage(request, userId);
            
            // Broadcast to room subscribers
            chatWebSocketService.sendMessageToRoom(request.getChatRoomId(), message);
            
            log.info("Successfully processed WebSocket message: {}", message.getId());
            
        } catch (Exception e) {
            log.error("Error processing WebSocket message for room: {}", request.getChatRoomId(), e);
        }
    }

    @MessageMapping("/chat/typing")
    public void handleTyping(
            @Payload TypingRequest request,
            SimpMessageHeaderAccessor headerAccessor) {
        
        log.debug("Received typing notification for room: {}", request.getChatRoomId());
        
        try {
            UUID userId = getUserIdFromAuth(headerAccessor);
            String username = getUsernameFromAuth(headerAccessor);
            
            chatWebSocketService.notifyTyping(
                request.getChatRoomId(),
                userId,
                username,
                request.isTyping()
            );
            
        } catch (Exception e) {
            log.error("Error processing typing notification for room: {}", request.getChatRoomId(), e);
        }
    }

    @MessageMapping("/chat/join")
    public void joinRoom(
            @Payload JoinRoomRequest request,
            SimpMessageHeaderAccessor headerAccessor) {
        
        log.debug("User joining room: {}", request.getChatRoomId());
        
        try {
            UUID userId = getUserIdFromAuth(headerAccessor);
            String username = getUsernameFromAuth(headerAccessor);
            
            chatWebSocketService.notifyUserJoined(request.getChatRoomId(), userId, username);
            
        } catch (Exception e) {
            log.error("Error processing room join for room: {}", request.getChatRoomId(), e);
        }
    }

    @MessageMapping("/chat/leave")
    public void leaveRoom(
            @Payload LeaveRoomRequest request,
            SimpMessageHeaderAccessor headerAccessor) {
        
        log.debug("User leaving room: {}", request.getChatRoomId());
        
        try {
            UUID userId = getUserIdFromAuth(headerAccessor);
            String username = getUsernameFromAuth(headerAccessor);
            
            chatWebSocketService.notifyUserLeft(request.getChatRoomId(), userId, username);
            
        } catch (Exception e) {
            log.error("Error processing room leave for room: {}", request.getChatRoomId(), e);
        }
    }

    @MessageMapping("/chat/read")
    public void markAsRead(
            @Payload ReadMessageRequest request,
            SimpMessageHeaderAccessor headerAccessor) {
        
        log.debug("Marking message as read: {}", request.getMessageId());
        
        try {
            UUID userId = getUserIdFromAuth(headerAccessor);
            
            chatService.markMessagesAsRead(request.getChatRoomId(), userId);
            chatWebSocketService.notifyMessageRead(request.getChatRoomId(), userId, request.getMessageId());
            
        } catch (Exception e) {
            log.error("Error processing read notification for message: {}", request.getMessageId(), e);
        }
    }

    private UUID getUserIdFromAuth(SimpMessageHeaderAccessor headerAccessor) {
        Authentication auth = (Authentication) headerAccessor.getUser();
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            return UUID.fromString(jwt.getClaim("userId"));
        }
        throw new IllegalStateException("Unable to extract user ID from WebSocket authentication");
    }

    private String getUsernameFromAuth(SimpMessageHeaderAccessor headerAccessor) {
        Authentication auth = (Authentication) headerAccessor.getUser();
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            return jwt.getClaim("username");
        }
        return "Unknown";
    }

    // Inner classes for WebSocket request payloads
    public static class TypingRequest {
        private UUID chatRoomId;
        private boolean typing;

        public UUID getChatRoomId() { return chatRoomId; }
        public void setChatRoomId(UUID chatRoomId) { this.chatRoomId = chatRoomId; }
        public boolean isTyping() { return typing; }
        public void setTyping(boolean typing) { this.typing = typing; }
    }

    public static class JoinRoomRequest {
        private UUID chatRoomId;

        public UUID getChatRoomId() { return chatRoomId; }
        public void setChatRoomId(UUID chatRoomId) { this.chatRoomId = chatRoomId; }
    }

    public static class LeaveRoomRequest {
        private UUID chatRoomId;

        public UUID getChatRoomId() { return chatRoomId; }
        public void setChatRoomId(UUID chatRoomId) { this.chatRoomId = chatRoomId; }
    }

    public static class ReadMessageRequest {
        private UUID chatRoomId;
        private UUID messageId;

        public UUID getChatRoomId() { return chatRoomId; }
        public void setChatRoomId(UUID chatRoomId) { this.chatRoomId = chatRoomId; }
        public UUID getMessageId() { return messageId; }
        public void setMessageId(UUID messageId) { this.messageId = messageId; }
    }
}