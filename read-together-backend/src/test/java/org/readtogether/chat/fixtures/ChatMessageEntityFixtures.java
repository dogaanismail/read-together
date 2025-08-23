package org.readtogether.chat.fixtures;

import lombok.experimental.UtilityClass;
import org.readtogether.chat.entity.ChatMessageEntity;
import org.readtogether.chat.common.enums.MessageType;

import java.time.Instant;
import java.util.UUID;

@UtilityClass
public class ChatMessageEntityFixtures {

    public static final UUID DEFAULT_MESSAGE_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440300");

    public static ChatMessageEntity createMessage(
            UUID id,
            UUID roomId,
            UUID senderId,
            String content) {
        
        return ChatMessageEntity.builder()
                .id(id)
                .chatRoomId(roomId)
                .senderId(senderId)
                .content(content)
                .messageType(MessageType.TEXT)
                .sentAt(Instant.now())
                .isDeleted(false)
                .build();
    }

    public static ChatMessageEntity createMessage(
            UUID roomId,
            UUID senderId,
            String content) {
        
        return ChatMessageEntity.builder()
                .chatRoomId(roomId)
                .senderId(senderId)
                .content(content)
                .messageType(MessageType.TEXT)
                .sentAt(Instant.now())
                .isDeleted(false)
                .build();
    }

    public static ChatMessageEntity createMessageWithAttachment(
            UUID roomId,
            UUID senderId,
            String content,
            String attachmentUrl,
            String attachmentName,
            Long attachmentSize,
            String attachmentType) {
        
        return ChatMessageEntity.builder()
                .chatRoomId(roomId)
                .senderId(senderId)
                .content(content)
                .messageType(MessageType.FILE)
                .sentAt(Instant.now())
                .isDeleted(false)
                .attachmentUrl(attachmentUrl)
                .attachmentName(attachmentName)
                .attachmentSize(attachmentSize)
                .attachmentType(attachmentType)
                .build();
    }

    public static ChatMessageEntity createSystemMessage(
            UUID roomId,
            String content) {
        
        return ChatMessageEntity.builder()
                .chatRoomId(roomId)
                .senderId(null) // System messages have no sender
                .content(content)
                .messageType(MessageType.SYSTEM)
                .sentAt(Instant.now())
                .isDeleted(false)
                .build();
    }

    public static ChatMessageEntity createReplyMessage(
            UUID roomId,
            UUID senderId,
            String content,
            UUID replyToMessageId) {
        
        return ChatMessageEntity.builder()
                .chatRoomId(roomId)
                .senderId(senderId)
                .content(content)
                .messageType(MessageType.TEXT)
                .sentAt(Instant.now())
                .isDeleted(false)
                .replyToMessageId(replyToMessageId)
                .build();
    }
}