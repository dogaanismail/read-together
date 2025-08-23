package org.readtogether.readingroom.factory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.readtogether.readingroom.entity.ReadingRoomInvitationEntity;
import org.readtogether.readingroom.fixtures.ReadingRoomEntityFixtures;
import org.readtogether.readingroom.fixtures.ReadingRoomInvitationEntityFixtures;
import org.readtogether.readingroom.model.response.InvitationResponse;
import org.readtogether.user.fixtures.UserEntityFixtures;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;
import static org.assertj.core.api.Assertions.assertThat;

class InvitationResponseFactoryTests {

    private static final String BASE_URL = "https://app.readtogether.com";

    @Test
    @DisplayName("Should create email invitation response")
    void shouldCreateEmailInvitationResponse() {
        // Given
        ReadingRoomInvitationEntity invitation = ReadingRoomInvitationEntityFixtures.createEmailInvitation(
                ReadingRoomEntityFixtures.createDefaultRoomEntity(),
                UserEntityFixtures.createDefaultUserEntity(),
                "test@example.com",
                Instant.now().plus(1, DAYS)
        );

        // When
        InvitationResponse result = InvitationResponseFactory.createResponse(invitation, BASE_URL);

        // Then
        assertThat(result.getId()).isEqualTo(invitation.getId());
        assertThat(result.getReadingRoomId()).isEqualTo(invitation.getReadingRoom().getId());
        assertThat(result.getRoomTitle()).isEqualTo(invitation.getReadingRoom().getTitle());
        assertThat(result.getRoomCode()).isEqualTo(invitation.getReadingRoom().getRoomCode());
        assertThat(result.getInvitedEmail()).isEqualTo(invitation.getInvitedEmail());
        assertThat(result.getInvitationToken()).isEqualTo(invitation.getInvitationToken());
        assertThat(result.getInvitationType()).isEqualTo(invitation.getInvitationType());
        assertThat(result.getStatus()).isEqualTo(invitation.getStatus());
        assertThat(result.getExpiresAt()).isEqualTo(invitation.getExpiresAt());
        assertThat(result.getMessage()).isEqualTo(invitation.getMessage());
        assertThat(result.getIsExpired()).isEqualTo(invitation.isExpired());
    }

    @Test
    @DisplayName("Should create direct invitation response")
    void shouldCreateDirectInvitationResponse() {
        // Given
        ReadingRoomInvitationEntity invitation = ReadingRoomInvitationEntityFixtures.createDirectInvitation(
                ReadingRoomEntityFixtures.createDefaultRoomEntity(),
                UserEntityFixtures.createDefaultUserEntity(),
                UserEntityFixtures.createSecondaryUserEntity(),
                Instant.now().plus(2, DAYS)
        );

        // When
        InvitationResponse result = InvitationResponseFactory.createResponse(invitation, BASE_URL);

        // Then
        assertThat(result.getInvitedUser()).isNotNull();
        assertThat(result.getInvitedUser().getId()).isEqualTo(invitation.getInvitedUser().getId());
        assertThat(result.getInvitedUser().getFirstName()).isEqualTo(invitation.getInvitedUser().getFirstName());
        assertThat(result.getInvitedUser().getLastName()).isEqualTo(invitation.getInvitedUser().getLastName());
        assertThat(result.getInvitedUser().getEmail()).isEqualTo(invitation.getInvitedUser().getEmail());
        assertThat(result.getInvitedEmail()).isNull(); // direct invitation doesn't use email field
    }

    @Test
    @DisplayName("Should map inviter information correctly")
    void shouldMapInviterInformationCorrectly() {
        // Given
        ReadingRoomInvitationEntity invitation = ReadingRoomInvitationEntityFixtures.createShareInvitation(
                ReadingRoomEntityFixtures.createDefaultRoomEntity(),
                UserEntityFixtures.createDefaultUserEntity(),
                Instant.now().plus(3, DAYS)
        );

        // When
        InvitationResponse result = InvitationResponseFactory.createResponse(invitation, BASE_URL);

        // Then
        assertThat(result.getInvitedBy()).isNotNull();
        assertThat(result.getInvitedBy().getId()).isEqualTo(invitation.getInvitedBy().getId());
        assertThat(result.getInvitedBy().getFirstName()).isEqualTo(invitation.getInvitedBy().getFirstName());
        assertThat(result.getInvitedBy().getLastName()).isEqualTo(invitation.getInvitedBy().getLastName());
        assertThat(result.getInvitedBy().getEmail()).isEqualTo(invitation.getInvitedBy().getEmail());
    }

    @Test
    @DisplayName("Should generate share link and QR code URL")
    void shouldGenerateShareLinkAndQRCodeUrl() {
        // Given
        ReadingRoomInvitationEntity invitation = ReadingRoomInvitationEntityFixtures.createShareInvitation(
                ReadingRoomEntityFixtures.createDefaultRoomEntity(),
                UserEntityFixtures.createDefaultUserEntity(),
                Instant.now().plus(1, DAYS)
        );

        // When
        InvitationResponse result = InvitationResponseFactory.createResponse(invitation, BASE_URL);

        // Then
        assertThat(result.getShareLink()).isNotNull();
        assertThat(result.getShareLink()).contains(BASE_URL);
        assertThat(result.getShareLink()).contains(invitation.getInvitationToken());
        assertThat(result.getQrCodeUrl()).isNotNull();
        assertThat(result.getQrCodeUrl()).contains("qr");
    }

