package org.readtogether.readingroom.factory;

import lombok.experimental.UtilityClass;
import org.readtogether.readingroom.common.enums.RoomStatus;
import org.readtogether.readingroom.entity.ReadingRoomEntity;
import org.readtogether.readingroom.model.response.ReadingRoomResponse;

import java.time.LocalDateTime;

@UtilityClass
public class ReadingRoomResponseFactory {

    public ReadingRoomResponse createResponse(
            ReadingRoomEntity room,
            Integer currentParticipants) {

        return ReadingRoomResponse.builder()
                .id(room.getId())
                .title(room.getTitle())
                .description(room.getDescription())
                .maxParticipants(room.getMaxParticipants())
                .currentParticipants(currentParticipants)
                .isPublic(room.isPublic())
                .roomCode(room.getRoomCode())
                .status(RoomStatus.valueOf(room.getStatus().name()))
                .scheduledStartTime(room.getScheduledStartTime())
                .actualStartTime(room.getActualStartTime())
                .endTime(room.getEndTime())
                .host(createHostInfo(room))
                .createdAt(LocalDateTime.from(room.getCreatedAt()))
                .build();
    }

    private ReadingRoomResponse.HostInfo createHostInfo(ReadingRoomEntity room) {
        return ReadingRoomResponse.HostInfo.builder()
                .id(room.getHost().getId())
                .firstName(room.getHost().getFirstName())
                .lastName(room.getHost().getLastName())
                .email(room.getHost().getEmail())
                .build();
    }
}
