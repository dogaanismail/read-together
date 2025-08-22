package org.readtogether.chat.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.readtogether.chat.model.request.ChatMessageSendRequest;
import org.readtogether.chat.model.request.ChatRoomCreateRequest;
import org.readtogether.chat.model.response.ChatMessageResponse;
import org.readtogether.chat.model.response.ChatRoomResponse;
import org.readtogether.chat.service.ChatFileService;
import org.readtogether.chat.service.ChatService;
import org.readtogether.chat.utils.ChatFileStorageUtils;
import org.readtogether.common.model.response.CustomResponse;
import org.readtogether.common.utils.SecurityUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final ChatFileService chatFileService;

    @GetMapping("/rooms")
    public CustomResponse<Page<ChatRoomResponse>> getUserChatRooms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        
        log.info("Getting chat rooms for user, page: {}, size: {}", page, size);
        
        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        Page<ChatRoomResponse> chatRooms = chatService.getUserChatRooms(userId, page, size);
        
        return CustomResponse.successOf(chatRooms);
    }

    @PostMapping("/rooms")
    public CustomResponse<ChatRoomResponse> createChatRoom(
            @Valid @RequestBody ChatRoomCreateRequest request,
            Authentication authentication) {
        
        log.info("Creating chat room: {}", request.getName());
        
        UUID userId = SecurityUtils.getCurrentUserId(authentication);
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
        
        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        Page<ChatMessageResponse> messages = chatService.getChatMessages(roomId, userId, page, size);
        
        return CustomResponse.successOf(messages);
    }

    @PostMapping("/rooms/{roomId}/read")
    public CustomResponse<Void> markMessagesAsRead(
            @PathVariable UUID roomId,
            Authentication authentication) {
        
        log.info("Marking messages as read for room: {}", roomId);
        
        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        chatService.markMessagesAsRead(roomId, userId);
        
        return CustomResponse.SUCCESS;
    }

    @GetMapping("/direct/{otherUserId}")
    public CustomResponse<ChatRoomResponse> getOrCreateDirectChat(
            @PathVariable UUID otherUserId,
            Authentication authentication) {
        
        log.info("Getting or creating direct chat with user: {}", otherUserId);
        
        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        ChatRoomResponse chatRoom = chatService.getOrCreateDirectChat(userId, otherUserId);
        
        return CustomResponse.successOf(chatRoom);
    }

    @PostMapping("/rooms/{roomId}/messages")
    public CustomResponse<ChatMessageResponse> sendMessageWithFile(
            @PathVariable UUID roomId,
            @RequestPart("message") @Valid ChatMessageSendRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file,
            Authentication authentication) throws IOException {
        
        log.info("Sending message with file to room: {}", roomId);
        
        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        request.setChatRoomId(roomId);
        request.setAttachment(file);
        
        ChatMessageResponse message = chatFileService.sendMessageWithFile(request, userId);
        
        return CustomResponse.successOf(message);
    }

    @GetMapping("/files/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        log.info("Downloading file: {}", fileName);
        
        try {
            String filePath = ChatFileStorageUtils.getFilePath(fileName).toString();
            Path path = Paths.get(filePath);
            Resource resource = new UrlResource(path.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                String contentType = "application/octet-stream";
                
                return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            log.error("Error downloading file: {}", fileName, e);
            return ResponseEntity.badRequest().build();
        }
    }
}