package org.readtogether.chat.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.readtogether.chat.entity.ChatMessageEntity;
import org.readtogether.chat.entity.ChatParticipantEntity;
import org.readtogether.chat.entity.ChatRoomEntity;
import org.readtogether.chat.model.request.ChatMessageWebSocketRequest;
import org.readtogether.chat.model.request.ChatRoomCreateRequest;
import org.readtogether.chat.model.response.ChatMessageResponse;
import org.readtogether.chat.model.response.ChatRoomResponse;
import org.readtogether.chat.repository.ChatMessageRepository;
import org.readtogether.chat.repository.ChatParticipantRepository;
import org.readtogether.chat.repository.ChatRoomRepository;
import org.readtogether.chat.fixtures.ChatRoomEntityFixtures;
import org.readtogether.chat.fixtures.ChatParticipantEntityFixtures;
import org.readtogether.chat.fixtures.ChatMessageEntityFixtures;
import org.readtogether.chat.fixtures.ChatRequestFixtures;
import org.readtogether.user.entity.UserEntity;
import org.readtogether.user.repository.UserRepository;
import org.readtogether.user.fixtures.UserEntityFixtures;
import org.readtogether.user.exception.UserNotFoundException;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChatService Tests")
class ChatServiceTests {

    private static final UUID TEST_USER_ID = UserEntityFixtures.DEFAULT_USER_ID;
    private static final UUID TEST_ROOM_ID = ChatRoomEntityFixtures.DEFAULT_ROOM_ID;
    private static final UUID TEST_MESSAGE_ID = ChatMessageEntityFixtures.DEFAULT_MESSAGE_ID;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private ChatParticipantRepository chatParticipantRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ChatService chatService;

    private UserEntity testUser;
    private ChatRoomEntity testRoom;
    private ChatParticipantEntity testParticipant;
    private ChatMessageEntity testMessage;

    @BeforeEach
    void setUp() {
        testUser = UserEntityFixtures.createDefaultUserEntity();
        testRoom = ChatRoomEntityFixtures.createDefaultRoom();
        testParticipant = ChatParticipantEntityFixtures.createMember(TEST_ROOM_ID, TEST_USER_ID);
        testMessage = ChatMessageEntityFixtures.createMessage(TEST_MESSAGE_ID, TEST_ROOM_ID, TEST_USER_ID, "Test message");
    }

    @Test
    @DisplayName("Should get user chat rooms with paging")
    void shouldGetUserChatRoomsWithPaging() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<ChatRoomEntity> rooms = Collections.singletonList(testRoom);
        Page<ChatRoomEntity> roomsPage = new PageImpl<>(rooms, pageable, 1);

        when(chatRoomRepository.findUserChatRooms(eq(TEST_USER_ID), eq(pageable)))
                .thenReturn(roomsPage);
        when(chatParticipantRepository.findByChatRoomIdAndIsActiveTrue(eq(TEST_ROOM_ID)))
                .thenReturn(Collections.singletonList(testParticipant));

