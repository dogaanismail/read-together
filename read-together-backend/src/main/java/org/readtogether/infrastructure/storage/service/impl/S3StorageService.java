package org.readtogether.infrastructure.storage.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.readtogether.infrastructure.storage.config.StorageProperties;
import org.readtogether.infrastructure.storage.factory.S3RequestFactory;
import org.readtogether.infrastructure.storage.service.StorageService;
import org.readtogether.infrastructure.storage.util.AwsClientFactory;
import org.readtogether.infrastructure.storage.util.S3OperationUtils;
import org.readtogether.infrastructure.storage.util.S3UrlUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service("s3StorageService")
public class S3StorageService implements StorageService {

    private final S3Client s3Client;
    private final StorageProperties.S3Properties s3Properties;

    public S3StorageService(StorageProperties storageProperties) {

        this.s3Properties = storageProperties.getS3();
        this.s3Client = createS3Client();
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
                S3OperationUtils.logAsyncUploadFailure(fileName, e);
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
            S3OperationUtils.logUploadFailure(fileName, e);
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
            String key = S3UrlUtils.buildS3Key(folder, fileName);
            String validContentType = S3OperationUtils.getContentTypeOrDefault(contentType);

            PutObjectRequest request = S3RequestFactory.createPutObjectRequest(
                    s3Properties.getBucket(),
                    key,
                    validContentType);

            s3Client.putObject(request, RequestBody.fromInputStream(inputStream, inputStream.available()));

            String publicUrl = S3UrlUtils.buildPublicUrl(
                    s3Properties.getBucket(),
                    s3Properties.getRegion(),
                    key,
                    s3Properties.getEndpoint());

            S3OperationUtils.logUploadSuccess(fileName, publicUrl);
            return publicUrl;
        } catch (Exception e) {
            S3OperationUtils.logUploadFailure(fileName, e);
            throw new RuntimeException("Failed to upload file to S3", e);
        }
    }

    @Override
    public boolean deleteFile(String fileUrl) {

        try {
            String key = S3UrlUtils.extractKeyFromUrl(fileUrl, s3Properties.getBucket());

            DeleteObjectRequest request = S3RequestFactory.createDeleteObjectRequest(
                    s3Properties.getBucket(),
                    key);

            s3Client.deleteObject(request);
            S3OperationUtils.logDeleteSuccess(key);
            return true;
        } catch (Exception e) {
            S3OperationUtils.logDeleteFailure(fileUrl, e);
            return false;
        }
    }

    @Override
    public String getPreSignedUploadUrl(
            String fileName,
            String folder,
            String contentType) {

        // For now, return a simple public URL since presigner is causing issues
        // TODO: Implement proper presigned URLs once AWS SDK setup is resolved
        String key = S3UrlUtils.buildS3Key(folder, fileName);
        return S3UrlUtils.buildPublicUrl(
                s3Properties.getBucket(),
                s3Properties.getRegion(),
                key,
                s3Properties.getEndpoint());
    }

    @Override
    public boolean fileExists(String fileUrl) {

        try {
            String key = S3UrlUtils.extractKeyFromUrl(fileUrl, s3Properties.getBucket());

            HeadObjectRequest request = S3RequestFactory.createHeadObjectRequest(
                    s3Properties.getBucket(),
                    key);

            s3Client.headObject(request);
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (Exception e) {
            S3OperationUtils.logFileExistenceCheckFailure(fileUrl, e);
            return false;
        }
    }

    private S3Client createS3Client() {

        return AwsClientFactory.createS3Client(
                s3Properties.getAccessKey(),
                s3Properties.getSecretKey(),
                s3Properties.getRegion(),
                s3Properties.getEndpoint());
    }
}
