package org.readtogether.chat.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import org.readtogether.chat.model.request.ChatMessageSendRequest;
import org.readtogether.chat.model.response.ChatMessageResponse;
import org.readtogether.chat.utils.ChatFileStorageUtils;
import org.readtogether.chat.utils.ChatFileUtils;
import org.readtogether.chat.fixtures.ChatRequestFixtures;
import org.readtogether.chat.fixtures.ChatResponseFixtures;
import org.readtogether.chat.exception.InvalidFileException;

import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChatFileService Tests")
class ChatFileServiceTests {

    private static final UUID TEST_ROOM_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440100");
    private static final UUID TEST_SENDER_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    private static final String TEST_FILE_NAME = "test-file-12345.jpg";

    @Mock
    private ChatService chatService;

    @Mock
    private ChatWebSocketService chatWebSocketService;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private ChatFileService chatFileService;

    private MockedStatic<ChatFileStorageUtils> fileStorageUtilsMock;

    private MockedStatic<ChatFileUtils> fileUtilsMock;

    private ChatMessageSendRequest testRequest;

    private ChatMessageResponse testResponse;

    @BeforeEach
    void setUp() {
        fileStorageUtilsMock = mockStatic(ChatFileStorageUtils.class);
        fileUtilsMock = mockStatic(ChatFileUtils.class);
        
        testRequest = ChatRequestFixtures.createChatMessageSendRequest(TEST_ROOM_ID, "Message with file");
        testRequest.setAttachment(multipartFile);
        
        testResponse = ChatResponseFixtures.createChatMessageResponse(
                UUID.randomUUID(), TEST_ROOM_ID, TEST_SENDER_ID, "Message with file"
        );
    }

    @AfterEach
    void tearDown() {
        fileStorageUtilsMock.close();
        fileUtilsMock.close();
    }

    @Test
    @DisplayName("Should upload file validate then send message - success path")
    void shouldUploadFileValidateThenSendMessage() throws IOException {
        // Given
        when(multipartFile.getOriginalFilename()).thenReturn("test.jpg");
        when(multipartFile.getSize()).thenReturn(1024L);
        when(multipartFile.getContentType()).thenReturn("image/jpeg");
        
        fileUtilsMock.when(() -> ChatFileUtils.validateFile(multipartFile))
                .thenAnswer(invocation -> null); // void method, no exception = success
        
        fileStorageUtilsMock.when(() -> ChatFileStorageUtils.uploadFile(multipartFile))
                .thenReturn(TEST_FILE_NAME);
        
        when(chatService.sendMessageWithAttachment(
                eq(testRequest),
                eq(TEST_SENDER_ID),
                eq("/api/v1/chat/files/" + TEST_FILE_NAME),
                eq("test.jpg"),
                eq(1024L),
                eq("image/jpeg")
        )).thenReturn(testResponse);

        // When
        ChatMessageResponse result = chatFileService.sendMessageWithFile(testRequest, TEST_SENDER_ID);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(testResponse);
        
        fileUtilsMock.verify(() -> ChatFileUtils.validateFile(multipartFile));
        fileStorageUtilsMock.verify(() -> ChatFileStorageUtils.uploadFile(multipartFile));
        verify(chatService).sendMessageWithAttachment(
                eq(testRequest),
                eq(TEST_SENDER_ID),
                eq("/api/v1/chat/files/" + TEST_FILE_NAME),
                eq("test.jpg"),
                eq(1024L),
                eq("image/jpeg")
        );
        verify(chatWebSocketService).sendMessageToRoom(eq(TEST_ROOM_ID), eq(testResponse));
    }

    @Test
    @DisplayName("Should reject large file size")
    void shouldRejectLargeFileSize() {
        // Given
        fileUtilsMock.when(() -> ChatFileUtils.validateFile(multipartFile))
                .thenThrow(new InvalidFileException("File size exceeds maximum allowed size"));

        // When & Then
        assertThatThrownBy(() -> chatFileService.sendMessageWithFile(testRequest, TEST_SENDER_ID))
                .isInstanceOf(InvalidFileException.class)
                .hasMessageContaining("File size exceeds maximum allowed size");
        
        fileUtilsMock.verify(() -> ChatFileUtils.validateFile(multipartFile));
        fileStorageUtilsMock.verifyNoInteractions();
        verify(chatService, never()).sendMessageWithAttachment(any(), any(), any(), any(), any(), any());
        verify(chatWebSocketService, never()).sendMessageToRoom(any(), any());
    }