    @Test
    @DisplayName("Should handle accepted invitation")
    void shouldHandleAcceptedInvitation() {
        // Given
        ReadingRoomInvitationEntity invitation = ReadingRoomInvitationEntityFixtures.createAcceptedInvitation(
                ReadingRoomEntityFixtures.createDefaultRoomEntity(),
                UserEntityFixtures.createDefaultUserEntity(),
                UserEntityFixtures.createSecondaryUserEntity()
        );

        // When
        InvitationResponse result = InvitationResponseFactory.createResponse(invitation, BASE_URL);

        // Then
        assertThat(result.getStatus()).isEqualTo(invitation.getStatus());
        assertThat(result.getAcceptedAt()).isEqualTo(invitation.getAcceptedAt());
        assertThat(result.getDeclinedAt()).isNull();
        assertThat(result.getInvitedUser()).isNotNull();
    }

    @Test
    @DisplayName("Should handle declined invitation")
    void shouldHandleDeclinedInvitation() {
        // Given
        ReadingRoomInvitationEntity invitation = ReadingRoomInvitationEntityFixtures.createDeclinedInvitation(
                ReadingRoomEntityFixtures.createDefaultRoomEntity(),
                UserEntityFixtures.createDefaultUserEntity(),
                UserEntityFixtures.createSecondaryUserEntity()
        );

        // When
        InvitationResponse result = InvitationResponseFactory.createResponse(invitation, BASE_URL);

        // Then
        assertThat(result.getStatus()).isEqualTo(invitation.getStatus());
        assertThat(result.getDeclinedAt()).isEqualTo(invitation.getDeclinedAt());
        assertThat(result.getAcceptedAt()).isNull();
    }

    @Test
    @DisplayName("Should handle expired invitation")
    void shouldHandleExpiredInvitation() {
        // Given
        ReadingRoomInvitationEntity invitation = ReadingRoomInvitationEntityFixtures.createExpiredInvitation(
                ReadingRoomEntityFixtures.createDefaultRoomEntity(),
                UserEntityFixtures.createDefaultUserEntity(),
                "expired@example.com"
        );

        // When
        InvitationResponse result = InvitationResponseFactory.createResponse(invitation, BASE_URL);

        // Then
        assertThat(result.getIsExpired()).isTrue();
        assertThat(result.getExpiresAt()).isBefore(Instant.now());
        assertThat(result.getInvitedEmail()).isEqualTo("expired@example.com");
    }

    @Test
    @DisplayName("Should handle invitation with null invited user")
    void shouldHandleInvitationWithNullInvitedUser() {
        // Given
        ReadingRoomInvitationEntity invitation = ReadingRoomInvitationEntityFixtures.createEmailInvitation(
                ReadingRoomEntityFixtures.createDefaultRoomEntity(),
                UserEntityFixtures.createDefaultUserEntity(),
                "email@example.com",
                Instant.now().plus(1, DAYS)
        );
        // Email invitations have null invitedUser

        // When
        InvitationResponse result = InvitationResponseFactory.createResponse(invitation, BASE_URL);

        // Then
        assertThat(result.getInvitedUser()).isNull();
        assertThat(result.getInvitedEmail()).isNotNull();
    }

    @Test
    @DisplayName("Should map invitation entity to response consistently")
    void shouldMapInvitationEntityToResponseConsistently() {
        // Given
        ReadingRoomInvitationEntity invitation = ReadingRoomInvitationEntityFixtures.createQRCodeInvitation(
                ReadingRoomEntityFixtures.createPrivateRoomEntity(),
                UserEntityFixtures.createSecondaryUserEntity(),
                Instant.now().plus(12, HOURS)
        );

        // When
        InvitationResponse response1 = InvitationResponseFactory.createResponse(invitation, BASE_URL);
        InvitationResponse response2 = InvitationResponseFactory.createResponse(invitation, BASE_URL);

        // Then
        assertThat(response1.getId()).isEqualTo(response2.getId());
        assertThat(response1.getInvitationToken()).isEqualTo(response2.getInvitationToken());
        assertThat(response1.getInvitationType()).isEqualTo(response2.getInvitationType());
        assertThat(response1.getShareLink()).isEqualTo(response2.getShareLink());
        assertThat(response1.getQrCodeUrl()).isEqualTo(response2.getQrCodeUrl());
    }

    @Test
    @DisplayName("Should use different base URLs correctly")
    void shouldUseDifferentBaseUrlsCorrectly() {
        // Given
        ReadingRoomInvitationEntity invitation = ReadingRoomInvitationEntityFixtures.createShareInvitation(
                ReadingRoomEntityFixtures.createDefaultRoomEntity(),
                UserEntityFixtures.createDefaultUserEntity(),
                Instant.now().plus(1, DAYS)
        );
        String baseUrl1 = "https://app.readtogether.com";
        String baseUrl2 = "https://staging.readtogether.com";

        // When
        InvitationResponse response1 = InvitationResponseFactory.createResponse(invitation, baseUrl1);
        InvitationResponse response2 = InvitationResponseFactory.createResponse(invitation, baseUrl2);

        // Then
        assertThat(response1.getShareLink()).contains(baseUrl1);
        assertThat(response2.getShareLink()).contains(baseUrl2);
        assertThat(response1.getShareLink()).isNotEqualTo(response2.getShareLink());
        // QR codes should also be different since they're based on different share links
        assertThat(response1.getQrCodeUrl()).isNotEqualTo(response2.getQrCodeUrl());
    }
}