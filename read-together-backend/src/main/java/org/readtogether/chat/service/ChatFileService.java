package org.readtogether.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.readtogether.chat.model.request.ChatMessageSendRequest;
import org.readtogether.chat.model.response.ChatMessageResponse;
import org.readtogether.chat.util.ChatFileStorageUtils;
import org.readtogether.chat.util.ChatFileUtils;
import org.readtogether.common.enums.MessageType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatFileService {

    private final ChatService chatService;
    private final ChatWebSocketService chatWebSocketService;

    @Transactional
    public ChatMessageResponse sendMessageWithFile(
            ChatMessageSendRequest request,
            UUID senderId) throws IOException {
        
        log.info("Processing message with file attachment for room: {}", request.getChatRoomId());
        
        MultipartFile file = request.getAttachment();
        String attachmentUrl = null;
        String attachmentName = null;
        Long attachmentSize = null;
        String attachmentType = null;

        if (file != null && !file.isEmpty()) {
            ChatFileUtils.validateFile(file);
            
            String fileName = ChatFileStorageUtils.uploadFile(file);
            attachmentUrl = "/api/v1/chat/files/" + fileName;
            attachmentName = file.getOriginalFilename();
            attachmentSize = file.getSize();
            attachmentType = file.getContentType();
            
            log.info("Successfully uploaded file: {} for message in room: {}", fileName, request.getChatRoomId());
        }

        ChatMessageResponse message = chatService.sendMessageWithAttachment(
            request, senderId, attachmentUrl, attachmentName, attachmentSize, attachmentType
        );

        chatWebSocketService.sendMessageToRoom(request.getChatRoomId(), message);

        return message;
    }

    public String getFileUrl(String fileName) {
        return ChatFileStorageUtils.getFilePath(fileName).toString();
    }

    private MessageType determineMessageType(String contentType) {
        if (ChatFileUtils.isImageFile(contentType)) {
            return MessageType.IMAGE;
        }
        return MessageType.FILE;
    }
}