    @Test
    @DisplayName("Should reject invalid file type")
    void shouldRejectInvalidFileType() {
        // Given
        fileUtilsMock.when(() -> ChatFileUtils.validateFile(multipartFile))
                .thenThrow(new InvalidFileException("File type not allowed"));

        // When & Then
        assertThatThrownBy(() -> chatFileService.sendMessageWithFile(testRequest, TEST_SENDER_ID))
                .isInstanceOf(InvalidFileException.class)
                .hasMessageContaining("File type not allowed");
        
        fileUtilsMock.verify(() -> ChatFileUtils.validateFile(multipartFile));
        fileStorageUtilsMock.verifyNoInteractions();
        verify(chatService, never()).sendMessageWithAttachment(any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("Should handle file upload failure")
    void shouldHandleFileUploadFailure() {
        // Given
        fileUtilsMock.when(() -> ChatFileUtils.validateFile(multipartFile))
                .thenAnswer(invocation -> null); // validation passes
        
        fileStorageUtilsMock.when(() -> ChatFileStorageUtils.uploadFile(multipartFile))
                .thenThrow(new IOException("Storage service unavailable"));

        // When & Then
        assertThatThrownBy(() -> chatFileService.sendMessageWithFile(testRequest, TEST_SENDER_ID))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("Storage service unavailable");
        
        fileUtilsMock.verify(() -> ChatFileUtils.validateFile(multipartFile));
        fileStorageUtilsMock.verify(() -> ChatFileStorageUtils.uploadFile(multipartFile));
        verify(chatService, never()).sendMessageWithAttachment(any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("Should handle null file attachment")
    void shouldHandleNullFileAttachment() {
        // Given
        testRequest.setAttachment(null);

        // When & Then
        assertThatThrownBy(() -> chatFileService.sendMessageWithFile(testRequest, TEST_SENDER_ID))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Should handle various file types correctly")
    void shouldHandleVariousFileTypesCorrectly() throws IOException {
        // Given - PDF file
        when(multipartFile.getOriginalFilename()).thenReturn("document.pdf");
        when(multipartFile.getSize()).thenReturn(2048L);
        when(multipartFile.getContentType()).thenReturn("application/pdf");
        
        fileUtilsMock.when(() -> ChatFileUtils.validateFile(multipartFile))
                .thenAnswer(invocation -> null); // validation passes
        
        fileStorageUtilsMock.when(() -> ChatFileStorageUtils.uploadFile(multipartFile))
                .thenReturn("document-67890.pdf");
        
        when(chatService.sendMessageWithAttachment(
                any(), any(), any(), any(), any(), any()
        )).thenReturn(testResponse);

        // When
        ChatMessageResponse result = chatFileService.sendMessageWithFile(testRequest, TEST_SENDER_ID);

        // Then
        assertThat(result).isNotNull();
        verify(chatService).sendMessageWithAttachment(
                eq(testRequest),
                eq(TEST_SENDER_ID),
                eq("/api/v1/chat/files/document-67890.pdf"),
                eq("document.pdf"),
                eq(2048L),
                eq("application/pdf")
        );
    }

    @Test
    @DisplayName("Should generate correct attachment URL format")
    void shouldGenerateCorrectAttachmentUrlFormat() throws IOException {
        // Given
        when(multipartFile.getOriginalFilename()).thenReturn("image.png");
        when(multipartFile.getSize()).thenReturn(512L);
        when(multipartFile.getContentType()).thenReturn("image/png");
        
        fileUtilsMock.when(() -> ChatFileUtils.validateFile(multipartFile))
                .thenAnswer(invocation -> null);
        
        String uploadedFileName = "unique-image-name.png";
        fileStorageUtilsMock.when(() -> ChatFileStorageUtils.uploadFile(multipartFile))
                .thenReturn(uploadedFileName);
        
        when(chatService.sendMessageWithAttachment(any(), any(), any(), any(), any(), any()))
                .thenReturn(testResponse);

        // When
        chatFileService.sendMessageWithFile(testRequest, TEST_SENDER_ID);

        // Then
        verify(chatService).sendMessageWithAttachment(
                eq(testRequest),
                eq(TEST_SENDER_ID),
                eq("/api/v1/chat/files/" + uploadedFileName),
                eq("image.png"),
                eq(512L),
                eq("image/png")
        );
    }
}