package org.readtogether.session.model;

import lombok.Data;
import org.readtogether.session.entity.SessionEntity;

import java.time.Instant;
import java.util.UUID;

@Data
public class SessionResponse {

    private UUID id;
    private UUID userId;
    private String username;
    private String userProfilePicture;
    private String title;
    private String description;
    private String mediaUrl;
    private SessionEntity.MediaType mediaType;
    private Integer durationSeconds;
    private Long fileSizeBytes;
    private String mimeType;
    private SessionEntity.ProcessingStatus processingStatus;
    private String processingError;
    private boolean isPublic;
    private long viewCount;
    private long likeCount;
    private long commentCount;
    private UUID readingRoomId;
    private String transcript;
    private String[] tags;
    private String thumbnailUrl;
    private String bookTitle;
    private String language;
    private boolean isLive;
    private String authorName;
    private Instant createdAt;
    private Instant updatedAt;

    private String formattedDuration;
    private String formattedFileSize;
    private boolean canEdit;
    private boolean isLiked;
}
