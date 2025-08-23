package org.readtogether.readingroom.factory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.readtogether.readingroom.entity.ReadingRoomEntity;
import org.readtogether.readingroom.fixtures.ReadingRoomEntityFixtures;
import org.readtogether.readingroom.model.response.ReadingRoomResponse;

import static org.assertj.core.api.Assertions.assertThat;

class ReadingRoomResponseFactoryTests {

    @Test
    @DisplayName("Should map entity to response")
    void shouldMapEntityToResponse() {
        // Given
        ReadingRoomEntity room = ReadingRoomEntityFixtures.createDefaultRoomEntity();
        Integer currentParticipants = 3;

        // When
        ReadingRoomResponse result = ReadingRoomResponseFactory.createResponse(room, currentParticipants);

        // Then
        assertThat(result.getId()).isEqualTo(room.getId());
        assertThat(result.getTitle()).isEqualTo(room.getTitle());
        assertThat(result.getDescription()).isEqualTo(room.getDescription());
        assertThat(result.getMaxParticipants()).isEqualTo(room.getMaxParticipants());
        assertThat(result.getCurrentParticipants()).isEqualTo(currentParticipants);
        assertThat(result.getIsPublic()).isEqualTo(room.isPublic());
        assertThat(result.getRoomCode()).isEqualTo(room.getRoomCode());
        assertThat(result.getStatus()).isEqualTo(room.getStatus());
        assertThat(result.getScheduledStartTime()).isEqualTo(room.getScheduledStartTime());
        assertThat(result.getActualStartTime()).isEqualTo(room.getActualStartTime());
        assertThat(result.getEndTime()).isEqualTo(room.getEndTime());
    }

    @Test
    @DisplayName("Should map host information correctly")
    void shouldMapHostInformationCorrectly() {
        // Given
        ReadingRoomEntity room = ReadingRoomEntityFixtures.createDefaultRoomEntity();
        Integer currentParticipants = 5;

        // When
        ReadingRoomResponse result = ReadingRoomResponseFactory.createResponse(room, currentParticipants);

        // Then
        assertThat(result.getHost()).isNotNull();
        assertThat(result.getHost().getId()).isEqualTo(room.getHost().getId());
        assertThat(result.getHost().getFirstName()).isEqualTo(room.getHost().getFirstName());
        assertThat(result.getHost().getLastName()).isEqualTo(room.getHost().getLastName());
        assertThat(result.getHost().getEmail()).isEqualTo(room.getHost().getEmail());
    }

    @Test
    @DisplayName("Should handle active room mapping")
    void shouldHandleActiveRoomMapping() {
        // Given
        ReadingRoomEntity activeRoom = ReadingRoomEntityFixtures.createActiveRoomEntity();
        Integer currentParticipants = 7;

        // When
        ReadingRoomResponse result = ReadingRoomResponseFactory.createResponse(activeRoom, currentParticipants);

        // Then
        assertThat(result.getStatus()).isEqualTo(activeRoom.getStatus());
        assertThat(result.getCurrentParticipants()).isEqualTo(7);
        assertThat(result.getScheduledStartTime()).isEqualTo(activeRoom.getScheduledStartTime());
        assertThat(result.getActualStartTime()).isEqualTo(activeRoom.getActualStartTime());
    }

    @Test
    @DisplayName("Should handle private room mapping")
    void shouldHandlePrivateRoomMapping() {
        // Given
        ReadingRoomEntity privateRoom = ReadingRoomEntityFixtures.createPrivateRoomEntity();
        Integer currentParticipants = 2;

        // When
        ReadingRoomResponse result = ReadingRoomResponseFactory.createResponse(privateRoom, currentParticipants);

        // Then
        assertThat(result.getIsPublic()).isFalse();
        assertThat(result.getCurrentParticipants()).isEqualTo(2);
        assertThat(result.getMaxParticipants()).isEqualTo(privateRoom.getMaxParticipants());
        assertThat(result.getHost().getId()).isEqualTo(privateRoom.getHost().getId());
    }

    @Test
    @DisplayName("Should handle room with no participants")
    void shouldHandleRoomWithNoParticipants() {
        // Given
        ReadingRoomEntity room = ReadingRoomEntityFixtures.createDefaultRoomEntity();
        Integer currentParticipants = 0;

        // When
        ReadingRoomResponse result = ReadingRoomResponseFactory.createResponse(room, currentParticipants);

        // Then
        assertThat(result.getCurrentParticipants()).isEqualTo(0);
        assertThat(result.getMaxParticipants()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Should handle completed room mapping")
    void shouldHandleCompletedRoomMapping() {
        // Given
        ReadingRoomEntity completedRoom = ReadingRoomEntityFixtures.createCompletedRoomEntity();
        Integer currentParticipants = 0;

        // When
        ReadingRoomResponse result = ReadingRoomResponseFactory.createResponse(completedRoom, currentParticipants);

        // Then
        assertThat(result.getStatus()).isEqualTo(completedRoom.getStatus());
        assertThat(result.getCurrentParticipants()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should handle room with null optional fields")
    void shouldHandleRoomWithNullOptionalFields() {
        // Given
        ReadingRoomEntity room = ReadingRoomEntityFixtures.createDefaultRoomEntity();
        // Default room has null scheduledStartTime, actualStartTime, endTime
        Integer currentParticipants = 1;

        // When
        ReadingRoomResponse result = ReadingRoomResponseFactory.createResponse(room, currentParticipants);

        // Then
        assertThat(result.getScheduledStartTime()).isNull();
        assertThat(result.getActualStartTime()).isNull();
        assertThat(result.getEndTime()).isNull();
        assertThat(result.getDescription()).isNotNull();
    }

    @Test
    @DisplayName("Should create consistent responses for same entity")
    void shouldCreateConsistentResponsesForSameEntity() {
        // Given
        ReadingRoomEntity room = ReadingRoomEntityFixtures.createDefaultRoomEntity();
        Integer currentParticipants = 4;

        // When
        ReadingRoomResponse response1 = ReadingRoomResponseFactory.createResponse(room, currentParticipants);
        ReadingRoomResponse response2 = ReadingRoomResponseFactory.createResponse(room, currentParticipants);

        // Then
        assertThat(response1.getId()).isEqualTo(response2.getId());
        assertThat(response1.getTitle()).isEqualTo(response2.getTitle());
        assertThat(response1.getRoomCode()).isEqualTo(response2.getRoomCode());
        assertThat(response1.getHost().getId()).isEqualTo(response2.getHost().getId());
        assertThat(response1.getCurrentParticipants()).isEqualTo(response2.getCurrentParticipants());
    }
}