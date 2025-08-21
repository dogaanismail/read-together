package org.readtogether.readingroom.factory;

import lombok.experimental.UtilityClass;
import org.readtogether.readingroom.entity.ReadingRoomEntity;
import org.readtogether.readingroom.entity.ReadingRoomInvitationEntity;
import org.readtogether.readingroom.model.request.InviteToRoomRequest;
import org.readtogether.readingroom.utils.ReadingRoomUtils;
import org.readtogether.user.entity.UserEntity;

import java.time.LocalDateTime;

import static org.readtogether.readingroom.common.enums.InvitationStatus.*;
import static org.readtogether.readingroom.common.enums.InvitationType.*;
import static org.readtogether.readingroom.common.enums.InvitationType.QR_CODE;

@UtilityClass
public class ReadingRoomInvitationEntityFactory {

    public ReadingRoomInvitationEntity createShareInvitation(
            ReadingRoomEntity room,
            UserEntity inviter,
            InviteToRoomRequest request,
            LocalDateTime expiresAt) {

        return switch (request.getInvitationType()) {
            case LINK_SHARE -> createShareLinkInvitation(
                    room, inviter, request.getMessage(), expiresAt);
            case QR_CODE -> createQRCodeInvitation(
                    room, inviter, request.getMessage(), expiresAt);
            default -> throw new RuntimeException("Unsupported share invitation type: " + request.getInvitationType());
        };
    }

    public ReadingRoomInvitationEntity createEmailInvitation(
            ReadingRoomEntity readingRoom,
            UserEntity inviter,
            String invitedEmail,
            String message,
            LocalDateTime expiresAt) {

        return ReadingRoomInvitationEntity.builder()
                .readingRoom(readingRoom)
                .invitedBy(inviter)
                .invitedEmail(invitedEmail)
                .invitationToken(ReadingRoomUtils.generateInvitationToken())
                .invitationType(EMAIL)
                .status(PENDING)
                .expiresAt(expiresAt)
                .message(message)
                .build();
    }

    public ReadingRoomInvitationEntity createDirectInvitation(
            ReadingRoomEntity readingRoom,
            UserEntity inviter,
            UserEntity invitedUser,
            String message,
            LocalDateTime expiresAt) {

        return ReadingRoomInvitationEntity.builder()
                .readingRoom(readingRoom)
                .invitedBy(inviter)
                .invitedUser(invitedUser)
                .invitedEmail(invitedUser.getEmail())
                .invitationToken(ReadingRoomUtils.generateInvitationToken())
                .invitationType(DIRECT_INVITE)
                .status(PENDING)
                .expiresAt(expiresAt)
                .message(message)
                .build();
    }

    public ReadingRoomInvitationEntity createShareLinkInvitation(
            ReadingRoomEntity readingRoom,
            UserEntity inviter,
            String message,
            LocalDateTime expiresAt) {

        return ReadingRoomInvitationEntity.builder()
                .readingRoom(readingRoom)
                .invitedBy(inviter)
                .invitationToken(ReadingRoomUtils.generateInvitationToken())
                .invitationType(LINK_SHARE)
                .status(PENDING)
                .expiresAt(expiresAt)
                .message(message)
                .build();
    }

    public ReadingRoomInvitationEntity createQRCodeInvitation(
            ReadingRoomEntity readingRoom,
            UserEntity inviter,
            String message,
            LocalDateTime expiresAt) {

        return ReadingRoomInvitationEntity.builder()
                .readingRoom(readingRoom)
                .invitedBy(inviter)
                .invitationToken(ReadingRoomUtils.generateInvitationToken())
                .invitationType(QR_CODE)
                .status(PENDING)
                .expiresAt(expiresAt)
                .message(message)
                .build();
    }

    public ReadingRoomInvitationEntity createAcceptInvitationEntity(
            ReadingRoomInvitationEntity invitation,
            UserEntity acceptingUser) {

        invitation.setStatus(ACCEPTED);
        invitation.setAcceptedAt(LocalDateTime.now());

        if (invitation.getInvitedUser() == null && acceptingUser != null) {
            invitation.setInvitedUser(acceptingUser);
        }

        return invitation;
    }

    public ReadingRoomInvitationEntity createDeclineInvitationEntity(ReadingRoomInvitationEntity invitation) {

        invitation.setStatus(DECLINED);
        invitation.setDeclinedAt(LocalDateTime.now());
        return invitation;
    }
}
