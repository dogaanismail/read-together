package org.readtogether.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.readtogether.chat.model.request.ChatMessageSendRequest;
import org.readtogether.chat.model.response.ChatMessageResponse;
import org.readtogether.chat.utils.ChatFileStorageUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

import static org.readtogether.chat.utils.ChatFileUtils.validateFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatFileService {

    private final ChatService chatService;
    private final ChatWebSocketService chatWebSocketService;

    public ChatMessageResponse sendMessageWithFile(
            ChatMessageSendRequest request,
            UUID senderId) throws IOException {

        log.info("Processing message with file attachment for room: {}", request.getChatRoomId());

        MultipartFile file = request.getAttachment();
        validateFile(file);

        String fileName = ChatFileStorageUtils.uploadFile(file);
        String attachmentUrl = "/api/v1/chat/files/" + fileName;

        ChatMessageResponse message = chatService.sendMessageWithAttachment(
                request,
                senderId,
                attachmentUrl,
                file.getOriginalFilename(),
                file.getSize(),
                file.getContentType()
        );

        chatWebSocketService.sendMessageToRoom(request.getChatRoomId(), message);
        return message;
    }

}