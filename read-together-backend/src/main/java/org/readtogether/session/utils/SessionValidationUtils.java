package org.readtogether.session.utils;

import lombok.experimental.UtilityClass;
import org.readtogether.session.common.enums.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.readtogether.session.common.enums.MediaType.AUDIO;
import static org.readtogether.session.common.enums.MediaType.VIDEO;

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
            MediaType mediaType) {

        String contentType = file.getContentType();

        if (mediaType == AUDIO && !ALLOWED_AUDIO_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Invalid audio file type: " + contentType);
        }

        if (mediaType == VIDEO && !ALLOWED_VIDEO_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Invalid video file type: " + contentType);
        }
    }
}
