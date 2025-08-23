package org.readtogether.readingroom.fixtures;

import lombok.experimental.UtilityClass;
import org.readtogether.readingroom.common.enums.InvitationType;
import org.readtogether.readingroom.common.enums.TranscriptionLanguage;
import org.readtogether.readingroom.model.request.CreateReadingRoomRequest;
import org.readtogether.readingroom.model.request.InviteToRoomRequest;
import org.readtogether.readingroom.model.request.JoinRoomRequest;
import org.readtogether.readingroom.model.request.UpdateRoomSettingsRequest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.readtogether.readingroom.common.enums.InvitationType.*;
import static org.readtogether.readingroom.common.enums.TranscriptionLanguage.*;

@UtilityClass
public class ReadingRoomRequestFixtures {

    public static CreateReadingRoomRequest createDefaultCreateReadingRoomRequest() {
        return CreateReadingRoomRequest.builder()
                .title("Test Reading Room")
                .description("A room for testing purposes")
                .maxParticipants(12)
                .isPublic(true)
                .scheduledStartTime(null)
                .build();
    }

    public static CreateReadingRoomRequest createPrivateCreateReadingRoomRequest() {
        return CreateReadingRoomRequest.builder()
                .title("Private Reading Room")
                .description("Private room for testing")
                .maxParticipants(6)
                .isPublic(false)
                .scheduledStartTime(LocalDateTime.now().plusHours(2))
                .build();
    }

    public static CreateReadingRoomRequest createCreateReadingRoomRequest(
            String title,
            String description,
            boolean isPublic,
            int maxParticipants,
            LocalDateTime scheduledStartTime) {

        return CreateReadingRoomRequest.builder()
                .title(title)
                .description(description)
                .isPublic(isPublic)
                .maxParticipants(maxParticipants)
                .scheduledStartTime(scheduledStartTime)
                .build();
    }

    public static CreateReadingRoomRequest createMinimalCreateRequest() {
        return CreateReadingRoomRequest.builder()
                .title("Minimal Room")
                .build();
    }

    public static JoinRoomRequest createDefaultJoinRoomRequest() {
        return JoinRoomRequest.builder()
                .roomCode("TEST001")
                .password(null)
                .build();
    }

    public static JoinRoomRequest createJoinRoomRequest(String roomCode, String password) {
        return JoinRoomRequest.builder()
                .roomCode(roomCode)
                .password(password)
                .build();
    }

    public static JoinRoomRequest createPasswordProtectedJoinRequest() {
        return JoinRoomRequest.builder()
                .roomCode("PRIV001")
                .password("testPassword123")
                .build();
    }

    public static InviteToRoomRequest createDefaultEmailInviteRequest() {
        return InviteToRoomRequest.builder()
                .invitationType(EMAIL)
                .invitedEmails(Arrays.asList("test1@example.com", "test2@example.com"))
                .message("Join our reading room!")
                .expirationHours(24)
                .build();
    }

    public static InviteToRoomRequest createDirectInviteRequest() {
        return InviteToRoomRequest.builder()
                .invitationType(DIRECT_INVITE)
                .invitedUserIds(Arrays.asList(UUID.randomUUID(), UUID.randomUUID()))
                .message("Direct invitation to reading room")
                .expirationHours(48)
                .build();
    }

    public static InviteToRoomRequest createShareLinkInviteRequest() {
        return InviteToRoomRequest.builder()
                .invitationType(LINK_SHARE)
                .message("Share this link to join!")
                .expirationHours(72)
                .generateNewToken(false)
                .build();
    }

    public static InviteToRoomRequest createQRCodeInviteRequest() {
        return InviteToRoomRequest.builder()
                .invitationType(QR_CODE)
                .message("Scan QR code to join")
                .expirationHours(12)
                .generateNewToken(true)
                .build();
    }

    public static InviteToRoomRequest createInviteToRoomRequest(
            InvitationType type,
            List<String> emails,
            List<UUID> userIds,
            String message,
            int expirationHours) {

        return InviteToRoomRequest.builder()
                .invitationType(type)
                .invitedEmails(emails)
                .invitedUserIds(userIds)
                .message(message)
                .expirationHours(expirationHours)
                .generateNewToken(false)
                .build();
    }

    public static UpdateRoomSettingsRequest createDefaultUpdateRoomSettingsRequest() {
        return UpdateRoomSettingsRequest.builder()
                .isPublic(true)
                .requireHostApproval(false)
                .enableChat(true)
                .enableAudio(true)
                .enableVideo(true)
                .autoMuteNewJoiners(true)
                .roomVolume(80)
                .enableLiveTranscription(false)
                .transcriptionLanguage(ENGLISH)
                .enableSpeakerIdentification(true)
                .downloadableTranscripts(true)
                .pronunciationHelp(true)
                .build();
    }

    public static UpdateRoomSettingsRequest createPrivateUpdateRoomSettingsRequest() {
        return UpdateRoomSettingsRequest.builder()
                .isPublic(false)
                .password("newPassword123")
                .requireHostApproval(true)
                .enableChat(true)
                .enableAudio(true)
                .enableVideo(false)
                .autoMuteNewJoiners(true)
                .roomVolume(70)
                .enableLiveTranscription(true)
                .transcriptionLanguage(SPANISH)
                .enableSpeakerIdentification(false)
                .downloadableTranscripts(false)
                .pronunciationHelp(true)
                .build();
    }

    public static UpdateRoomSettingsRequest createUpdateRoomSettingsRequest(
            Boolean isPublic,
            Boolean requireHostApproval,
            Boolean enableChat,
            Boolean enableAudio,
            Boolean enableVideo,
            Boolean autoMuteNewJoiners,
            String password,
            Integer roomVolume) {

        return UpdateRoomSettingsRequest.builder()
                .isPublic(isPublic)
                .requireHostApproval(requireHostApproval)
                .enableChat(enableChat)
                .enableAudio(enableAudio)
                .enableVideo(enableVideo)
                .autoMuteNewJoiners(autoMuteNewJoiners)
                .password(password)
                .roomVolume(roomVolume)
                .enableLiveTranscription(false)
                .transcriptionLanguage(ENGLISH)
                .enableSpeakerIdentification(true)
                .downloadableTranscripts(true)
                .pronunciationHelp(true)
                .build();
    }

    public static UpdateRoomSettingsRequest createRestrictiveSettingsRequest() {
        return UpdateRoomSettingsRequest.builder()
                .isPublic(false)
                .password("verySecurePassword")
                .requireHostApproval(true)
                .enableChat(false)
                .enableAudio(false)
                .enableVideo(false)
                .autoMuteNewJoiners(true)
                .roomVolume(50)
                .enableLiveTranscription(false)
                .transcriptionLanguage(ENGLISH)
                .enableSpeakerIdentification(false)
                .downloadableTranscripts(false)
                .pronunciationHelp(false)
                .build();
    }
}