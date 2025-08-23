package org.readtogether.readingroom.factory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.readtogether.readingroom.entity.ReadingRoomEntity;
import org.readtogether.readingroom.entity.ReadingRoomInvitationEntity;
import org.readtogether.readingroom.fixtures.ReadingRoomEntityFixtures;
import org.readtogether.readingroom.fixtures.ReadingRoomRequestFixtures;
import org.readtogether.readingroom.model.request.InviteToRoomRequest;
import org.readtogether.user.entity.UserEntity;
import org.readtogether.user.fixtures.UserEntityFixtures;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.readtogether.readingroom.common.enums.InvitationStatus.*;
import static org.readtogether.readingroom.common.enums.InvitationType.*;

class ReadingRoomInvitationEntityFactoryTests {

    @Test
    @DisplayName("Should create email invitation")
    void shouldCreateEmailInvitation() {
        // Given
        ReadingRoomEntity room = ReadingRoomEntityFixtures.createDefaultRoomEntity();
        UserEntity inviter = UserEntityFixtures.createDefaultUserEntity();
        String email = "test@example.com";
        String message = "Join our reading room!";
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(1);

        // When
        ReadingRoomInvitationEntity result = ReadingRoomInvitationEntityFactory.createEmailInvitation(
                room, inviter, email, message, expiresAt);

        // Then
        assertThat(result.getReadingRoom()).isEqualTo(room);
        assertThat(result.getInvitedBy()).isEqualTo(inviter);
        assertThat(result.getInvitedEmail()).isEqualTo(email);
        assertThat(result.getInvitedUser()).isNull(); // email invitations don't have invited user
        assertThat(result.getInvitationType()).isEqualTo(EMAIL);
        assertThat(result.getStatus()).isEqualTo(PENDING);
        assertThat(result.getExpiresAt()).isEqualTo(expiresAt);
        assertThat(result.getMessage()).isEqualTo(message);
        assertThat(result.getInvitationToken()).isNotNull();
        assertThat(result.getAcceptedAt()).isNull();
        assertThat(result.getDeclinedAt()).isNull();
    }

    @Test
    @DisplayName("Should create direct invitation")
    void shouldCreateDirectInvitation() {
        // Given
        ReadingRoomEntity room = ReadingRoomEntityFixtures.createPrivateRoomEntity();
        UserEntity inviter = UserEntityFixtures.createDefaultUserEntity();
        UserEntity invitedUser = UserEntityFixtures.createSecondaryUserEntity();
        String message = "Direct invitation";
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(2);

        // When
        ReadingRoomInvitationEntity result = ReadingRoomInvitationEntityFactory.createDirectInvitation(
                room, inviter, invitedUser, message, expiresAt);

        // Then
        assertThat(result.getReadingRoom()).isEqualTo(room);
        assertThat(result.getInvitedBy()).isEqualTo(inviter);
        assertThat(result.getInvitedUser()).isEqualTo(invitedUser);
        assertThat(result.getInvitedEmail()).isEqualTo(invitedUser.getEmail()); // copies from user
        assertThat(result.getInvitationType()).isEqualTo(DIRECT_INVITE);
        assertThat(result.getStatus()).isEqualTo(PENDING);
        assertThat(result.getExpiresAt()).isEqualTo(expiresAt);
        assertThat(result.getMessage()).isEqualTo(message);
        assertThat(result.getInvitationToken()).isNotNull();
    }

    @Test
    @DisplayName("Should create share link invitation")
    void shouldCreateShareLinkInvitation() {
        // Given
        ReadingRoomEntity room = ReadingRoomEntityFixtures.createActiveRoomEntity();
        UserEntity inviter = UserEntityFixtures.createDefaultUserEntity();
        String message = "Share this link!";
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(3);

        // When
        ReadingRoomInvitationEntity result = ReadingRoomInvitationEntityFactory.createShareLinkInvitation(
                room, inviter, message, expiresAt);

        // Then
        assertThat(result.getReadingRoom()).isEqualTo(room);
        assertThat(result.getInvitedBy()).isEqualTo(inviter);
        assertThat(result.getInvitedUser()).isNull();
        assertThat(result.getInvitedEmail()).isNull();
        assertThat(result.getInvitationType()).isEqualTo(LINK_SHARE);
        assertThat(result.getStatus()).isEqualTo(PENDING);
        assertThat(result.getExpiresAt()).isEqualTo(expiresAt);
        assertThat(result.getMessage()).isEqualTo(message);
        assertThat(result.getInvitationToken()).isNotNull();
    }

