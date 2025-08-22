package org.readtogether.session.factory;

import lombok.experimental.UtilityClass;
import org.readtogether.session.entity.SessionEntity;
import org.readtogether.session.model.request.SessionCreateRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static org.readtogether.session.common.enums.ProcessingStatus.PENDING;

@UtilityClass
public class SessionEntityFactory {

    public static SessionEntity createFromRequest(
            UUID userId,
            SessionCreateRequest request,
            MultipartFile file) {

        return SessionEntity.builder()
                .userId(userId)
                .title(request.getTitle())
                .description(request.getDescription())
                .mediaType(request.getMediaType())
                .mimeType(file.getContentType())
                .fileSizeBytes(file.getSize())
                .isPublic(request.isPublic())
                .readingRoomId(request.getReadingRoomId())
                .tags(request.getTags())
                .build();
    }

    public static SessionEntity createPendingSession(
            UUID userId,
            SessionCreateRequest request,
            MultipartFile file) {

        return SessionEntity.builder()
                .userId(userId)
                .title(request.getTitle())
                .description(request.getDescription())
                .mediaType(request.getMediaType())
                .mimeType(file.getContentType())
                .fileSizeBytes(file.getSize())
                .isPublic(request.isPublic())
                .readingRoomId(request.getReadingRoomId())
                .tags(request.getTags())
                .processingStatus(PENDING)
                .build();
    }
}
