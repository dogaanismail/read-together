package org.readtogether.session.factory;

import lombok.experimental.UtilityClass;
import org.readtogether.common.utils.FileUtils;
import org.readtogether.common.utils.TimeUtils;
import org.readtogether.session.entity.SessionEntity;
import org.readtogether.session.model.SessionResponse;

@UtilityClass
public class SessionResponseFactory {

    public static SessionResponse createFromEntity(SessionEntity session) {

        SessionResponse response = new SessionResponse();
        response.setId(session.getId());
        response.setUserId(session.getUserId());
        response.setTitle(session.getTitle());
        response.setDescription(session.getDescription());
        response.setMediaUrl(session.getMediaUrl());
        response.setMediaType(session.getMediaType());
        response.setDurationSeconds(session.getDurationSeconds());
        response.setFileSizeBytes(session.getFileSizeBytes());
        response.setMimeType(session.getMimeType());
        response.setProcessingStatus(session.getProcessingStatus());
        response.setProcessingError(session.getProcessingError());
        response.setPublic(session.isPublic());
        response.setViewCount(session.getViewCount());
        response.setLikeCount(session.getLikeCount());
        response.setReadingRoomId(session.getReadingRoomId());
        response.setTranscript(session.getTranscript());
        response.setCreatedAt(session.getCreatedAt());
        response.setUpdatedAt(session.getUpdatedAt());

        if (session.getTags() != null && !session.getTags().isEmpty()) {
            response.setTags(session.getTags().split(","));
        }

        response.setFormattedDuration(TimeUtils.formatDuration(session.getDurationSeconds()));
        response.setFormattedFileSize(FileUtils.formatFileSize(session.getFileSizeBytes()));

        return response;
    }
}
