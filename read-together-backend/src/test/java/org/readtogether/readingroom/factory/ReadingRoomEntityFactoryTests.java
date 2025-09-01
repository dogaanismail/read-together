package org.readtogether.readingroom.factory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.readtogether.readingroom.entity.ReadingRoomEntity;
import org.readtogether.readingroom.fixtures.ReadingRoomRequestFixtures;
import org.readtogether.readingroom.model.request.CreateReadingRoomRequest;
import org.readtogether.user.entity.UserEntity;
import org.readtogether.user.fixtures.UserEntityFixtures;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.HOURS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.readtogether.readingroom.common.enums.RoomStatus.ACTIVE;

@Tag("unit")
@DisplayName("ReadingRoomEntityFactory Tests")
class ReadingRoomEntityFactoryTests {

    @Test
    @DisplayName("Should create room entity from create request")
    void shouldCreateRoomEntityFromCreateRequest() {
        // Given
        CreateReadingRoomRequest request = ReadingRoomRequestFixtures.createDefaultCreateReadingRoomRequest();
        UserEntity host = UserEntityFixtures.createDefaultUserEntity();

        // When
        ReadingRoomEntity result = ReadingRoomEntityFactory.createReadingRoomEntity(request, host);

        // Then
        assertThat(result.getTitle()).isEqualTo(request.getTitle());
        assertThat(result.getDescription()).isEqualTo(request.getDescription());
        assertThat(result.getMaxParticipants()).isEqualTo(request.getMaxParticipants());
        assertThat(result.isPublic()).isEqualTo(request.isPublic());
        assertThat(result.getScheduledStartTime()).isEqualTo(request.getScheduledStartTime());
        assertThat(result.getHost()).isEqualTo(host);
        assertThat(result.getStatus()).isEqualTo(ACTIVE);
        assertThat(result.getRoomCode()).isNotNull();
        assertThat(result.getRoomCode()).matches("[A-Z0-9]+");
    }

    @Test
    @DisplayName("Should default unset fields when creating from request")
    void shouldDefaultUnsetFields() {
        // Given
        CreateReadingRoomRequest request = ReadingRoomRequestFixtures.createMinimalCreateRequest();
        UserEntity host = UserEntityFixtures.createDefaultUserEntity();

        // When
        ReadingRoomEntity result = ReadingRoomEntityFactory.createReadingRoomEntity(request, host);

        // Then
        assertThat(result.getTitle()).isEqualTo("Minimal Room");
        assertThat(result.getDescription()).isNull();
        assertThat(result.getMaxParticipants()).isEqualTo(12);
        assertThat(result.isPublic()).isTrue();
        assertThat(result.getScheduledStartTime()).isNull();
        assertThat(result.getStatus()).isEqualTo(ACTIVE);
        assertThat(result.getRoomCode()).isNotNull();
    }

    @Test
    @DisplayName("Should handle private room creation")
    void shouldHandlePrivateRoomCreation() {
        // Given
        CreateReadingRoomRequest request = ReadingRoomRequestFixtures.createPrivateCreateReadingRoomRequest();
        UserEntity host = UserEntityFixtures.createSecondaryUserEntity();

        // When
        ReadingRoomEntity result = ReadingRoomEntityFactory.createReadingRoomEntity(request, host);

        // Then
        assertThat(result.getTitle()).isEqualTo("Private Reading Room");
        assertThat(result.getDescription()).isEqualTo("Private room for testing");
        assertThat(result.getMaxParticipants()).isEqualTo(6);
        assertThat(result.isPublic()).isFalse();
        assertThat(result.getScheduledStartTime()).isNotNull();
        assertThat(result.getHost()).isEqualTo(host);
    }

    @Test
    @DisplayName("Should create room with custom values")
    void shouldCreateRoomWithCustomValues() {
        // Given
        Instant scheduledTime = Instant.now().plus(3, HOURS);
        CreateReadingRoomRequest request = ReadingRoomRequestFixtures.createCreateReadingRoomRequest(
                "Custom Room",
                "Custom description",
                false,
                8,
                scheduledTime
        );
        UserEntity host = UserEntityFixtures.createDefaultUserEntity();

        // When
        ReadingRoomEntity result = ReadingRoomEntityFactory.createReadingRoomEntity(request, host);

        // Then
        assertThat(result.getTitle()).isEqualTo("Custom Room");
        assertThat(result.getDescription()).isEqualTo("Custom description");
        assertThat(result.getMaxParticipants()).isEqualTo(8);
        assertThat(result.isPublic()).isFalse();
        assertThat(result.getScheduledStartTime()).isEqualTo(scheduledTime);
        assertThat(result.getHost()).isEqualTo(host);
        assertThat(result.getStatus()).isEqualTo(ACTIVE);
    }

    @Test
    @DisplayName("Should generate unique room codes")
    void shouldGenerateUniqueRoomCodes() {
        // Given
        CreateReadingRoomRequest request = ReadingRoomRequestFixtures.createDefaultCreateReadingRoomRequest();
        UserEntity host = UserEntityFixtures.createDefaultUserEntity();

        // When
        ReadingRoomEntity room1 = ReadingRoomEntityFactory.createReadingRoomEntity(request, host);
        ReadingRoomEntity room2 = ReadingRoomEntityFactory.createReadingRoomEntity(request, host);

        // Then
        assertThat(room1.getRoomCode()).isNotNull();
        assertThat(room2.getRoomCode()).isNotNull();
        assertThat(room1.getRoomCode()).isNotEqualTo(room2.getRoomCode());
    }
}