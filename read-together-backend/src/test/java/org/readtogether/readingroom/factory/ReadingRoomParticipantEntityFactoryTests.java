package org.readtogether.readingroom.factory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.readtogether.readingroom.entity.ReadingRoomEntity;
import org.readtogether.readingroom.entity.ReadingRoomParticipantEntity;
import org.readtogether.readingroom.fixtures.ReadingRoomEntityFixtures;
import org.readtogether.user.entity.UserEntity;
import org.readtogether.user.fixtures.UserEntityFixtures;

import static org.assertj.core.api.Assertions.assertThat;
import static org.readtogether.readingroom.common.enums.ApprovalStatus.APPROVED;
import static org.readtogether.readingroom.common.enums.ParticipantStatus.JOINED;
import static org.readtogether.readingroom.common.enums.ParticipantStatus.LEFT;

@Tag("unit")
@DisplayName("ReadingRoomParticipantEntityFactory Tests")
class ReadingRoomParticipantEntityFactoryTests {

    @Test
    @DisplayName("Should create participant with auto mute flag")
    void shouldCreateParticipantWithAutoMuteFlag() {
        // Given
        ReadingRoomEntity room = ReadingRoomEntityFixtures.createDefaultRoomEntity();
        UserEntity user = UserEntityFixtures.createDefaultUserEntity();
        boolean autoMuteNewJoiners = true;

        // When
        ReadingRoomParticipantEntity result = ReadingRoomParticipantEntityFactory.createParticipant(
                room, user, autoMuteNewJoiners);

        // Then
        assertThat(result.getReadingRoom()).isEqualTo(room);
        assertThat(result.getUser()).isEqualTo(user);
        assertThat(result.getStatus()).isEqualTo(JOINED);
        assertThat(result.getJoinedAt()).isNotNull();
        assertThat(result.isMuted()).isTrue(); // auto-muted
        assertThat(result.isVideoEnabled()).isTrue();
        assertThat(result.isSpeaking()).isFalse();
        assertThat(result.getApprovalStatus()).isEqualTo(APPROVED);
        assertThat(result.getLeftAt()).isNull();
    }

    @Test
    @DisplayName("Should create participant without auto mute")
    void shouldCreateParticipantWithoutAutoMute() {
        // Given
        ReadingRoomEntity room = ReadingRoomEntityFixtures.createPrivateRoomEntity();
        UserEntity user = UserEntityFixtures.createSecondaryUserEntity();
        boolean autoMuteNewJoiners = false;

        // When
        ReadingRoomParticipantEntity result = ReadingRoomParticipantEntityFactory.createParticipant(
                room, user, autoMuteNewJoiners);

        // Then
        assertThat(result.getReadingRoom()).isEqualTo(room);
        assertThat(result.getUser()).isEqualTo(user);
        assertThat(result.getStatus()).isEqualTo(JOINED);
        assertThat(result.getJoinedAt()).isNotNull();
        assertThat(result.isMuted()).isFalse(); // not auto-muted
        assertThat(result.isVideoEnabled()).isTrue();
        assertThat(result.isSpeaking()).isFalse();
        assertThat(result.getApprovalStatus()).isEqualTo(APPROVED);
    }

    @Test
    @DisplayName("Should mark participant left")
    void shouldMarkParticipantLeft() {
        // Given
        ReadingRoomEntity room = ReadingRoomEntityFixtures.createActiveRoomEntity();
        UserEntity user = UserEntityFixtures.createDefaultUserEntity();
        ReadingRoomParticipantEntity participant = ReadingRoomParticipantEntityFactory.createParticipant(
                room, user, false);

        // Simulate a participant speaking before leaving
        participant.setSpeaking(true);

        // When
        ReadingRoomParticipantEntity result = ReadingRoomParticipantEntityFactory.participantLeft(participant);

        // Then
        assertThat(result.getStatus()).isEqualTo(LEFT);
        assertThat(result.getLeftAt()).isNotNull();
        assertThat(result.getJoinedAt()).isNotNull(); // should preserve join time

        // Note: The factory doesn't modify speaking status, that's handled elsewhere
        assertThat(result.isSpeaking()).isTrue(); // still true as factory doesn't change it
    }

    @Test
    @DisplayName("Should preserve participant identity when marked left")
    void shouldPreserveParticipantIdentityWhenMarkedLeft() {
        // Given
        ReadingRoomEntity room = ReadingRoomEntityFixtures.createDefaultRoomEntity();
        UserEntity user = UserEntityFixtures.createSecondaryUserEntity();
        ReadingRoomParticipantEntity participant = ReadingRoomParticipantEntityFactory.createParticipant(
                room, user, true);

        // Store original values
        boolean originalMuted = participant.isMuted();
        boolean originalVideoEnabled = participant.isVideoEnabled();

        // When
        ReadingRoomParticipantEntity result = ReadingRoomParticipantEntityFactory.participantLeft(participant);

        // Then
        assertThat(result).isSameAs(participant);
        assertThat(result.getReadingRoom()).isEqualTo(room);
        assertThat(result.getUser()).isEqualTo(user);
        assertThat(result.isMuted()).isEqualTo(originalMuted);
        assertThat(result.isVideoEnabled()).isEqualTo(originalVideoEnabled);
        assertThat(result.getApprovalStatus()).isEqualTo(APPROVED);
    }

    @Test
    @DisplayName("Should create multiple participants for same room")
    void shouldCreateMultipleParticipantsForSameRoom() {
        // Given
        ReadingRoomEntity room = ReadingRoomEntityFixtures.createDefaultRoomEntity();
        UserEntity user1 = UserEntityFixtures.createDefaultUserEntity();
        UserEntity user2 = UserEntityFixtures.createSecondaryUserEntity();

        // When
        ReadingRoomParticipantEntity participant1 = ReadingRoomParticipantEntityFactory.createParticipant(
                room, user1, true);
        ReadingRoomParticipantEntity participant2 = ReadingRoomParticipantEntityFactory.createParticipant(
                room, user2, false);

        // Then
        assertThat(participant1.getReadingRoom()).isEqualTo(room);
        assertThat(participant2.getReadingRoom()).isEqualTo(room);
        assertThat(participant1.getUser()).isEqualTo(user1);
        assertThat(participant2.getUser()).isEqualTo(user2);
        assertThat(participant1.isMuted()).isTrue();
        assertThat(participant2.isMuted()).isFalse();
        assertThat(participant1.getJoinedAt()).isNotNull();
        assertThat(participant2.getJoinedAt()).isNotNull();
    }
}