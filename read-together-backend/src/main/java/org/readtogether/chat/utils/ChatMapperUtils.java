package org.readtogether.chat.utils;

import lombok.experimental.UtilityClass;
import org.readtogether.chat.entity.ChatMessageEntity;
import org.readtogether.chat.entity.ChatParticipantEntity;
import org.readtogether.chat.entity.ChatRoomEntity;
import org.readtogether.chat.model.response.ChatMessageResponse;
import org.readtogether.chat.model.response.ChatParticipantResponse;
import org.readtogether.chat.model.response.ChatRoomResponse;
import org.readtogether.user.entity.UserEntity;

@UtilityClass
public class ChatMapperUtils {

    public static ChatMessageResponse mapToChatMessageResponse(ChatMessageEntity message, UserEntity sender) {
        return ChatMessageResponse.builder()
            .id(message.getId())
            .chatRoomId(message.getChatRoomId())
            .senderId(message.getSenderId())
            .senderName(sender != null ? sender.getFirstName() + " " + sender.getLastName() : "System")
            .senderUsername(sender != null ? sender.getUsername() : "system")
            .senderAvatar(sender != null ? sender.getProfilePictureUrl() : null)
            .content(message.getContent())
            .messageType(message.getMessageType())
            .sentAt(message.getSentAt())
            .editedAt(message.getEditedAt())
            .isDeleted(message.isDeleted())
            .replyToMessageId(message.getReplyToMessageId())
            .attachmentUrl(message.getAttachmentUrl())
            .attachmentName(message.getAttachmentName())
            .attachmentSize(message.getAttachmentSize())
            .attachmentType(message.getAttachmentType())
            .build();
    }

    public static ChatRoomResponse.ChatRoomType mapToResponseType(ChatRoomEntity.ChatRoomType entityType) {
        return switch (entityType) {
            case DIRECT -> ChatRoomResponse.ChatRoomType.DIRECT;
            case GROUP -> ChatRoomResponse.ChatRoomType.GROUP;
        };
    }

    public static ChatParticipantResponse.ParticipantRole mapToResponseRole(ChatParticipantEntity.ParticipantRole entityRole) {
        return switch (entityRole) {
            case ADMIN -> ChatParticipantResponse.ParticipantRole.ADMIN;
            case MODERATOR -> ChatParticipantResponse.ParticipantRole.MODERATOR;
            case MEMBER -> ChatParticipantResponse.ParticipantRole.MEMBER;
        };
    }
}