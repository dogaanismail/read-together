package org.readtogether.chat.utils;

import lombok.experimental.UtilityClass;
import org.springframework.web.multipart.MultipartFile;

@UtilityClass
public class ChatFileUtils {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final String[] ALLOWED_EXTENSIONS = {
            ".jpg", ".jpeg", ".png", ".gif", ".bmp", // Images
            ".pdf", ".doc", ".docx", ".txt", ".rtf", // Documents
            ".mp3", ".wav", ".ogg", ".m4a", // Audio
            ".mp4", ".avi", ".mkv", ".mov", ".webm", // Video
            ".zip", ".rar", ".7z", ".tar", ".gz" // Archives
    };

    public static void validateFile(MultipartFile file) {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds maximum allowed size of 10MB");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new IllegalArgumentException("File must have a valid filename");
        }

        String extension = getFileExtension(originalFilename).toLowerCase();
        boolean isAllowed = false;
        for (String allowedExt : ALLOWED_EXTENSIONS) {
            if (extension.equals(allowedExt)) {
                isAllowed = true;
                break;
            }
        }

        if (!isAllowed) {
            throw new IllegalArgumentException("File type not allowed: " + extension);
        }
    }

    public static String getFileExtension(String filename) {

        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        return lastDotIndex > 0 ? filename.substring(lastDotIndex) : "";
    }

    public static boolean isImageFile(String contentType) {

        return contentType != null && contentType.startsWith("image/");
    }

    public static boolean isVideoFile(String contentType) {

        return contentType != null && contentType.startsWith("video/");
    }

    public static boolean isAudioFile(String contentType) {

        return contentType != null && contentType.startsWith("audio/");
    }

    public static boolean isDocumentFile(String contentType) {

        return contentType != null && (
                contentType.equals("application/pdf") ||
                        contentType.equals("application/msword") ||
                        contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") ||
                        contentType.equals("text/plain") ||
                        contentType.equals("application/rtf")
        );
    }
}