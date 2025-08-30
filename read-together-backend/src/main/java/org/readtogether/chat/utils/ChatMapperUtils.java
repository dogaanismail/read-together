package org.readtogether.chat.utils;

import lombok.experimental.UtilityClass;
import org.readtogether.chat.entity.ChatMessageEntity;
import org.readtogether.chat.common.enums.ChatRoomType;
import org.readtogether.chat.common.enums.ParticipantRole;
import org.readtogether.chat.model.response.ChatMessageResponse;
import org.readtogether.user.entity.UserEntity;

import static org.readtogether.chat.common.enums.ChatRoomType.DIRECT;
import static org.readtogether.chat.common.enums.ChatRoomType.GROUP;
import static org.readtogether.chat.common.enums.ParticipantRole.ADMIN;
import static org.readtogether.chat.common.enums.ParticipantRole.MODERATOR;
import static org.readtogether.chat.common.enums.ParticipantRole.MEMBER;

@UtilityClass
public class ChatMapperUtils {

    public static ChatMessageResponse mapToChatMessageResponse(
            ChatMessageEntity message,
            UserEntity sender) {

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

    public static ChatRoomType mapToResponseType(
            ChatRoomType entityType) {

        return switch (entityType) {
            case DIRECT -> DIRECT;
            case GROUP -> GROUP;
        };
    }

    public static ParticipantRole mapToResponseRole(
            ParticipantRole entityRole) {

        return switch (entityRole) {
            case ADMIN -> ADMIN;
            case MODERATOR -> MODERATOR;
            case MEMBER -> MEMBER;
        };
    }
}