package org.readtogether.infrastructure.storage.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.readtogether.infrastructure.storage.config.StorageProperties;
import org.readtogether.infrastructure.storage.service.StorageService;
import org.readtogether.infrastructure.storage.utils.FileOperationUtils;
import org.readtogether.infrastructure.storage.utils.LocalStorageOperationUtils;
import org.readtogether.infrastructure.storage.utils.LocalStorageUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service("localStorageService")
public class LocalStorageService implements StorageService {

    private final StorageProperties.LocalProperties localProperties;
    private final Path basePath;

    public LocalStorageService(StorageProperties storageProperties) {

        this.localProperties = storageProperties.getLocal();
        this.basePath = Paths.get(localProperties.getBasePath());

        try {
            FileOperationUtils.createDirectoriesIfNotExists(basePath);
        } catch (IOException e) {
            LocalStorageOperationUtils.logDirectoryCreationFailure(basePath.toString(), e);
            throw new RuntimeException("Failed to initialize local storage", e);
        }
    }

    @Override
    @Async
    public CompletableFuture<String> uploadFileAsync(
            MultipartFile file,
            String fileName,
            String folder) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                return uploadFile(file, fileName, folder);
            } catch (Exception e) {
                LocalStorageOperationUtils.logAsyncUploadFailure(fileName, e);
                throw new RuntimeException("Failed to upload file", e);
            }
        });
    }

    @Override
    public String uploadFile(
            MultipartFile file,
            String fileName,
            String folder) {

        try {
            return uploadFile(file.getInputStream(), fileName, folder, file.getContentType());
        } catch (IOException e) {
            LocalStorageOperationUtils.logUploadFailure(fileName, e);
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    @Override
    public String uploadFile(
            InputStream inputStream,
            String fileName,
            String folder,
            String contentType) {

        try {
            Path folderPath = LocalStorageUtils.buildFolderPath(basePath, folder);
            FileOperationUtils.createDirectoriesIfNotExists(folderPath);

            Path filePath = LocalStorageUtils.buildFilePath(basePath, folder, fileName);
            FileOperationUtils.copyInputStreamToFile(inputStream, filePath);

            String publicUrl = LocalStorageUtils.buildPublicUrl(
                    localProperties.getPublicUrlBase(),
                    folder,
                    fileName);

            LocalStorageOperationUtils.logUploadSuccess(fileName, publicUrl);
            return publicUrl;
        } catch (IOException e) {
            LocalStorageOperationUtils.logUploadFailure(fileName, e);
            throw new RuntimeException("Failed to upload file locally", e);
        }
    }

    @Override
    public boolean deleteFile(String fileUrl) {

        try {
            Path filePath = LocalStorageUtils.extractPathFromUrl(
                    fileUrl,
                    localProperties.getPublicUrlBase(),
                    basePath);

            boolean deleted = FileOperationUtils.deleteFileIfExists(filePath);
            if (deleted) {
                LocalStorageOperationUtils.logDeleteSuccess(filePath.toString());
            }
            return deleted;
        } catch (Exception e) {
            LocalStorageOperationUtils.logDeleteFailure(fileUrl, e);
            return false;
        }
    }

    @Override
    public String getPreSignedUploadUrl(
            String fileName,
            String folder,
            String contentType) {

        return LocalStorageUtils.buildPublicUrl(
                localProperties.getPublicUrlBase(),
                folder,
                fileName);
    }

    @Override
    public boolean fileExists(String fileUrl) {

        try {
            Path filePath = LocalStorageUtils.extractPathFromUrl(
                    fileUrl,
                    localProperties.getPublicUrlBase(),
                    basePath);

            return FileOperationUtils.fileExists(filePath);
        } catch (Exception e) {
            LocalStorageOperationUtils.logFileExistenceCheckFailure(fileUrl, e);
            return false;
        }
    }
}
