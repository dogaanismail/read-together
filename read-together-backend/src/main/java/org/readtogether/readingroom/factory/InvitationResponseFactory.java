package org.readtogether.readingroom.factory;

import lombok.experimental.UtilityClass;
import org.readtogether.readingroom.entity.ReadingRoomInvitationEntity;
import org.readtogether.readingroom.model.response.InvitationResponse;
import org.readtogether.readingroom.utils.ReadingRoomUtils;

import java.time.LocalDateTime;

@UtilityClass
public class InvitationResponseFactory {

    public InvitationResponse createResponse(
            ReadingRoomInvitationEntity invitation,
            String baseUrl) {

        String shareLink = ReadingRoomUtils.generateShareLink(baseUrl, invitation.getInvitationToken());
        String qrCodeUrl = ReadingRoomUtils.generateQRCodeUrl(shareLink);

        return InvitationResponse.builder()
                .id(invitation.getId())
                .readingRoomId(invitation.getReadingRoom().getId())
                .roomTitle(invitation.getReadingRoom().getTitle())
                .roomCode(invitation.getReadingRoom().getRoomCode())
                .invitedBy(createInviterInfo(invitation))
                .invitedUser(createInvitedUserInfo(invitation))
                .invitedEmail(invitation.getInvitedEmail())
                .invitationToken(invitation.getInvitationToken())
                .invitationType(invitation.getInvitationType())
                .status(invitation.getStatus())
                .expiresAt(invitation.getExpiresAt())
                .acceptedAt(invitation.getAcceptedAt())
                .declinedAt(invitation.getDeclinedAt())
                .message(invitation.getMessage())
                .shareLink(shareLink)
                .qrCodeUrl(qrCodeUrl)
                .createdAt(invitation.getCreatedAt())
                .isExpired(invitation.isExpired())
                .build();
    }

    private InvitationResponse.InviterInfo createInviterInfo(ReadingRoomInvitationEntity invitation) {

        return InvitationResponse.InviterInfo.builder()
                .id(invitation.getInvitedBy().getId())
                .firstName(invitation.getInvitedBy().getFirstName())
                .lastName(invitation.getInvitedBy().getLastName())
                .email(invitation.getInvitedBy().getEmail())
                .build();
    }

    private InvitationResponse.InvitedUserInfo createInvitedUserInfo(ReadingRoomInvitationEntity invitation) {

        if (invitation.getInvitedUser() == null) {
            return null;
        }

        return InvitationResponse.InvitedUserInfo.builder()
                .id(invitation.getInvitedUser().getId())
                .firstName(invitation.getInvitedUser().getFirstName())
                .lastName(invitation.getInvitedUser().getLastName())
                .email(invitation.getInvitedUser().getEmail())
                .build();
    }
}
