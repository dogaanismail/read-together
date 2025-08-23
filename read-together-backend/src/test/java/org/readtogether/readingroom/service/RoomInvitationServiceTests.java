package org.readtogether.readingroom.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.readtogether.readingroom.entity.ReadingRoomEntity;
import org.readtogether.readingroom.entity.ReadingRoomInvitationEntity;
import org.readtogether.readingroom.fixtures.ReadingRoomEntityFixtures;
import org.readtogether.readingroom.fixtures.ReadingRoomInvitationEntityFixtures;
import org.readtogether.readingroom.fixtures.ReadingRoomRequestFixtures;
import org.readtogether.readingroom.model.request.InviteToRoomRequest;
import org.readtogether.readingroom.model.response.InvitationResponse;
import org.readtogether.readingroom.repository.ReadingRoomInvitationRepository;
import org.readtogether.readingroom.repository.ReadingRoomRepository;
import org.readtogether.user.entity.UserEntity;
import org.readtogether.user.fixtures.UserEntityFixtures;
import org.readtogether.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.readtogether.readingroom.common.enums.InvitationStatus.*;
import static org.readtogether.readingroom.common.enums.InvitationType.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RoomInvitationService Tests")
class RoomInvitationServiceTests {

    @Mock
    private ReadingRoomInvitationRepository invitationRepository;

    @Mock
    private ReadingRoomRepository roomRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private RoomInvitationService roomInvitationService;

    private ReadingRoomEntity room;
    private UserEntity hostUser;
    private UserEntity invitedUser;
    private ReadingRoomInvitationEntity invitation;
    private UUID roomId;
    private UUID hostId;
    private UUID invitedUserId;

    @BeforeEach
    void setUp() {
        room = ReadingRoomEntityFixtures.createDefaultRoomEntity();
        hostUser = UserEntityFixtures.createDefaultUserEntity();
        invitedUser = UserEntityFixtures.createSecondaryUserEntity();
        invitation = ReadingRoomInvitationEntityFixtures.createDirectInvitation(
                room, hostUser, invitedUser, LocalDateTime.now().plusDays(1));
        
        roomId = room.getId();
        hostId = hostUser.getId();
        invitedUserId = invitedUser.getId();
    }

    @Test
    @DisplayName("Should invite by email")
    void shouldInviteByEmail() {
        // Given
        InviteToRoomRequest request = ReadingRoomRequestFixtures.createDefaultEmailInviteRequest();

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(userService.findUserEntityById(hostId)).thenReturn(hostUser);
        when(invitationRepository.saveAll(any())).thenReturn(Arrays.asList(invitation));

        // When
        List<InvitationResponse> result = roomInvitationService.inviteToRoom(roomId, request, hostId);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(2); // 2 emails in the request

        verify(roomRepository).findById(roomId);
        verify(userService).findUserEntityById(hostId);
        verify(invitationRepository).saveAll(any());
    }

    @Test
    @DisplayName("Should invite direct users")
    void shouldInviteDirectUsers() {
        // Given
        InviteToRoomRequest request = ReadingRoomRequestFixtures.createDirectInviteRequest();

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(userService.findUserEntityById(hostId)).thenReturn(hostUser);
        when(userService.findUserEntityById(any(UUID.class))).thenReturn(invitedUser);
        when(invitationRepository.saveAll(any())).thenReturn(Arrays.asList(invitation));

        // When
        List<InvitationResponse> result = roomInvitationService.inviteToRoom(roomId, request, hostId);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(2); // 2 users in the request

        verify(roomRepository).findById(roomId);
        verify(userService).findUserEntityById(hostId);
        verify(userService, times(2)).findUserEntityById(any(UUID.class));
        verify(invitationRepository).saveAll(any());
    }

    @Test
    @DisplayName("Should create share invitation")
    void shouldCreateShareInvitation() {
        // Given
        InviteToRoomRequest request = ReadingRoomRequestFixtures.createShareLinkInviteRequest();

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(userService.findUserEntityById(hostId)).thenReturn(hostUser);
        when(invitationRepository.save(any(ReadingRoomInvitationEntity.class))).thenReturn(invitation);

        // When
        List<InvitationResponse> result = roomInvitationService.inviteToRoom(roomId, request, hostId);

        // Then
        assertThat(result).hasSize(1); // one share invitation created
        assertThat(result.get(0).getInvitationType()).isEqualTo(LINK_SHARE);

        verify(roomRepository).findById(roomId);
        verify(userService).findUserEntityById(hostId);
        verify(invitationRepository).save(any(ReadingRoomInvitationEntity.class));
    }

