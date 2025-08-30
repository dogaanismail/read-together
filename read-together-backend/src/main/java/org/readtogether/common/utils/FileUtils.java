package org.readtogether.common.utils;

import lombok.experimental.UtilityClass;

import java.util.Map;

@UtilityClass
public class FileUtils {

    private static final Map<String, String> CONTENT_TYPE_MAP = Map.of(
            "mp3", "audio/mpeg",
            "wav", "audio/wav",
            "mp4", "video/mp4",
            "avi", "video/avi",
            "mov", "video/quicktime",
            "webm", "video/webm",
            "jpg", "image/jpeg",
            "jpeg", "image/jpeg",
            "png", "image/png",
            "gif", "image/gif"
    );

    public static String determineContentType(
            String filename) {

        if (filename == null || !filename.contains(".")) {
            return "application/octet-stream";
        }

        String extension = extractFileExtension(filename);
        return CONTENT_TYPE_MAP.getOrDefault(extension, "application/octet-stream");
    }

    public static String extractFileExtension(
            String filename) {

        if (filename == null || !filename.contains(".")) {
            return "";
        }

        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    public static String extractFileName(
            String fullPath) {

        if (fullPath == null) {
            return "";
        }

        int lastSlash = Math.max(fullPath.lastIndexOf('/'), fullPath.lastIndexOf('\\'));
        return lastSlash >= 0 ? fullPath.substring(lastSlash + 1) : fullPath;
    }

    public static boolean isImageFile(
            String filename) {

        String extension = extractFileExtension(filename);
        return extension.equals("jpg") || extension.equals("jpeg") ||
                extension.equals("png") || extension.equals("gif");
    }

    public static boolean isAudioFile(
            String filename) {

        String extension = extractFileExtension(filename);
        return extension.equals("mp3") || extension.equals("wav") ||
                extension.equals("mp4");
    }

    public static boolean isVideoFile(
            String filename) {

        String extension = extractFileExtension(filename);
        return extension.equals("mp4") || extension.equals("avi") ||
                extension.equals("mov") || extension.equals("webm");
    }

    public static long bytesToMB(
            long bytes) {

        return bytes / (1024 * 1024);
    }

    public static String formatFileSize(
            long bytes) {

        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.1f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        } else {
            return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
        }
    }
}
