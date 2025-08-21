package org.readtogether.infrastructure.storage.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface ProgressAwareStorageService extends StorageService {

    /**
     * Upload a file asynchronously with progress tracking
     * @param file The file to upload
     * @param fileName The desired filename
     * @param folder The folder/prefix to store the file in
     * @param userId The user ID for progress notifications
     * @param sessionId The session ID for progress tracking
     * @param progressCallback Callback to report upload progress (0-100)
     * @return CompletableFuture with the public URL of the uploaded file
     */
    CompletableFuture<String> uploadFileWithProgress(
            MultipartFile file,
            String fileName,
            String folder,
            UUID userId,
            UUID sessionId,
            Consumer<Integer> progressCallback);

    /**
     * Upload a large file asynchronously with chunked upload and progress tracking
     * @param file The file to upload
     * @param fileName The desired filename
     * @param folder The folder/prefix to store the file in
     * @param userId The user ID for progress notifications
     * @param sessionId The session ID for progress tracking
     * @return CompletableFuture with the public URL of the uploaded file
     */
    CompletableFuture<String> uploadLargeFileAsync(
            MultipartFile file,
            String fileName,
            String folder,
            UUID userId,
            UUID sessionId);
}