    @Test
    @DisplayName("Should create QR code invitation")
    void shouldCreateQRCodeInvitation() {
        // Given
        ReadingRoomEntity room = ReadingRoomEntityFixtures.createDefaultRoomEntity();
        UserEntity inviter = UserEntityFixtures.createSecondaryUserEntity();
        String message = "Scan QR code";
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(12);

        // When
        ReadingRoomInvitationEntity result = ReadingRoomInvitationEntityFactory.createQRCodeInvitation(
                room, inviter, message, expiresAt);

        // Then
        assertThat(result.getReadingRoom()).isEqualTo(room);
        assertThat(result.getInvitedBy()).isEqualTo(inviter);
        assertThat(result.getInvitedUser()).isNull();
        assertThat(result.getInvitedEmail()).isNull();
        assertThat(result.getInvitationType()).isEqualTo(QR_CODE);
        assertThat(result.getStatus()).isEqualTo(PENDING);
        assertThat(result.getExpiresAt()).isEqualTo(expiresAt);
        assertThat(result.getMessage()).isEqualTo(message);
        assertThat(result.getInvitationToken()).isNotNull();
    }

    @Test
    @DisplayName("Should create share invitation based on request type - LINK_SHARE")
    void shouldCreateShareInvitationBasedOnRequestTypeLinkShare() {
        // Given
        ReadingRoomEntity room = ReadingRoomEntityFixtures.createDefaultRoomEntity();
        UserEntity inviter = UserEntityFixtures.createDefaultUserEntity();
        InviteToRoomRequest request = ReadingRoomRequestFixtures.createShareLinkInviteRequest();
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(1);

        // When
        ReadingRoomInvitationEntity result = ReadingRoomInvitationEntityFactory.createShareInvitation(
                room, inviter, request, expiresAt);

        // Then
        assertThat(result.getInvitationType()).isEqualTo(LINK_SHARE);
        assertThat(result.getMessage()).isEqualTo(request.getMessage());
        assertThat(result.getReadingRoom()).isEqualTo(room);
        assertThat(result.getInvitedBy()).isEqualTo(inviter);
        assertThat(result.getExpiresAt()).isEqualTo(expiresAt);
    }

    @Test
    @DisplayName("Should create share invitation based on request type - QR_CODE")
    void shouldCreateShareInvitationBasedOnRequestTypeQRCode() {
        // Given
        ReadingRoomEntity room = ReadingRoomEntityFixtures.createPrivateRoomEntity();
        UserEntity inviter = UserEntityFixtures.createSecondaryUserEntity();
        InviteToRoomRequest request = ReadingRoomRequestFixtures.createQRCodeInviteRequest();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(6);

        // When
        ReadingRoomInvitationEntity result = ReadingRoomInvitationEntityFactory.createShareInvitation(
                room, inviter, request, expiresAt);

        // Then
        assertThat(result.getInvitationType()).isEqualTo(QR_CODE);
        assertThat(result.getMessage()).isEqualTo(request.getMessage());
        assertThat(result.getReadingRoom()).isEqualTo(room);
        assertThat(result.getInvitedBy()).isEqualTo(inviter);
        assertThat(result.getExpiresAt()).isEqualTo(expiresAt);
    }

    @Test
    @DisplayName("Should throw when unsupported share invitation type")
    void shouldThrowWhenUnsupportedShareInvitationType() {
        // Given
        ReadingRoomEntity room = ReadingRoomEntityFixtures.createDefaultRoomEntity();
        UserEntity inviter = UserEntityFixtures.createDefaultUserEntity();
        InviteToRoomRequest request = ReadingRoomRequestFixtures.createDirectInviteRequest(); // DIRECT_INVITE not supported for share
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(1);

        // When / Then
        assertThatThrownBy(() -> ReadingRoomInvitationEntityFactory.createShareInvitation(
                room, inviter, request, expiresAt))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unsupported share invitation type");
    }

    @Test
    @DisplayName("Should create accept invitation entity")
    void shouldCreateAcceptInvitationEntity() {
        // Given
        ReadingRoomEntity room = ReadingRoomEntityFixtures.createDefaultRoomEntity();
        UserEntity inviter = UserEntityFixtures.createDefaultUserEntity();
        UserEntity acceptingUser = UserEntityFixtures.createSecondaryUserEntity();
        ReadingRoomInvitationEntity invitation = ReadingRoomInvitationEntityFactory.createDirectInvitation(
                room, inviter, acceptingUser, "Test invitation", LocalDateTime.now().plusDays(1));

        // When
        ReadingRoomInvitationEntity result = ReadingRoomInvitationEntityFactory.createAcceptInvitationEntity(
                invitation, acceptingUser);

        // Then
        assertThat(result).isSameAs(invitation); // modifies existing entity
        assertThat(result.getStatus()).isEqualTo(ACCEPTED);
        assertThat(result.getAcceptedAt()).isNotNull();
        assertThat(result.getDeclinedAt()).isNull();
        assertThat(result.getInvitedUser()).isEqualTo(acceptingUser);
    }

