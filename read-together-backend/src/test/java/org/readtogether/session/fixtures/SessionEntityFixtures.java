package org.readtogether.session.fixtures;

import lombok.experimental.UtilityClass;
import org.readtogether.session.common.enums.MediaType;
import org.readtogether.session.common.enums.ProcessingStatus;
import org.readtogether.session.entity.SessionEntity;

import java.time.Instant;
import java.util.UUID;

import static org.readtogether.session.common.enums.MediaType.AUDIO;
import static org.readtogether.session.common.enums.MediaType.VIDEO;
import static org.readtogether.session.common.enums.ProcessingStatus.*;

@UtilityClass
public class SessionEntityFixtures {

    public static final UUID DEFAULT_SESSION_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    public static final UUID SECONDARY_SESSION_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
    public static final UUID DEFAULT_USER_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    public static final UUID SECONDARY_USER_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
    public static final UUID DEFAULT_READING_ROOM_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440002");

    public static SessionEntity createDefaultSessionEntity() {

        return createSessionEntity(
                DEFAULT_SESSION_ID,
                DEFAULT_USER_ID,
                "Default Session Title",
                "Default session description",
                AUDIO,
                COMPLETED,
                true
        );
    }

    public static SessionEntity createSecondarySessionEntity() {

        return createSessionEntity(
                SECONDARY_SESSION_ID,
                SECONDARY_USER_ID,
                "Secondary Session Title",
                "Secondary session description",
                VIDEO,
                PENDING,
                false
        );
    }

    public static SessionEntity createSessionEntity(
            UUID id,
            UUID userId,
            String title,
            String description,
            MediaType mediaType,
            ProcessingStatus status,
            boolean isPublic) {

        return SessionEntity.builder()
                .id(id)
                .userId(userId)
                .title(title)
                .description(description)
                .mediaUrl("https://example.com/media/" + id)
                .mediaType(mediaType)
                .durationSeconds(120)
                .fileSizeBytes(1024000L)
                .mimeType(mediaType == AUDIO ? "audio/mpeg" : "video/mp4")
                .processingStatus(status)
                .isPublic(isPublic)
                .readingRoomId(DEFAULT_READING_ROOM_ID)
                .transcript("Sample transcript text")
                .tags("reading,practice,speech")
                .thumbnailUrl("https://example.com/thumbnails/" + id + ".jpg")
                .bookTitle("Sample Book Title")
                .language("en")
                .authorName("Sample Author")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    public static SessionEntity createPendingSessionEntity() {

        return createSessionEntity(
                UUID.randomUUID(),
                DEFAULT_USER_ID,
                "Pending Session",
                "A session that is pending processing",
                AUDIO,
                PENDING,
                false
        );
    }

    public static SessionEntity createProcessingSessionEntity() {

        return createSessionEntity(
                UUID.randomUUID(),
                DEFAULT_USER_ID,
                "Processing Session",
                "A session that is currently processing",
                VIDEO,
                PROCESSING,
                true
        );
    }

    public static SessionEntity createFailedSessionEntity() {

        return SessionEntity.builder()
                .id(UUID.randomUUID())
                .userId(DEFAULT_USER_ID)
                .title("Failed Session")
                .description("A session that failed processing")
                .mediaUrl("https://example.com/media/failed")
                .mediaType(AUDIO)
                .durationSeconds(0)
                .fileSizeBytes(512000L)
                .mimeType("audio/mpeg")
                .processingStatus(FAILED)
                .processingError("File format not supported")
                .isPublic(false)
                .tags("failed,test")
                .language("en")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    public static SessionEntity createPublicVideoSessionEntity() {

        return createSessionEntity(
                UUID.randomUUID(),
                SECONDARY_USER_ID,
                "Public Video Session",
                "A public video session for testing",
                VIDEO,
                COMPLETED,
                true
        );
    }

    public static SessionEntity createPrivateAudioSessionEntity() {

        return createSessionEntity(
                UUID.randomUUID(),
                DEFAULT_USER_ID,
                "Private Audio Session",
                "A private audio session for testing",
                AUDIO,
                COMPLETED,
                false
        );
    }
}