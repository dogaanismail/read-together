package org.readtogether.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.readtogether.chat.model.request.ChatMessageWebSocketRequest;
import org.readtogether.chat.model.request.JoinLeaveWebSocketRequest;
import org.readtogether.chat.model.request.ReadWebSocketRequest;
import org.readtogether.chat.model.request.TypingWebSocketRequest;
import org.readtogether.chat.model.response.ChatMessageResponse;
import org.readtogether.chat.service.ChatService;
import org.readtogether.chat.service.ChatWebSocketService;
import org.readtogether.common.utils.SecurityUtils;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.Authentication;
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
            Authentication auth = (Authentication) headerAccessor.getUser();
            UUID userId = SecurityUtils.getCurrentUserId(auth);
            
            ChatMessageResponse message = chatService.sendMessage(request, userId);
            
            chatWebSocketService.sendMessageToRoom(request.getChatRoomId(), message);
            
            log.info("Successfully processed WebSocket message: {}", message.getId());
            
        } catch (Exception e) {
            log.error("Error processing WebSocket message for room: {}", request.getChatRoomId(), e);
        }
    }

    @MessageMapping("/chat/typing")
    public void handleTyping(
            @Payload TypingWebSocketRequest request,
            SimpMessageHeaderAccessor headerAccessor) {
        
        log.debug("Received typing notification for room: {}", request.getChatRoomId());
        
        try {
            Authentication auth = (Authentication) headerAccessor.getUser();
            UUID userId = SecurityUtils.getCurrentUserId(auth);
            Authentication auth2 = (Authentication) headerAccessor.getUser();
            String username = chatWebSocketService.getUsernameFromAuth(auth2);
            
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
            @Payload JoinLeaveWebSocketRequest request,
            SimpMessageHeaderAccessor headerAccessor) {
        
        log.debug("User joining room: {}", request.getChatRoomId());
        
        try {
            Authentication auth = (Authentication) headerAccessor.getUser();
            UUID userId = SecurityUtils.getCurrentUserId(auth);
            String username = chatWebSocketService.getUsernameFromAuth(auth);
            
            chatWebSocketService.notifyUserJoined(request.getChatRoomId(), userId, username);
            
        } catch (Exception e) {
            log.error("Error processing room join for room: {}", request.getChatRoomId(), e);
        }
    }

    @MessageMapping("/chat/leave")
    public void leaveRoom(
            @Payload JoinLeaveWebSocketRequest request,
            SimpMessageHeaderAccessor headerAccessor) {
        
        log.debug("User leaving room: {}", request.getChatRoomId());
        
        try {
            Authentication auth = (Authentication) headerAccessor.getUser();
            UUID userId = SecurityUtils.getCurrentUserId(auth);
            String username = chatWebSocketService.getUsernameFromAuth(auth);
            
            chatWebSocketService.notifyUserLeft(request.getChatRoomId(), userId, username);
            
        } catch (Exception e) {
            log.error("Error processing room leave for room: {}", request.getChatRoomId(), e);
        }
    }

    @MessageMapping("/chat/read")
    public void markAsRead(
            @Payload ReadWebSocketRequest request,
            SimpMessageHeaderAccessor headerAccessor) {
        
        log.debug("Marking message as read: {}", request.getMessageId());
        
        try {
            Authentication auth = (Authentication) headerAccessor.getUser();
            UUID userId = SecurityUtils.getCurrentUserId(auth);
            
            chatService.markMessagesAsRead(request.getChatRoomId(), userId);
            chatWebSocketService.notifyMessageRead(request.getChatRoomId(), userId, request.getMessageId());
            
        } catch (Exception e) {
            log.error("Error processing read notification for message: {}", request.getMessageId(), e);
        }
    }
}