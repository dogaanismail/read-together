package org.readtogether.infrastructure.storage.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

@Slf4j
@UtilityClass
public class S3OperationUtils {

    public static final Duration DEFAULT_PRESIGN_DURATION = Duration.ofHours(1);
    public static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";

    public static void logUploadSuccess(
            String fileName,
            String publicUrl) {

        log.info("Successfully uploaded file to S3: {} -> {}", fileName, publicUrl);
    }

    public static void logUploadFailure(
            String fileName,
            Exception exception) {

        log.error("Failed to upload file to S3: {}", fileName, exception);
    }

    public static void logDeleteSuccess(String key) {

        log.info("Successfully deleted file from S3: {}", key);
    }

    public static void logDeleteFailure(
            String fileUrl,
            Exception exception) {

        log.error("Failed to delete file from S3: {}", fileUrl, exception);
    }

    public static void logPresignUrlFailure(
            String fileName,
            Exception exception) {

        log.error("Failed to generate pre-signed URL: {}", fileName, exception);
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

    public static boolean isValidContentType(String contentType) {

        return contentType != null && !contentType.trim().isEmpty();
    }

    public static String getContentTypeOrDefault(String contentType) {

        return isValidContentType(contentType) ? contentType : DEFAULT_CONTENT_TYPE;
    }
}
