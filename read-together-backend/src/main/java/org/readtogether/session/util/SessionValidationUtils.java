package org.readtogether.session.util;

import lombok.experimental.UtilityClass;
import org.readtogether.session.entity.SessionEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@UtilityClass
public class SessionValidationUtils {

    private static final List<String> ALLOWED_AUDIO_TYPES = List.of(
            "audio/mpeg", "audio/wav", "audio/mp3", "audio/mp4"
    );

    private static final List<String> ALLOWED_VIDEO_TYPES = List.of(
            "video/mp4", "video/avi", "video/mov", "video/webm"
    );

    public static void validateFileType(
            MultipartFile file,
            SessionEntity.MediaType mediaType) {

        String contentType = file.getContentType();

        if (mediaType == SessionEntity.MediaType.AUDIO && !ALLOWED_AUDIO_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Invalid audio file type: " + contentType);
        }

        if (mediaType == SessionEntity.MediaType.VIDEO && !ALLOWED_VIDEO_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Invalid video file type: " + contentType);
        }
    }
}
