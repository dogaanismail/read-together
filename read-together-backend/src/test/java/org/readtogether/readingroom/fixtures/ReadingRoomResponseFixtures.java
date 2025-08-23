package org.readtogether.readingroom.fixtures;

import lombok.experimental.UtilityClass;
import org.readtogether.readingroom.model.response.InvitationResponse;
import org.readtogether.readingroom.model.response.ReadingRoomResponse;
import org.readtogether.readingroom.model.response.RoomSettingsResponse;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.readtogether.readingroom.common.enums.InvitationStatus.*;
import static org.readtogether.readingroom.common.enums.InvitationType.*;
import static org.readtogether.readingroom.common.enums.RoomStatus.*;
import static org.readtogether.readingroom.common.enums.TranscriptionLanguage.*;

@UtilityClass
public class ReadingRoomResponseFixtures {

    public static ReadingRoomResponse createDefaultReadingRoomResponse() {

        return ReadingRoomResponse.builder()
                .id(UUID.fromString("550e8400-e29b-41d4-a716-446655440100"))
                .title("Test Reading Room")
                .description("A room for testing")
                .maxParticipants(12)
                .currentParticipants(3)
                .isPublic(true)
                .roomCode("ROOM001")
                .status(WAITING)
                .scheduledStartTime(null)
                .actualStartTime(null)
                .endTime(null)
                .host(createDefaultHostInfo())
                .createdAt(Instant.now().minus(1, ChronoUnit.HOURS))
                .build();
    }

    public static ReadingRoomResponse createActiveReadingRoomResponse() {

        return ReadingRoomResponse.builder()
                .id(UUID.fromString("550e8400-e29b-41d4-a716-446655440101"))
                .title("Active Reading Room")
                .description("Currently active room")
                .maxParticipants(10)
                .currentParticipants(5)
                .isPublic(true)
                .roomCode("ACTIVE01")
                .status(ACTIVE)
                .scheduledStartTime(Instant.now().minus(30, ChronoUnit.MINUTES))
                .actualStartTime(Instant.now().minus(25, ChronoUnit.MINUTES))
                .endTime(null)
                .host(createDefaultHostInfo())
                .createdAt(Instant.now().minus(2, ChronoUnit.HOURS))
                .build();
    }

    public static ReadingRoomResponse createPrivateReadingRoomResponse() {

        return ReadingRoomResponse.builder()
                .id(UUID.randomUUID())
                .title("Private Reading Room")
                .description("Private room with password")
                .maxParticipants(6)
                .currentParticipants(2)
                .isPublic(false)
                .roomCode("PRIV001")
                .status(WAITING)
                .scheduledStartTime(Instant.now().plus(1, ChronoUnit.HOURS))
                .actualStartTime(null)
                .endTime(null)
                .host(createSecondaryHostInfo())
                .createdAt(Instant.now().minus(30, ChronoUnit.MINUTES))
                .build();
    }

