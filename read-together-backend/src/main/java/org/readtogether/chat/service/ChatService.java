package org.readtogether.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.readtogether.chat.entity.ChatMessageEntity;
import org.readtogether.chat.entity.ChatParticipantEntity;
import org.readtogether.chat.entity.ChatRoomEntity;
import org.readtogether.chat.factory.ChatMessageEntityFactory;
import org.readtogether.chat.factory.ChatParticipantEntityFactory;
import org.readtogether.chat.factory.ChatRoomEntityFactory;
import org.readtogether.chat.model.request.ChatMessageSendRequest;
import org.readtogether.chat.model.request.ChatMessageWebSocketRequest;
import org.readtogether.chat.model.request.ChatRoomCreateRequest;
import org.readtogether.chat.model.response.ChatMessageResponse;
import org.readtogether.chat.model.response.ChatParticipantResponse;
import org.readtogether.chat.model.response.ChatRoomResponse;
import org.readtogether.chat.repository.ChatMessageRepository;
import org.readtogether.chat.repository.ChatParticipantRepository;
import org.readtogether.chat.repository.ChatRoomRepository;
import org.readtogether.chat.util.ChatUtils;
import org.readtogether.chat.exception.ChatRoomNotFoundException;
import org.readtogether.user.exception.UserNotFoundException;
import org.readtogether.user.entity.UserEntity;
import org.readtogether.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<ChatRoomResponse> getUserChatRooms(UUID userId, int page, int size) {
        log.debug("Getting chat rooms for user: {}", userId);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ChatRoomEntity> chatRooms = chatRoomRepository.findUserChatRooms(userId, pageable);
        
        return chatRooms.map(room -> mapToChatRoomResponse(room, userId));
    }

    @Transactional
    public ChatRoomResponse createChatRoom(ChatRoomCreateRequest request, UUID creatorId) {
        log.info("Creating chat room: {} by user: {}", request.getName(), creatorId);
        
        // Validate creator exists
        if (!userRepository.existsById(creatorId)) {
            throw new UserNotFoundException("User not found: " + creatorId);
        }

        // Create room
        ChatRoomEntity room = ChatRoomEntityFactory.createFromRequest(request, creatorId);
        room = chatRoomRepository.save(room);

        // Add creator as admin
        ChatParticipantEntity creatorParticipant = ChatParticipantEntityFactory
            .createAdmin(room.getId(), creatorId);
        chatParticipantRepository.save(creatorParticipant);

        // Add other participants if specified
        if (request.getParticipantIds() != null) {
            for (UUID participantId : request.getParticipantIds()) {
                if (!participantId.equals(creatorId) && userRepository.existsById(participantId)) {
                    ChatParticipantEntity participant = ChatParticipantEntityFactory
                        .createMember(room.getId(), participantId);
                    chatParticipantRepository.save(participant);
                }
            }
        }

        log.info("Created chat room: {} with ID: {}", room.getName(), room.getId());
        return mapToChatRoomResponse(room, creatorId);
    }

    @Transactional(readOnly = true)
    public Page<ChatMessageResponse> getChatMessages(UUID chatRoomId, UUID userId, int page, int size) {
        log.debug("Getting messages for chat room: {} by user: {}", chatRoomId, userId);
        
        // Verify user is participant
        if (!chatRoomRepository.isUserParticipant(chatRoomId, userId)) {
            throw new AccessDeniedException("User is not a participant in this chat room");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<ChatMessageEntity> messages = chatMessageRepository
            .findByChatRoomIdOrderBySentAtDesc(chatRoomId, pageable);
        
        return messages.map(this::mapToChatMessageResponse);
    }

    @Transactional
    public ChatMessageResponse sendMessage(ChatMessageWebSocketRequest request, UUID senderId) {
        log.debug("Sending WebSocket message to room: {} from user: {}", request.getChatRoomId(), senderId);
        
        // Verify user is participant
        if (!chatRoomRepository.isUserParticipant(request.getChatRoomId(), senderId)) {
            throw new AccessDeniedException("User is not a participant in this chat room");
        }

        // Create and save message
        ChatMessageEntity message = ChatMessageEntityFactory.createFromWebSocketRequest(request, senderId);
        message = chatMessageRepository.save(message);

        // Update unread counts
        chatParticipantRepository.incrementUnreadCount(request.getChatRoomId(), senderId);

        log.info("Sent message: {} to room: {}", message.getId(), request.getChatRoomId());
        return mapToChatMessageResponse(message);
    }

    @Transactional
    public void markMessagesAsRead(UUID chatRoomId, UUID userId) {
        log.debug("Marking messages as read for room: {} by user: {}", chatRoomId, userId);
        
        if (!chatRoomRepository.isUserParticipant(chatRoomId, userId)) {
            throw new AccessDeniedException("User is not a participant in this chat room");
        }

        chatParticipantRepository.markAsRead(chatRoomId, userId, Instant.now());
    }

    @Transactional(readOnly = true)
    public ChatRoomResponse getOrCreateDirectChat(UUID user1Id, UUID user2Id) {
        log.debug("Getting or creating direct chat between users: {} and {}", user1Id, user2Id);
        
        // Check if direct chat already exists
        return chatRoomRepository.findDirectChatRoom(user1Id, user2Id)
            .map(room -> mapToChatRoomResponse(room, user1Id))
            .orElseGet(() -> createDirectChatRoom(user1Id, user2Id));
    }

    private ChatRoomResponse createDirectChatRoom(UUID user1Id, UUID user2Id) {
        // Get user names for room name
        UserEntity user1 = userRepository.findById(user1Id)
            .orElseThrow(() -> new UserNotFoundException("User not found: " + user1Id));
        UserEntity user2 = userRepository.findById(user2Id)
            .orElseThrow(() -> new UserNotFoundException("User not found: " + user2Id));

        String roomName = ChatUtils.generateDirectChatRoomName(
            user1.getFirstName() + " " + user1.getLastName(),
            user2.getFirstName() + " " + user2.getLastName()
        );

        // Create room
        ChatRoomEntity room = ChatRoomEntityFactory.createDirectChatRoom(user1Id, user2Id, roomName);
        room = chatRoomRepository.save(room);

        // Add both users as members
        ChatParticipantEntity participant1 = ChatParticipantEntityFactory.createMember(room.getId(), user1Id);
        ChatParticipantEntity participant2 = ChatParticipantEntityFactory.createMember(room.getId(), user2Id);
        
        chatParticipantRepository.save(participant1);
        chatParticipantRepository.save(participant2);

        log.info("Created direct chat room: {} between users: {} and {}", room.getId(), user1Id, user2Id);
        return mapToChatRoomResponse(room, user1Id);
    }

    private ChatRoomResponse mapToChatRoomResponse(ChatRoomEntity room, UUID currentUserId) {
        // Get participants
        List<ChatParticipantEntity> participants = chatParticipantRepository
            .findByChatRoomIdAndIsActiveTrue(room.getId());
        
        List<ChatParticipantResponse> participantResponses = participants.stream()
            .map(this::mapToChatParticipantResponse)
            .toList();

        // Get last message
        ChatMessageResponse lastMessage = chatMessageRepository
            .findLastMessageByChatRoomId(room.getId())
            .map(this::mapToChatMessageResponse)
            .orElse(null);

        // Get unread count for current user
        Integer unreadCount = participants.stream()
            .filter(p -> p.getUserId().equals(currentUserId))
            .findFirst()
            .map(ChatParticipantEntity::getUnreadCount)
            .orElse(0);

        return ChatRoomResponse.builder()
            .id(room.getId())
            .name(room.getName())
            .description(room.getDescription())
            .type(mapToResponseType(room.getType()))
            .creatorId(room.getCreatorId())
            .createdAt(room.getCreatedAt())
            .updatedAt(room.getUpdatedAt())
            .isActive(room.getIsActive())
            .maxParticipants(room.getMaxParticipants())
            .participants(participantResponses)
            .lastMessage(lastMessage)
            .unreadCount(unreadCount)
            .build();
    }

    private ChatParticipantResponse mapToChatParticipantResponse(ChatParticipantEntity participant) {
        UserEntity user = userRepository.findById(participant.getUserId()).orElse(null);
        
        return ChatParticipantResponse.builder()
            .id(participant.getId())
            .userId(participant.getUserId())
            .userName(user != null ? user.getFirstName() + " " + user.getLastName() : "Unknown")
            .username(user != null ? user.getUsername() : "unknown")
            .avatar(user != null ? user.getProfilePictureUrl() : null)
            .role(mapToResponseRole(participant.getRole()))
            .joinedAt(participant.getJoinedAt())
            .isActive(participant.getIsActive())
            .unreadCount(participant.getUnreadCount())
            .lastReadAt(participant.getLastReadAt())
            .online(false) // TODO: Implement online status tracking
            .lastSeen("Unknown") // TODO: Implement last seen tracking
            .build();
    }

    private ChatMessageResponse mapToChatMessageResponse(ChatMessageEntity message) {
        UserEntity sender = null;
        if (message.getSenderId() != null) {
            sender = userRepository.findById(message.getSenderId()).orElse(null);
        }
        
        return ChatMessageResponse.builder()
            .id(message.getId())
            .chatRoomId(message.getChatRoomId())
            .senderId(message.getSenderId())
            .senderName(sender != null ? sender.getFirstName() + " " + sender.getLastName() : "System")
            .senderUsername(sender != null ? sender.getUsername() : "system")
            .senderAvatar(sender != null ? sender.getProfilePictureUrl() : null)
            .content(message.getContent())
            .messageType(mapToResponseMessageType(message.getMessageType()))
            .sentAt(message.getSentAt())
            .editedAt(message.getEditedAt())
            .isDeleted(message.getIsDeleted())
            .replyToMessageId(message.getReplyToMessageId())
            .attachmentUrl(message.getAttachmentUrl())
            .attachmentName(message.getAttachmentName())
            .attachmentSize(message.getAttachmentSize())
            .attachmentType(message.getAttachmentType())
            .build();
    }

    private ChatRoomResponse.ChatRoomType mapToResponseType(ChatRoomEntity.ChatRoomType entityType) {
        return switch (entityType) {
            case DIRECT -> ChatRoomResponse.ChatRoomType.DIRECT;
            case GROUP -> ChatRoomResponse.ChatRoomType.GROUP;
        };
    }

    private ChatParticipantResponse.ParticipantRole mapToResponseRole(ChatParticipantEntity.ParticipantRole entityRole) {
        return switch (entityRole) {
            case ADMIN -> ChatParticipantResponse.ParticipantRole.ADMIN;
            case MODERATOR -> ChatParticipantResponse.ParticipantRole.MODERATOR;
            case MEMBER -> ChatParticipantResponse.ParticipantRole.MEMBER;
        };
    }

    private ChatMessageResponse.MessageType mapToResponseMessageType(ChatMessageEntity.MessageType entityType) {
        return switch (entityType) {
            case TEXT -> ChatMessageResponse.MessageType.TEXT;
            case IMAGE -> ChatMessageResponse.MessageType.IMAGE;
            case FILE -> ChatMessageResponse.MessageType.FILE;
            case EMOJI -> ChatMessageResponse.MessageType.EMOJI;
            case SYSTEM -> ChatMessageResponse.MessageType.SYSTEM;
        };
    }
}