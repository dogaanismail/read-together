package org.readtogether.chat.fixtures;

import lombok.experimental.UtilityClass;
import org.readtogether.chat.model.request.*;
import org.readtogether.chat.common.enums.ChatRoomType;
import org.readtogether.chat.common.enums.MessageType;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@UtilityClass
public class ChatRequestFixtures {

    public static ChatRoomCreateRequest createChatRoomCreateRequest(
            String name,
            String description,
            ChatRoomType type,
            List<UUID> participantIds) {

        return ChatRoomCreateRequest.builder()
                .name(name)
                .description(description)
                .type(type)
                .participantIds(participantIds)
                .maxParticipants(10)
                .build();
    }

    public static ChatRoomCreateRequest createDefaultChatRoomCreateRequest() {

        return createChatRoomCreateRequest(
                "Test Room",
                "A test chat room",
                ChatRoomType.GROUP,
                Arrays.asList(
                        UUID.fromString("550e8400-e29b-41d4-a716-446655440000"),
                        UUID.fromString("550e8400-e29b-41d4-a716-446655440001")
                )
        );
    }

    public static ChatRoomCreateRequest createDirectChatCreateRequest() {

        return createChatRoomCreateRequest(
                "Direct Chat",
                "A direct chat between two users",
                ChatRoomType.DIRECT,
                Arrays.asList(
                        UUID.fromString("550e8400-e29b-41d4-a716-446655440000"),
                        UUID.fromString("550e8400-e29b-41d4-a716-446655440001")
                )
        );
    }

    public static ChatMessageSendRequest createChatMessageSendRequest(
            UUID roomId,
            String content) {

        return ChatMessageSendRequest.builder()
                .chatRoomId(roomId)
                .content(content)
                .type(MessageType.TEXT)
                .build();
    }

    public static ChatMessageSendRequest createChatMessageSendRequestWithReply(
            UUID roomId,
            String content,
            UUID replyToMessageId) {

        return ChatMessageSendRequest.builder()
                .chatRoomId(roomId)
                .content(content)
                .type(MessageType.TEXT)
                .replyToMessageId(replyToMessageId)
                .build();
    }

    public static ChatMessageWebSocketRequest createChatMessageWebSocketRequest(
            UUID roomId,
            String content) {

        return ChatMessageWebSocketRequest.builder()
                .chatRoomId(roomId)
                .content(content)
                .type(MessageType.TEXT)
                .build();
    }

    public static ChatMessageWebSocketRequest createChatMessageWebSocketRequestWithAttachment(
            UUID roomId,
            String content,
            String attachmentUrl,
            String attachmentName,
            Long attachmentSize,
            String attachmentType) {

        return ChatMessageWebSocketRequest.builder()
                .chatRoomId(roomId)
                .content(content)
                .type(MessageType.FILE)
                .attachmentUrl(attachmentUrl)
                .attachmentName(attachmentName)
                .attachmentSize(attachmentSize)
                .attachmentType(attachmentType)
                .build();
    }

    public static TypingWebSocketRequest createTypingWebSocketRequest(
            UUID roomId,
            boolean typing) {

        TypingWebSocketRequest request = new TypingWebSocketRequest();
        request.setChatRoomId(roomId);
        request.setTyping(typing);
        return request;
    }

    public static ReadWebSocketRequest createReadWebSocketRequest(
            UUID roomId,
            UUID messageId) {

        ReadWebSocketRequest request = new ReadWebSocketRequest();
        request.setChatRoomId(roomId);
        request.setMessageId(messageId);
        return request;
    }

    public static JoinLeaveWebSocketRequest createJoinWebSocketRequest
            (UUID roomId) {

        JoinLeaveWebSocketRequest request = new JoinLeaveWebSocketRequest();
        request.setChatRoomId(roomId);
        return request;
    }

    public static JoinLeaveWebSocketRequest createLeaveWebSocketRequest(
            UUID roomId) {

        JoinLeaveWebSocketRequest request = new JoinLeaveWebSocketRequest();
        request.setChatRoomId(roomId);
        return request;
    }
}