    @Test
    @DisplayName("Should throw when unsupported invitation type")
    void shouldThrowWhenUnsupportedInvitationType() {
        // Given
        InviteToRoomRequest request = InviteToRoomRequest.builder()
                .invitationType(CONTACT_SHARE) // unsupported type
                .message("Test message")
                .expirationHours(24)
                .build();

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(userService.findUserEntityById(hostId)).thenReturn(hostUser);

        // When / Then
        assertThatThrownBy(() -> roomInvitationService.inviteToRoom(roomId, request, hostId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unsupported invitation type");

        verify(invitationRepository, never()).save(any());
        verify(invitationRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("Should accept invitation when pending and not expired")
    void shouldAcceptInvitationWhenPendingAndNotExpired() {
        // Given
        String invitationToken = invitation.getInvitationToken();
        UUID acceptingUserId = invitedUserId;

        when(invitationRepository.findByInvitationToken(invitationToken))
                .thenReturn(Optional.of(invitation));
        when(userService.findUserEntityById(acceptingUserId)).thenReturn(invitedUser);
        when(invitationRepository.save(any(ReadingRoomInvitationEntity.class))).thenReturn(invitation);

        // When
        InvitationResponse result = roomInvitationService.acceptInvitation(invitationToken, acceptingUserId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(ACCEPTED);

        verify(invitationRepository).findByInvitationToken(invitationToken);
        verify(userService).findUserEntityById(acceptingUserId);
        verify(invitationRepository).save(any(ReadingRoomInvitationEntity.class));
    }

    @Test
    @DisplayName("Should decline invitation")
    void shouldDeclineInvitation() {
        // Given
        String invitationToken = invitation.getInvitationToken();

        when(invitationRepository.findByInvitationToken(invitationToken))
                .thenReturn(Optional.of(invitation));
        when(invitationRepository.save(any(ReadingRoomInvitationEntity.class))).thenReturn(invitation);

        // When
        InvitationResponse result = roomInvitationService.declineInvitation(invitationToken, hostId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(DECLINED);

        verify(invitationRepository).findByInvitationToken(invitationToken);
        verify(invitationRepository).save(any(ReadingRoomInvitationEntity.class));
    }

    @Test
    @DisplayName("Should throw when invitation expired")
    void shouldThrowWhenInvitationExpired() {
        // Given
        ReadingRoomInvitationEntity expiredInvitation = ReadingRoomInvitationEntityFixtures
                .createExpiredInvitation(room, hostUser, "expired@example.com");
        String invitationToken = expiredInvitation.getInvitationToken();

        when(invitationRepository.findByInvitationToken(invitationToken))
                .thenReturn(Optional.of(expiredInvitation));

        // When / Then
        assertThatThrownBy(() -> roomInvitationService.acceptInvitation(invitationToken, invitedUserId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invitation has expired");

        verify(invitationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw when invitation invalid token")
    void shouldThrowWhenInvitationInvalidToken() {
        // Given
        String invalidToken = "invalid-token";

        when(invitationRepository.findByInvitationToken(invalidToken))
                .thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> roomInvitationService.acceptInvitation(invalidToken, invitedUserId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invitation not found");

        verify(userService, never()).findUserEntityById(any());
        verify(invitationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should get room invitations by host")
    void shouldGetRoomInvitationsByHost() {
        // Given
        List<ReadingRoomInvitationEntity> invitations = Arrays.asList(invitation);

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(invitationRepository.findByReadingRoomId(roomId))
                .thenReturn(invitations);

        // When
        List<InvitationResponse> result = roomInvitationService.getRoomInvitations(roomId, hostId);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getReadingRoomId()).isEqualTo(roomId);

        verify(roomRepository).findById(roomId);
        verify(invitationRepository).findByReadingRoomId(roomId);
    }

    @Test
    @DisplayName("Should throw when non-host gets room invitations")
    void shouldThrowWhenNonHostGetsRoomInvitations() {
        // Given
        UUID nonHostId = UUID.randomUUID();

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));

        // When / Then
        assertThatThrownBy(() -> roomInvitationService.getRoomInvitations(roomId, nonHostId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Only the host can view invitations");

        verify(invitationRepository, never()).findByReadingRoomId(any());
    }

    @Test
    @DisplayName("Should get user pending invitations by user ID and email")
    void shouldGetUserPendingInvitationsByUserIdAndEmail() {
        // Given
        String email = invitedUser.getEmail();
        List<ReadingRoomInvitationEntity> userInvitations = Arrays.asList(invitation);
        List<ReadingRoomInvitationEntity> emailInvitations = Arrays.asList();

        when(userService.findUserEntityById(invitedUserId)).thenReturn(invitedUser);
        when(invitationRepository.findPendingInvitationsByUserId(invitedUserId))
                .thenReturn(userInvitations);
        when(invitationRepository.findPendingInvitationsByEmail(email))
                .thenReturn(emailInvitations);

        // When
        List<InvitationResponse> result = roomInvitationService.getUserPendingInvitations(invitedUserId);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(PENDING);

        verify(userService).findUserEntityById(invitedUserId);
        verify(invitationRepository).findPendingInvitationsByUserId(invitedUserId);
        verify(invitationRepository).findPendingInvitationsByEmail(email);
    }

    @Test
    @DisplayName("Should generate share link")
    void shouldGenerateShareLink() {
        // Given
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(userService.findUserEntityById(hostId)).thenReturn(hostUser);
        when(invitationRepository.save(any(ReadingRoomInvitationEntity.class))).thenReturn(invitation);

        // When
        String result = roomInvitationService.generateShareLink(roomId, hostId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).contains("http");
        assertThat(result).contains("join");

        verify(roomRepository).findById(roomId);
        verify(userService).findUserEntityById(hostId);
        verify(invitationRepository).save(any(ReadingRoomInvitationEntity.class));
    }

    @Test
    @DisplayName("Should get invitation by token")
    void shouldGetInvitationByToken() {
        // Given
        String invitationToken = invitation.getInvitationToken();

        when(invitationRepository.findByInvitationToken(invitationToken))
                .thenReturn(Optional.of(invitation));

        // When
        InvitationResponse result = roomInvitationService.getInvitationByToken(invitationToken);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getInvitationToken()).isEqualTo(invitationToken);

        verify(invitationRepository).findByInvitationToken(invitationToken);
    }

    @Test
    @DisplayName("Should throw when room not found for invitation")
    void shouldThrowWhenRoomNotFoundForInvitation() {
        // Given
        UUID nonExistentRoomId = UUID.randomUUID();
        InviteToRoomRequest request = ReadingRoomRequestFixtures.createDefaultEmailInviteRequest();

        when(roomRepository.findById(nonExistentRoomId)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> roomInvitationService.inviteToRoom(nonExistentRoomId, request, hostId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Room not found");

        verify(invitationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw when non-host tries to invite")
    void shouldThrowWhenNonHostTriesToInvite() {
        // Given
        InviteToRoomRequest request = ReadingRoomRequestFixtures.createDefaultEmailInviteRequest();
        UUID nonHostId = UUID.randomUUID();

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(userService.findUserEntityById(nonHostId)).thenReturn(invitedUser); // not the host

        // When / Then
        assertThatThrownBy(() -> roomInvitationService.inviteToRoom(roomId, request, nonHostId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Only the host can send invitations");

        verify(invitationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should handle already accepted invitation gracefully")
    void shouldHandleAlreadyAcceptedInvitationGracefully() {
        // Given
        ReadingRoomInvitationEntity acceptedInvitation = ReadingRoomInvitationEntityFixtures
                .createAcceptedInvitation(room, hostUser, invitedUser);
        String invitationToken = acceptedInvitation.getInvitationToken();

        when(invitationRepository.findByInvitationToken(invitationToken))
                .thenReturn(Optional.of(acceptedInvitation));

        // When / Then
        assertThatThrownBy(() -> roomInvitationService.acceptInvitation(invitationToken, invitedUserId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invitation is no longer pending");

        verify(invitationRepository, never()).save(any());
    }
}