    public static ReadingRoomResponse.HostInfo createDefaultHostInfo() {

        return ReadingRoomResponse.HostInfo.builder()
                .id(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"))
                .firstName("John")
                .lastName("Doe")
                .email("test@example.com")
                .build();
    }

    public static ReadingRoomResponse.HostInfo createSecondaryHostInfo() {

        return ReadingRoomResponse.HostInfo.builder()
                .id(UUID.fromString("550e8400-e29b-41d4-a716-446655440001"))
                .firstName("Jane")
                .lastName("Smith")
                .email("jane@example.com")
                .build();
    }

    public static InvitationResponse createDefaultInvitationResponse() {

        return InvitationResponse.builder()
                .id(UUID.randomUUID())
                .readingRoomId(UUID.fromString("550e8400-e29b-41d4-a716-446655440100"))
                .roomTitle("Test Reading Room")
                .roomCode("ROOM001")
                .invitedBy(createDefaultInviterInfo())
                .invitedUser(null)
                .invitedEmail("invited@example.com")
                .invitationToken("invitation-token-123")
                .invitationType(EMAIL)
                .status(PENDING)
                .expiresAt(Instant.now().plus(1, ChronoUnit.HOURS))
                .acceptedAt(null)
                .declinedAt(null)
                .message("You're invited to join our reading room!")
                .shareLink(null)
                .qrCodeUrl(null)
                .createdAt(Instant.now().minus(10, ChronoUnit.MINUTES))
                .isExpired(false)
                .build();
    }

    public static InvitationResponse createDirectInvitationResponse() {

        return InvitationResponse.builder()
                .id(UUID.randomUUID())
                .readingRoomId(UUID.fromString("550e8400-e29b-41d4-a716-446655440100"))
                .roomTitle("Test Reading Room")
                .roomCode("ROOM001")
                .invitedBy(createDefaultInviterInfo())
                .invitedUser(createDefaultInvitedUserInfo())
                .invitedEmail(null)
                .invitationToken("direct-token-456")
                .invitationType(DIRECT_INVITE)
                .status(PENDING)
                .expiresAt(Instant.now().minus(2, ChronoUnit.DAYS))
                .acceptedAt(null)
                .declinedAt(null)
                .message("Direct invitation to reading room")
                .shareLink(null)
                .qrCodeUrl(null)
                .createdAt(Instant.now().minus(5, ChronoUnit.MINUTES))
                .isExpired(false)
                .build();
    }

    public static InvitationResponse createShareLinkInvitationResponse() {

        return InvitationResponse.builder()
                .id(UUID.randomUUID())
                .readingRoomId(UUID.fromString("550e8400-e29b-41d4-a716-446655440100"))
                .roomTitle("Test Reading Room")
                .roomCode("ROOM001")
                .invitedBy(createDefaultInviterInfo())
                .invitedUser(null)
                .invitedEmail(null)
                .invitationToken("share-token-789")
                .invitationType(LINK_SHARE)
                .status(PENDING)
                .expiresAt(Instant.now().plus(3, ChronoUnit.DAYS))
                .acceptedAt(null)
                .declinedAt(null)
                .message("Join our reading session!")
                .shareLink("https://app.readtogether.com/join?token=share-token-789")
                .qrCodeUrl(null)
                .createdAt(Instant.now().minus(15, ChronoUnit.MINUTES))
                .isExpired(false)
                .build();
    }

    public static InvitationResponse.InviterInfo createDefaultInviterInfo() {

        return InvitationResponse.InviterInfo.builder()
                .id(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"))
                .firstName("John")
                .lastName("Doe")
                .email("test@example.com")
                .build();
    }

    public static InvitationResponse.InvitedUserInfo createDefaultInvitedUserInfo() {

        return InvitationResponse.InvitedUserInfo.builder()
                .id(UUID.fromString("550e8400-e29b-41d4-a716-446655440001"))
                .firstName("Jane")
                .lastName("Smith")
                .email("jane@example.com")
                .build();
    }

    public static RoomSettingsResponse createDefaultRoomSettingsResponse() {
        return RoomSettingsResponse.builder()
                .id(UUID.randomUUID())
                .readingRoomId(UUID.fromString("550e8400-e29b-41d4-a716-446655440100"))
                .isPublic(true)
                .hasPassword(false)
                .requireHostApproval(false)
                .enableVideo(true)
                .enableAudio(true)
                .enableChat(true)
                .allowRecording(false)
                .autoMuteNewJoiners(true)
                .roomVolume(80)
                .enableLiveTranscription(false)
                .transcriptionLanguage(ENGLISH)
                .transcriptionLanguageDisplay("English")
                .enableSpeakerIdentification(true)
                .downloadableTranscripts(true)
                .pronunciationHelp(true)
                .build();
    }

    public static RoomSettingsResponse createPrivateRoomSettingsResponse() {

        return RoomSettingsResponse.builder()
                .id(UUID.randomUUID())
                .readingRoomId(UUID.fromString("550e8400-e29b-41d4-a716-446655440101"))
                .isPublic(false)
                .hasPassword(true)
                .requireHostApproval(true)
                .enableVideo(false)
                .enableAudio(true)
                .enableChat(true)
                .allowRecording(false)
                .autoMuteNewJoiners(true)
                .roomVolume(70)
                .enableLiveTranscription(true)
                .transcriptionLanguage(SPANISH)
                .transcriptionLanguageDisplay("Spanish")
                .enableSpeakerIdentification(false)
                .downloadableTranscripts(false)
                .pronunciationHelp(true)
                .build();
    }
}