    @Test
    @DisplayName("Should set invited user when accepting email invitation")
    void shouldSetInvitedUserWhenAcceptingEmailInvitation() {
        // Given
        ReadingRoomEntity room = ReadingRoomEntityFixtures.createDefaultRoomEntity();
        UserEntity inviter = UserEntityFixtures.createDefaultUserEntity();
        UserEntity acceptingUser = UserEntityFixtures.createSecondaryUserEntity();
        ReadingRoomInvitationEntity emailInvitation = ReadingRoomInvitationEntityFactory.createEmailInvitation(
                room, inviter, "test@example.com", "Email invitation", LocalDateTime.now().plusDays(1));
        
        // Email invitations start with null invited user
        assertThat(emailInvitation.getInvitedUser()).isNull();

        // When
        ReadingRoomInvitationEntity result = ReadingRoomInvitationEntityFactory.createAcceptInvitationEntity(
                emailInvitation, acceptingUser);

        // Then
        assertThat(result.getInvitedUser()).isEqualTo(acceptingUser);
        assertThat(result.getStatus()).isEqualTo(ACCEPTED);
        assertThat(result.getAcceptedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should create decline invitation entity")
    void shouldCreateDeclineInvitationEntity() {
        // Given
        ReadingRoomEntity room = ReadingRoomEntityFixtures.createPrivateRoomEntity();
        UserEntity inviter = UserEntityFixtures.createDefaultUserEntity();
        UserEntity invitedUser = UserEntityFixtures.createSecondaryUserEntity();
        ReadingRoomInvitationEntity invitation = ReadingRoomInvitationEntityFactory.createDirectInvitation(
                room, inviter, invitedUser, "Invitation to decline", LocalDateTime.now().plusDays(1));

        // When
        ReadingRoomInvitationEntity result = ReadingRoomInvitationEntityFactory.createDeclineInvitationEntity(invitation);

        // Then
        assertThat(result).isSameAs(invitation); // modifies existing entity
        assertThat(result.getStatus()).isEqualTo(DECLINED);
        assertThat(result.getDeclinedAt()).isNotNull();
        assertThat(result.getAcceptedAt()).isNull();
    }

    @Test
    @DisplayName("Should generate unique invitation tokens")
    void shouldGenerateUniqueInvitationTokens() {
        // Given
        ReadingRoomEntity room = ReadingRoomEntityFixtures.createDefaultRoomEntity();
        UserEntity inviter = UserEntityFixtures.createDefaultUserEntity();
        String email = "test@example.com";
        String message = "Test message";
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(1);

        // When
        ReadingRoomInvitationEntity invitation1 = ReadingRoomInvitationEntityFactory.createEmailInvitation(
                room, inviter, email, message, expiresAt);
        ReadingRoomInvitationEntity invitation2 = ReadingRoomInvitationEntityFactory.createEmailInvitation(
                room, inviter, email, message, expiresAt);

        // Then
        assertThat(invitation1.getInvitationToken()).isNotNull();
        assertThat(invitation2.getInvitationToken()).isNotNull();
        assertThat(invitation1.getInvitationToken()).isNotEqualTo(invitation2.getInvitationToken());
    }

    @Test
    @DisplayName("Should preserve original invitation data when accepting")
    void shouldPreserveOriginalInvitationDataWhenAccepting() {
        // Given
        ReadingRoomEntity room = ReadingRoomEntityFixtures.createDefaultRoomEntity();
        UserEntity inviter = UserEntityFixtures.createDefaultUserEntity();
        UserEntity acceptingUser = UserEntityFixtures.createSecondaryUserEntity();
        String originalMessage = "Original message";
        LocalDateTime originalExpiry = LocalDateTime.now().plusDays(1);
        String originalToken = "original-token";
        
        ReadingRoomInvitationEntity invitation = ReadingRoomInvitationEntityFactory.createShareLinkInvitation(
                room, inviter, originalMessage, originalExpiry);
        invitation.setInvitationToken(originalToken); // set known token for testing

        // When
        ReadingRoomInvitationEntity result = ReadingRoomInvitationEntityFactory.createAcceptInvitationEntity(
                invitation, acceptingUser);

        // Then
        assertThat(result.getReadingRoom()).isEqualTo(room);
        assertThat(result.getInvitedBy()).isEqualTo(inviter);
        assertThat(result.getMessage()).isEqualTo(originalMessage);
        assertThat(result.getExpiresAt()).isEqualTo(originalExpiry);
        assertThat(result.getInvitationToken()).isEqualTo(originalToken);
        assertThat(result.getInvitationType()).isEqualTo(LINK_SHARE);
    }
}