        // When
        Page<ChatRoomResponse> result = chatService.getUserChatRooms(TEST_USER_ID, 0, 10);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getId()).isEqualTo(TEST_ROOM_ID);
        assertThat(result.getContent().getFirst().getName()).isEqualTo("Test Chat Room");

        verify(chatRoomRepository).findUserChatRooms(eq(TEST_USER_ID), eq(pageable));
        verify(chatParticipantRepository).findByChatRoomIdAndIsActiveTrue(eq(TEST_ROOM_ID));
    }

    @Test
    @DisplayName("Should create chat room and add participants")
    void shouldCreateChatRoomAndAddParticipants() {
        // Given
        ChatRoomCreateRequest request = ChatRequestFixtures.createDefaultChatRoomCreateRequest();

        when(userRepository.existsById(eq(TEST_USER_ID))).thenReturn(true);
        when(userRepository.existsById(eq(UserEntityFixtures.SECONDARY_USER_ID))).thenReturn(true);
        when(chatRoomRepository.save(any(ChatRoomEntity.class))).thenReturn(testRoom);
        when(chatParticipantRepository.save(any(ChatParticipantEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(chatParticipantRepository.findByChatRoomIdAndIsActiveTrue(eq(TEST_ROOM_ID)))
                .thenReturn(Collections.singletonList(testParticipant));

        // When
        ChatRoomResponse result = chatService.createChatRoom(request, TEST_USER_ID);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(TEST_ROOM_ID);
        assertThat(result.getName()).isEqualTo("Test Chat Room");

        verify(chatRoomRepository).save(any(ChatRoomEntity.class));
        verify(chatParticipantRepository, times(2)).save(any(ChatParticipantEntity.class));
    }

    @Test
    @DisplayName("Should get messages for participant")
    void shouldGetMessagesForParticipant() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        List<ChatMessageEntity> messages = Collections.singletonList(testMessage);
        Page<ChatMessageEntity> messagesPage = new PageImpl<>(messages, pageable, 1);

        when(chatRoomRepository.isUserParticipant(eq(TEST_ROOM_ID), eq(TEST_USER_ID)))
                .thenReturn(true);
        when(chatMessageRepository.findByChatRoomIdOrderBySentAtDesc(eq(TEST_ROOM_ID), eq(pageable)))
                .thenReturn(messagesPage);
        when(userRepository.findById(eq(TEST_USER_ID))).thenReturn(Optional.of(testUser));

        // When
        Page<ChatMessageResponse> result = chatService.getChatMessages(TEST_ROOM_ID, TEST_USER_ID, 0, 20);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getId()).isEqualTo(TEST_MESSAGE_ID);
        assertThat(result.getContent().getFirst().getContent()).isEqualTo("Test message");

        verify(chatRoomRepository).isUserParticipant(eq(TEST_ROOM_ID), eq(TEST_USER_ID));
        verify(chatMessageRepository).findByChatRoomIdOrderBySentAtDesc(eq(TEST_ROOM_ID), eq(pageable));
    }

    @Test
    @DisplayName("Should send WebSocket message and increment unread")
    void shouldSendWebSocketMessageAndIncrementUnread() {
        // Given
        ChatMessageWebSocketRequest request = ChatRequestFixtures.createChatMessageWebSocketRequest(
                TEST_ROOM_ID, "WebSocket message"
        );

        when(chatRoomRepository.isUserParticipant(eq(TEST_ROOM_ID), eq(TEST_USER_ID)))
                .thenReturn(true);
        when(chatMessageRepository.save(any(ChatMessageEntity.class))).thenReturn(testMessage);
        when(userRepository.findById(eq(TEST_USER_ID))).thenReturn(Optional.of(testUser));

        // When
        ChatMessageResponse result = chatService.sendMessage(request, TEST_USER_ID);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("Test message");

        verify(chatMessageRepository).save(any(ChatMessageEntity.class));
        verify(chatParticipantRepository).incrementUnreadCount(eq(TEST_ROOM_ID), eq(TEST_USER_ID));
    }

    @Test
    @DisplayName("Should mark messages as read")
    void shouldMarkMessagesAsRead() {
        // Given
        when(chatRoomRepository.isUserParticipant(eq(TEST_ROOM_ID), eq(TEST_USER_ID)))
                .thenReturn(true);

        // When
        chatService.markMessagesAsRead(TEST_ROOM_ID, TEST_USER_ID);

        // Then
        verify(chatRoomRepository).isUserParticipant(eq(TEST_ROOM_ID), eq(TEST_USER_ID));
        verify(chatParticipantRepository).markAsRead(eq(TEST_ROOM_ID), eq(TEST_USER_ID), any());
    }

    @Test
    @DisplayName("Should get or create direct chat")
    void shouldGetOrCreateDirectChat() {
        // Given
        UUID user2Id = UserEntityFixtures.SECONDARY_USER_ID;
        UserEntity user1 = testUser;
        UserEntity user2 = UserEntityFixtures.createSecondaryUserEntity();

        when(userRepository.findById(eq(TEST_USER_ID))).thenReturn(Optional.of(user1));
        when(userRepository.findById(eq(user2Id))).thenReturn(Optional.of(user2));
        when(chatRoomRepository.findDirectChatRoom(eq(TEST_USER_ID), eq(user2Id)))
                .thenReturn(Optional.empty());
        when(chatRoomRepository.save(any(ChatRoomEntity.class))).thenReturn(testRoom);
        when(chatParticipantRepository.save(any(ChatParticipantEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        ChatRoomResponse result = chatService.getOrCreateDirectChat(TEST_USER_ID, user2Id);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(TEST_ROOM_ID);

        verify(chatRoomRepository).findDirectChatRoom(eq(TEST_USER_ID), eq(user2Id));
        verify(chatRoomRepository).save(any(ChatRoomEntity.class));
        verify(chatParticipantRepository, times(2)).save(any(ChatParticipantEntity.class));
    }

    // Negative test cases

    @Test
    @DisplayName("Should throw when creator user not found")
    void shouldThrowWhenCreatorUserNotFound() {
        // Given
        ChatRoomCreateRequest request = ChatRequestFixtures.createDefaultChatRoomCreateRequest();
        when(userRepository.existsById(eq(TEST_USER_ID))).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> chatService.createChatRoom(request, TEST_USER_ID))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository).existsById(eq(TEST_USER_ID));
        verify(chatRoomRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should deny access when not participant")
    void shouldDenyAccessWhenNotParticipant() {
        // Given
        when(chatRoomRepository.isUserParticipant(eq(TEST_ROOM_ID), eq(TEST_USER_ID)))
                .thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> chatService.getChatMessages(TEST_ROOM_ID, TEST_USER_ID, 0, 20))
                .isInstanceOf(AccessDeniedException.class);

        verify(chatRoomRepository).isUserParticipant(eq(TEST_ROOM_ID), eq(TEST_USER_ID));
        verify(chatMessageRepository, never()).findByChatRoomIdOrderBySentAtDesc(any(), any());
    }

    @Test
    @DisplayName("Should throw when room not found for send message")
    void shouldThrowWhenRoomNotFoundForSendMessage() {
        // Given
        ChatMessageWebSocketRequest request = ChatRequestFixtures.createChatMessageWebSocketRequest(
                TEST_ROOM_ID, "Message"
        );
        when(chatRoomRepository.isUserParticipant(eq(TEST_ROOM_ID), eq(TEST_USER_ID)))
                .thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> chatService.sendMessage(request, TEST_USER_ID))
                .isInstanceOf(AccessDeniedException.class);

        verify(chatMessageRepository, never()).save(any());
    }
}