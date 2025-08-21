package org.readtogether.infrastructure.storage.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

public interface StorageService {

    /**
     * Upload a file asynchronously
     * @param file The file to upload
     * @param fileName The desired filename
     * @param folder The folder/prefix to store the file in
     * @return CompletableFuture with the public URL of the uploaded file
     */
    CompletableFuture<String> uploadFileAsync(MultipartFile file, String fileName, String folder);

    /**
     * Upload a file synchronously
     * @param file The file to upload
     * @param fileName The desired filename
     * @param folder The folder/prefix to store the file in
     * @return The public URL of the uploaded file
     */
    String uploadFile(MultipartFile file, String fileName, String folder);

    /**
     * Upload from InputStream
     * @param inputStream The input stream
     * @param fileName The desired filename
     * @param folder The folder/prefix to store the file in
     * @param contentType The content type of the file
     * @return The public URL of the uploaded file
     */
    String uploadFile(InputStream inputStream, String fileName, String folder, String contentType);

    /**
     * Delete a file
     * @param fileUrl The URL or key of the file to delete
     * @return true if deleted successfully
     */
    boolean deleteFile(String fileUrl);

    /**
     * Get a pre-signed URL for direct upload (useful for large files)
     * @param fileName The desired filename
     * @param folder The folder/prefix
     * @param contentType The content type
     * @return Pre-signed URL
     */
    String getPreSignedUploadUrl(String fileName, String folder, String contentType);

    /**
     * Check if file exists
     * @param fileUrl The URL or key of the file
     * @return true if file exists
     */
    boolean fileExists(String fileUrl);
}
