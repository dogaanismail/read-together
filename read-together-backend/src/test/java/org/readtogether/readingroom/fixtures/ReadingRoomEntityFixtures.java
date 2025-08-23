package org.readtogether.readingroom.fixtures;

import lombok.experimental.UtilityClass;
import org.readtogether.readingroom.common.enums.RoomStatus;
import org.readtogether.readingroom.entity.ReadingRoomEntity;
import org.readtogether.user.entity.UserEntity;
import org.readtogether.user.fixtures.UserEntityFixtures;

import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.readtogether.readingroom.common.enums.RoomStatus.*;

@UtilityClass
public class ReadingRoomEntityFixtures {

    public static final UUID DEFAULT_ROOM_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440100");
    public static final UUID SECONDARY_ROOM_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440101");

    public static ReadingRoomEntity createDefaultRoomEntity() {

        return createRoomEntity(
                DEFAULT_ROOM_ID,
                UserEntityFixtures.createDefaultUserEntity(),
                "Default Reading Room",
                "A default room for testing",
                true,
                12,
                WAITING,
                "ROOM001",
                null
        );
    }

    public static ReadingRoomEntity createSecondaryRoomEntity() {

        return createRoomEntity(
                SECONDARY_ROOM_ID,
                UserEntityFixtures.createSecondaryUserEntity(),
                "Secondary Reading Room",
                "Another room for testing",
                false,
                8,
                ACTIVE,
                "ROOM002",
                Instant.now().plus(2, HOURS)
        );
    }

    public static ReadingRoomEntity createRoomEntity(
            UUID id,
            UserEntity host,
            String title,
            String description,
            boolean isPublic,
            int maxParticipants,
            RoomStatus status,
            String roomCode,
            Instant scheduledStartTime) {

        return ReadingRoomEntity.builder()
                .id(id)
                .host(host)
                .title(title)
                .description(description)
                .isPublic(isPublic)
                .maxParticipants(maxParticipants)
                .status(status)
                .roomCode(roomCode)
                .scheduledStartTime(scheduledStartTime)
                .participants(new ArrayList<>())
                .build();
    }

    public static ReadingRoomEntity createActiveRoomEntity() {

        return createRoomEntity(
                UUID.randomUUID(),
                UserEntityFixtures.createDefaultUserEntity(),
                "Active Reading Room",
                "Currently active room",
                true,
                10,
                ACTIVE,
                "ACTIVE01",
                Instant.now().minus(30, MINUTES)
        );
    }

    public static ReadingRoomEntity createPrivateRoomEntity() {

        return createRoomEntity(
                UUID.randomUUID(),
                UserEntityFixtures.createDefaultUserEntity(),
                "Private Reading Room",
                "Private room with password",
                false,
                6,
                WAITING,
                "PRIV001",
                Instant.now().plus(1, HOURS)
        );
    }

    public static ReadingRoomEntity createCompletedRoomEntity() {

        return createRoomEntity(
                UUID.randomUUID(),
                UserEntityFixtures.createDefaultUserEntity(),
                "Completed Reading Room",
                "Room that has finished",
                true,
                12,
                COMPLETED,
                "COMP001",
                Instant.now().minus(2, HOURS)
        );
    }

    public static ReadingRoomEntity createFullRoomEntity() {

        return createRoomEntity(
                UUID.randomUUID(),
                UserEntityFixtures.createDefaultUserEntity(),
                "Full Reading Room",
                "Room at capacity",
                true,
                2, // Small capacity for testing
                WAITING,
                "FULL001",
                Instant.now().plus(30, MINUTES)
        );
    }
}