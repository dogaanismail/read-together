package org.readtogether.readingroom.factory;

import lombok.experimental.UtilityClass;
import org.readtogether.readingroom.entity.ReadingRoomEntity;
import org.readtogether.readingroom.model.request.CreateReadingRoomRequest;
import org.readtogether.readingroom.utils.ReadingRoomUtils;
import org.readtogether.user.entity.UserEntity;

import static org.readtogether.readingroom.common.enums.RoomStatus.ACTIVE;

@UtilityClass
public class ReadingRoomEntityFactory {

    public ReadingRoomEntity createReadingRoomEntity(
            CreateReadingRoomRequest createReadingRoomRequest,
            UserEntity host) {

        return ReadingRoomEntity.builder()
                .title(createReadingRoomRequest.getTitle())
                .description(createReadingRoomRequest.getDescription())
                .maxParticipants(createReadingRoomRequest.getMaxParticipants())
                .isPublic(createReadingRoomRequest.isPublic())
                .roomCode(ReadingRoomUtils.generateRoomCode())
                .scheduledStartTime(createReadingRoomRequest.getScheduledStartTime())
                .host(host)
                .status(ACTIVE)
                .build();

    }
}
