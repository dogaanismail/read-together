package org.readtogether.chat.factory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.readtogether.chat.entity.ChatMessageEntity;
import org.readtogether.chat.model.request.ChatMessageSendRequest;
import org.readtogether.chat.model.request.ChatMessageWebSocketRequest;
import org.readtogether.chat.common.enums.MessageType;
import org.readtogether.chat.fixtures.ChatRequestFixtures;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ChatMessageEntityFactory Tests")
class ChatMessageEntityFactoryTest {

    private static final UUID TEST_ROOM_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440100");
    private static final UUID TEST_SENDER_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    private static final UUID TEST_REPLY_MESSAGE_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440300");

    @Test
    @DisplayName("Should create message from WebSocket request")
    void shouldCreateMessageFromWebSocketRequest() {
        // Given
        ChatMessageWebSocketRequest request = ChatRequestFixtures.createChatMessageWebSocketRequest(
                TEST_ROOM_ID, "Hello World"
        );

        // When
        ChatMessageEntity result = ChatMessageEntityFactory.createFromWebSocketRequest(request, TEST_SENDER_ID);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getChatRoomId()).isEqualTo(TEST_ROOM_ID);
        assertThat(result.getSenderId()).isEqualTo(TEST_SENDER_ID);
        assertThat(result.getContent()).isEqualTo("Hello World");
        assertThat(result.getMessageType()).isEqualTo(MessageType.TEXT);
        assertThat(result.getSentAt()).isNotNull();
        assertThat(result.isDeleted()).isFalse();
        assertThat(result.getReplyToMessageId()).isNull();
        assertThat(result.getAttachmentUrl()).isNull();
    }

    @Test
    @DisplayName("Should create message from REST request")
    void shouldCreateMessageFromRestRequest() {
        // Given
        ChatMessageSendRequest request = ChatRequestFixtures.createChatMessageSendRequest(
                TEST_ROOM_ID, "REST message"
        );
        String attachmentUrl = "/files/test.jpg";
        String attachmentName = "test.jpg";
        Long attachmentSize = 1024L;
        String attachmentType = "image/jpeg";

        // When
        ChatMessageEntity result = ChatMessageEntityFactory.createFromRestRequest(
                request, TEST_SENDER_ID, attachmentUrl, attachmentName, attachmentSize, attachmentType
        );

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getChatRoomId()).isEqualTo(TEST_ROOM_ID);
        assertThat(result.getSenderId()).isEqualTo(TEST_SENDER_ID);
        assertThat(result.getContent()).isEqualTo("REST message");
        assertThat(result.getMessageType()).isEqualTo(MessageType.TEXT);
        assertThat(result.getSentAt()).isNotNull();
        assertThat(result.isDeleted()).isFalse();
        assertThat(result.getAttachmentUrl()).isEqualTo(attachmentUrl);
        assertThat(result.getAttachmentName()).isEqualTo(attachmentName);
        assertThat(result.getAttachmentSize()).isEqualTo(attachmentSize);
        assertThat(result.getAttachmentType()).isEqualTo(attachmentType);
    }

    @Test
    @DisplayName("Should create system message")
    void shouldCreateSystemMessage() {
        // Given
        String systemContent = "User joined the room";

        // When
        ChatMessageEntity result = ChatMessageEntityFactory.createSystemMessage(TEST_ROOM_ID, systemContent);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getChatRoomId()).isEqualTo(TEST_ROOM_ID);
        assertThat(result.getSenderId()).isNull(); // System messages have no sender
        assertThat(result.getContent()).isEqualTo(systemContent);
        assertThat(result.getMessageType()).isEqualTo(MessageType.SYSTEM);
        assertThat(result.getSentAt()).isNotNull();
        assertThat(result.isDeleted()).isFalse();
    }

    @Test
    @DisplayName("Should default unset fields correctly")
    void shouldDefaultUnsetFields() {
        // Given
        ChatMessageWebSocketRequest request = ChatMessageWebSocketRequest.builder()
                .chatRoomId(TEST_ROOM_ID)
                .content("Minimal message")
                .type(MessageType.TEXT)
                .build();

        // When
        ChatMessageEntity result = ChatMessageEntityFactory.createFromWebSocketRequest(request, TEST_SENDER_ID);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isDeleted()).isFalse();
        assertThat(result.getSentAt()).isNotNull();
        assertThat(result.getEditedAt()).isNull();
        assertThat(result.getReplyToMessageId()).isNull();
        assertThat(result.getAttachmentUrl()).isNull();
        assertThat(result.getAttachmentName()).isNull();
        assertThat(result.getAttachmentSize()).isNull();
        assertThat(result.getAttachmentType()).isNull();
        
        // Verify the timestamp is recent (within the last 10 seconds)
        assertThat(result.getSentAt().toEpochMilli())
                .isCloseTo(System.currentTimeMillis(), org.assertj.core.data.Offset.offset(10000L));
    }

    @Test
    @DisplayName("Should handle reply message from WebSocket request")
    void shouldHandleReplyMessageFromWebSocketRequest() {
        // Given
        ChatMessageWebSocketRequest request = ChatMessageWebSocketRequest.builder()
                .chatRoomId(TEST_ROOM_ID)
                .content("Reply message")
                .type(MessageType.TEXT)
                .replyToMessageId(TEST_REPLY_MESSAGE_ID)
                .build();

        // When
        ChatMessageEntity result = ChatMessageEntityFactory.createFromWebSocketRequest(request, TEST_SENDER_ID);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getReplyToMessageId()).isEqualTo(TEST_REPLY_MESSAGE_ID);
        assertThat(result.getContent()).isEqualTo("Reply message");
    }

    @Test
    @DisplayName("Should handle attachment data from WebSocket request")
    void shouldHandleAttachmentDataFromWebSocketRequest() {
        // Given
        ChatMessageWebSocketRequest request = ChatRequestFixtures.createChatMessageWebSocketRequestWithAttachment(
                TEST_ROOM_ID,
                "Message with attachment",
                "/files/document.pdf",
                "document.pdf",
                2048L,
                "application/pdf"
        );

        // When
        ChatMessageEntity result = ChatMessageEntityFactory.createFromWebSocketRequest(request, TEST_SENDER_ID);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAttachmentUrl()).isEqualTo("/files/document.pdf");
        assertThat(result.getAttachmentName()).isEqualTo("document.pdf");
        assertThat(result.getAttachmentSize()).isEqualTo(2048L);
        assertThat(result.getAttachmentType()).isEqualTo("application/pdf");
        assertThat(result.getMessageType()).isEqualTo(MessageType.FILE);
    }

    @Test
    @DisplayName("Should handle null attachment data in REST request")
    void shouldHandleNullAttachmentDataInRestRequest() {
        // Given
        ChatMessageSendRequest request = ChatRequestFixtures.createChatMessageSendRequest(
                TEST_ROOM_ID, "Message without attachment"
        );

        // When
        ChatMessageEntity result = ChatMessageEntityFactory.createFromRestRequest(
                request, TEST_SENDER_ID, null, null, null, null
        );

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAttachmentUrl()).isNull();
        assertThat(result.getAttachmentName()).isNull();
        assertThat(result.getAttachmentSize()).isNull();
        assertThat(result.getAttachmentType()).isNull();
    }
}