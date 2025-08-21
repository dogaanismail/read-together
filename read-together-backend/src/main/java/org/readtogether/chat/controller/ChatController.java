package org.readtogether.chat.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.readtogether.chat.model.request.ChatRoomCreateRequest;
import org.readtogether.chat.model.response.ChatMessageResponse;
import org.readtogether.chat.model.response.ChatRoomResponse;
import org.readtogether.chat.service.ChatService;
import org.readtogether.chat.service.ChatWebSocketService;
import org.readtogether.common.model.CustomResponse;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final ChatWebSocketService chatWebSocketService;

    @GetMapping("/rooms")
    public CustomResponse<Page<ChatRoomResponse>> getUserChatRooms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        
        log.info("Getting chat rooms for user, page: {}, size: {}", page, size);
        
        UUID userId = getUserIdFromAuth(authentication);
        Page<ChatRoomResponse> chatRooms = chatService.getUserChatRooms(userId, page, size);
        
        return CustomResponse.successOf(chatRooms);
    }

    @PostMapping("/rooms")
    public CustomResponse<ChatRoomResponse> createChatRoom(
            @Valid @RequestBody ChatRoomCreateRequest request,
            Authentication authentication) {
        
        log.info("Creating chat room: {}", request.getName());
        
        UUID userId = getUserIdFromAuth(authentication);
        ChatRoomResponse chatRoom = chatService.createChatRoom(request, userId);
        
        return CustomResponse.successOf(chatRoom);
    }

    @GetMapping("/rooms/{roomId}/messages")
    public CustomResponse<Page<ChatMessageResponse>> getChatMessages(
            @PathVariable UUID roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            Authentication authentication) {
        
        log.info("Getting messages for chat room: {}, page: {}, size: {}", roomId, page, size);
        
        UUID userId = getUserIdFromAuth(authentication);
        Page<ChatMessageResponse> messages = chatService.getChatMessages(roomId, userId, page, size);
        
        return CustomResponse.successOf(messages);
    }

    @PostMapping("/rooms/{roomId}/read")
    public CustomResponse<Void> markMessagesAsRead(
            @PathVariable UUID roomId,
            Authentication authentication) {
        
        log.info("Marking messages as read for room: {}", roomId);
        
        UUID userId = getUserIdFromAuth(authentication);
        chatService.markMessagesAsRead(roomId, userId);
        
        return CustomResponse.SUCCESS;
    }

    @GetMapping("/direct/{otherUserId}")
    public CustomResponse<ChatRoomResponse> getOrCreateDirectChat(
            @PathVariable UUID otherUserId,
            Authentication authentication) {
        
        log.info("Getting or creating direct chat with user: {}", otherUserId);
        
        UUID userId = getUserIdFromAuth(authentication);
        ChatRoomResponse chatRoom = chatService.getOrCreateDirectChat(userId, otherUserId);
        
        return CustomResponse.successOf(chatRoom);
    }

    private UUID getUserIdFromAuth(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        return UUID.fromString(jwt.getClaim("userId"));
    }
}