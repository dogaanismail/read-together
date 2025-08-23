package org.readtogether.readingroom.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.readtogether.readingroom.common.enums.InvitationStatus;
import org.readtogether.readingroom.common.enums.InvitationType;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvitationResponse {

    private UUID id;
    private UUID readingRoomId;
    private String roomTitle;
    private String roomCode;
    private InviterInfo invitedBy;
    private InvitedUserInfo invitedUser;
    private String invitedEmail;
    private String invitationToken;
    private InvitationType invitationType;
    private InvitationStatus status;
    private Instant expiresAt;
    private Instant acceptedAt;
    private Instant declinedAt;
    private String message;
    private String shareLink;
    private String qrCodeUrl;
    private Instant createdAt;
    private Boolean isExpired;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InviterInfo {
        private UUID id;
        private String firstName;
        private String lastName;
        private String email;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InvitedUserInfo {
        private UUID id;
        private String firstName;
        private String lastName;
        private String email;
    }
}
