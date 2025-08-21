package org.readtogether.infrastructure.storage.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class LocalStorageOperationUtils {

    public static void logUploadSuccess(
            String fileName,
            String publicUrl) {

        log.info("Successfully uploaded file locally: {} -> {}", fileName, publicUrl);
    }

    public static void logUploadFailure(
            String fileName,
            Exception exception) {

        log.error("Failed to upload file locally: {}", fileName, exception);
    }

    public static void logDeleteSuccess(String filePath) {

        log.info("Successfully deleted file: {}", filePath);
    }

    public static void logDeleteFailure(
            String fileUrl,
            Exception exception) {

        log.error("Failed to delete file: {}", fileUrl, exception);
    }

    public static void logFileExistenceCheckFailure(
            String fileUrl,
            Exception exception) {

        log.error("Failed to check file existence: {}", fileUrl, exception);
    }

    public static void logAsyncUploadFailure(
            String fileName,
            Exception exception) {

        log.error("Failed to upload file asynchronously: {}", fileName, exception);
    }

    public static void logDirectoryCreationFailure(
            String path,
            Exception exception) {

        log.error("Failed to create base storage directory: {}", path, exception);
    }
}
