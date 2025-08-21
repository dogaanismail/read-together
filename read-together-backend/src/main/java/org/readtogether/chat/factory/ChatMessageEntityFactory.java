package org.readtogether.chat.factory;

import lombok.experimental.UtilityClass;
import org.readtogether.chat.entity.ChatMessageEntity;
import org.readtogether.chat.model.request.ChatMessageSendRequest;
import org.readtogether.chat.model.request.ChatMessageWebSocketRequest;
import org.readtogether.common.enums.MessageType;

import java.time.Instant;
import java.util.UUID;

@UtilityClass
public class ChatMessageEntityFactory {

    public static ChatMessageEntity createFromWebSocketRequest(
            ChatMessageWebSocketRequest request,
            UUID senderId) {
        
        return ChatMessageEntity.builder()
                .chatRoomId(request.getChatRoomId())
                .senderId(senderId)
                .content(request.getContent())
                .messageType(request.getType())
                .sentAt(Instant.now())
                .isDeleted(false)
                .replyToMessageId(request.getReplyToMessageId())
                .attachmentUrl(request.getAttachmentUrl())
                .attachmentName(request.getAttachmentName())
                .attachmentSize(request.getAttachmentSize())
                .attachmentType(request.getAttachmentType())
                .build();
    }

    public static ChatMessageEntity createFromRestRequest(
            ChatMessageSendRequest request,
            UUID senderId,
            String attachmentUrl,
            String attachmentName,
            Long attachmentSize,
            String attachmentType) {
        
        return ChatMessageEntity.builder()
                .chatRoomId(request.getChatRoomId())
                .senderId(senderId)
                .content(request.getContent())
                .messageType(request.getType())
                .sentAt(Instant.now())
                .isDeleted(false)
                .replyToMessageId(request.getReplyToMessageId())
                .attachmentUrl(attachmentUrl)
                .attachmentName(attachmentName)
                .attachmentSize(attachmentSize)
                .attachmentType(attachmentType)
                .build();
    }

    public static ChatMessageEntity createSystemMessage(
            UUID chatRoomId,
            String content) {
        
        return ChatMessageEntity.builder()
                .chatRoomId(chatRoomId)
                .senderId(null) // System messages have no sender
                .content(content)
                .messageType(MessageType.SYSTEM)
                .sentAt(Instant.now())
                .isDeleted(false)
                .build();
    }
}