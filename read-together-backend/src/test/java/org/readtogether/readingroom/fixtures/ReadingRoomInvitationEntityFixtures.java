package org.readtogether.readingroom.fixtures;

import lombok.experimental.UtilityClass;
import org.readtogether.readingroom.common.enums.InvitationStatus;
import org.readtogether.readingroom.common.enums.InvitationType;
import org.readtogether.readingroom.entity.ReadingRoomEntity;
import org.readtogether.readingroom.entity.ReadingRoomInvitationEntity;
import org.readtogether.user.entity.UserEntity;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.readtogether.readingroom.common.enums.InvitationStatus.*;
import static org.readtogether.readingroom.common.enums.InvitationType.*;

@UtilityClass
public class ReadingRoomInvitationEntityFixtures {

    public static ReadingRoomInvitationEntity createEmailInvitation(
            ReadingRoomEntity room,
            UserEntity inviter,
            String email,
            LocalDateTime expiresAt) {

        return createInvitationEntity(
                room,
                inviter,
                null,
                email,
                EMAIL,
                PENDING,
                "invitation-token-" + UUID.randomUUID(),
                expiresAt,
                null,
                null,
                "You're invited to join our reading room!"
        );
    }

    public static ReadingRoomInvitationEntity createDirectInvitation(
            ReadingRoomEntity room,
            UserEntity inviter,
            UserEntity invitedUser,
            LocalDateTime expiresAt) {

        return createInvitationEntity(
                room,
                inviter,
                invitedUser,
                null,
                DIRECT_INVITE,
                PENDING,
                "direct-token-" + UUID.randomUUID(),
                expiresAt,
                null,
                null,
                "Direct invitation to join reading room"
        );
    }

    public static ReadingRoomInvitationEntity createShareInvitation(
            ReadingRoomEntity room,
            UserEntity inviter,
            LocalDateTime expiresAt) {

        return createInvitationEntity(
                room,
                inviter,
                null,
                null,
                LINK_SHARE,
                PENDING,
                "share-token-" + UUID.randomUUID(),
                expiresAt,
                null,
                null,
                "Join our reading session!"
        );
    }

    public static ReadingRoomInvitationEntity createQRCodeInvitation(
            ReadingRoomEntity room,
            UserEntity inviter,
            LocalDateTime expiresAt) {

        return createInvitationEntity(
                room,
                inviter,
                null,
                null,
                QR_CODE,
                PENDING,
                "qr-token-" + UUID.randomUUID(),
                expiresAt,
                null,
                null,
                "Scan to join reading room"
        );
    }

    public static ReadingRoomInvitationEntity createAcceptedInvitation(
            ReadingRoomEntity room,
            UserEntity inviter,
            UserEntity invitedUser) {

        return createInvitationEntity(
                room,
                inviter,
                invitedUser,
                null,
                DIRECT_INVITE,
                ACCEPTED,
                "accepted-token-" + UUID.randomUUID(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().minusMinutes(5),
                null,
                "Accepted invitation"
        );
    }

    public static ReadingRoomInvitationEntity createExpiredInvitation(
            ReadingRoomEntity room,
            UserEntity inviter,
            String email) {

        return createInvitationEntity(
                room,
                inviter,
                null,
                email,
                EMAIL,
                EXPIRED,
                "expired-token-" + UUID.randomUUID(),
                LocalDateTime.now().minusDays(1),
                null,
                null,
                "Expired invitation"
        );
    }

    public static ReadingRoomInvitationEntity createDeclinedInvitation(
            ReadingRoomEntity room,
            UserEntity inviter,
            UserEntity invitedUser) {

        return createInvitationEntity(
                room,
                inviter,
                invitedUser,
                null,
                DIRECT_INVITE,
                DECLINED,
                "declined-token-" + UUID.randomUUID(),
                LocalDateTime.now().plusDays(1),
                null,
                LocalDateTime.now().minusMinutes(10),
                "Declined invitation"
        );
    }

    public static ReadingRoomInvitationEntity createInvitationEntity(
            ReadingRoomEntity room,
            UserEntity inviter,
            UserEntity invitedUser,
            String invitedEmail,
            InvitationType invitationType,
            InvitationStatus status,
            String invitationToken,
            LocalDateTime expiresAt,
            LocalDateTime acceptedAt,
            LocalDateTime declinedAt,
            String message) {

        return ReadingRoomInvitationEntity.builder()
                .id(UUID.randomUUID())
                .readingRoom(room)
                .invitedBy(inviter)
                .invitedUser(invitedUser)
                .invitedEmail(invitedEmail)
                .invitationType(invitationType)
                .status(status)
                .invitationToken(invitationToken)
                .expiresAt(expiresAt)
                .acceptedAt(acceptedAt)
                .declinedAt(declinedAt)
                .message(message)
                .build();
    }

    public static ReadingRoomInvitationEntity createCustomInvitation(
            ReadingRoomEntity room,
            UserEntity inviter,
            InvitationType type,
            String token,
            int expirationHours) {

        return createInvitationEntity(
                room,
                inviter,
                null,
                type == EMAIL ? "test@example.com" : null,
                type,
                PENDING,
                token,
                LocalDateTime.now().plusHours(expirationHours),
                null,
                null,
                "Custom test invitation"
        );
    }
}