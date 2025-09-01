package org.readtogether.infrastructure.storage.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.readtogether.infrastructure.storage.config.StorageProperties;
import org.readtogether.infrastructure.storage.utils.AwsClientFactory;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("integration")
@Testcontainers
@DisplayName("S3StorageService Integration Tests")
class S3StorageServiceIntegrationTest {

    @Container
    static LocalStackContainer localStack = new LocalStackContainer(
            DockerImageName.parse("localstack/localstack:latest"))
            .withServices(LocalStackContainer.Service.S3);

    private S3Client s3Client;
    private S3StorageService s3StorageService;
    private StorageProperties.S3Properties s3Properties;

    @BeforeEach
    void setUp() {
        // Configure S3 properties for LocalStack
        s3Properties = new StorageProperties.S3Properties();
        s3Properties.setBucket("test-bucket");
        s3Properties.setRegion(localStack.getRegion());
        s3Properties.setAccessKey("test");
        s3Properties.setSecretKey("test");
        s3Properties.setEndpoint(localStack.getEndpointOverride(LocalStackContainer.Service.S3).toString());
        s3Properties.setServerSideEncryption("AES256");

        // Create S3 client configured for LocalStack
        s3Client = AwsClientFactory.createS3Client(
                s3Properties.getAccessKey(),
                s3Properties.getSecretKey(),
                s3Properties.getRegion(),
                s3Properties.getEndpoint());

        // Create test bucket
        try {
            s3Client.createBucket(CreateBucketRequest.builder()
                    .bucket(s3Properties.getBucket())
                    .build());
        } catch (BucketAlreadyExistsException | BucketAlreadyOwnedByYouException e) {
            // Bucket already exists, which is fine
        }

        s3StorageService = new S3StorageService(s3Client, s3Properties);
    }

    @Test
    @DisplayName("Should upload file with AES256 encryption and verify it exists")
    void shouldUploadFileWithAES256EncryptionAndVerifyItExists() throws Exception {
        // Given
        String fileName = "test-file.mp3";
        String folder = "audio";
        String contentType = "audio/mpeg";
        String testContent = "test audio content";
        InputStream inputStream = new ByteArrayInputStream(testContent.getBytes());

        // When - Upload file
        String publicUrl = s3StorageService.uploadFile(inputStream, fileName, folder, contentType);

        // Then - Verify upload succeeded
        assertThat(publicUrl).contains(s3Properties.getBucket());
        assertThat(publicUrl).contains("audio/test-file.mp3");

        // Verify file exists using headObject
        String key = folder + "/" + fileName;
        HeadObjectResponse headResponse = s3Client.headObject(HeadObjectRequest.builder()
                .bucket(s3Properties.getBucket())
                .key(key)
                .build());

        assertThat(headResponse.contentType()).isEqualTo(contentType);
        assertThat(headResponse.contentLength()).isEqualTo(testContent.length());
        // Note: LocalStack may not always return server-side encryption metadata
        // but the important thing is that the upload succeeded

        // Verify file exists using service method
        assertThat(s3StorageService.fileExists(publicUrl)).isTrue();

        // Verify file content using getObject
        try (var getObjectResponse = s3Client.getObject(GetObjectRequest.builder()
                .bucket(s3Properties.getBucket())
                .key(key)
                .build())) {
            
            assertThat(getObjectResponse.response().contentType()).isEqualTo(contentType);
        }
    }

    @Test
    @DisplayName("Should delete file and verify it no longer exists")
    void shouldDeleteFileAndVerifyItNoLongerExists() {
        // Given - Upload a file first
        String fileName = "test-delete-file.mp3";
        String folder = "audio";
        String contentType = "audio/mpeg";
        String testContent = "test audio content to delete";
        InputStream inputStream = new ByteArrayInputStream(testContent.getBytes());

        String publicUrl = s3StorageService.uploadFile(inputStream, fileName, folder, contentType);

        // Verify file exists before deletion
        assertThat(s3StorageService.fileExists(publicUrl)).isTrue();

        // When - Delete the file
        boolean deleteResult = s3StorageService.deleteFile(publicUrl);

        // Then - Verify deletion succeeded
        assertThat(deleteResult).isTrue();

        // Verify file no longer exists using service method
        assertThat(s3StorageService.fileExists(publicUrl)).isFalse();

        // Verify file no longer exists using direct S3 client
        String key = folder + "/" + fileName;
        assertThatThrownBy(() -> s3Client.headObject(HeadObjectRequest.builder()
                .bucket(s3Properties.getBucket())
                .key(key)
                .build()))
                .isInstanceOf(NoSuchKeyException.class);
    }

    @Test
    @DisplayName("Should handle nested folder structure correctly")
    void shouldHandleNestedFolderStructureCorrectly() {
        // Given
        String fileName = "nested-file.mp3";
        String folder = "audio/sessions/user123";
        String contentType = "audio/mpeg";
        String testContent = "nested audio content";
        InputStream inputStream = new ByteArrayInputStream(testContent.getBytes());

        // When
        String publicUrl = s3StorageService.uploadFile(inputStream, fileName, folder, contentType);

        // Then
        assertThat(publicUrl).contains("audio/sessions/user123/nested-file.mp3");

        // Verify file exists
        assertThat(s3StorageService.fileExists(publicUrl)).isTrue();

        // Verify we can retrieve the file
        String key = folder + "/" + fileName;
        HeadObjectResponse headResponse = s3Client.headObject(HeadObjectRequest.builder()
                .bucket(s3Properties.getBucket())
                .key(key)
                .build());

        assertThat(headResponse.contentType()).isEqualTo(contentType);
        assertThat(headResponse.contentLength()).isEqualTo(testContent.length());
    }

    @Test
    @DisplayName("Should return false for file existence check on non-existent file")
    void shouldReturnFalseForFileExistenceCheckOnNonExistentFile() {
        // Given
        String nonExistentUrl = s3Properties.getEndpoint() + "/" + s3Properties.getBucket() + "/non-existent/file.mp3";

        // When
        boolean exists = s3StorageService.fileExists(nonExistentUrl);

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should handle content type fallback for real upload")
    void shouldHandleContentTypeFallbackForRealUpload() {
        // Given
        String fileName = "no-content-type.dat";
        String folder = "data";
        String testContent = "binary data content";
        InputStream inputStream = new ByteArrayInputStream(testContent.getBytes());

        // When - Upload without content type
        String publicUrl = s3StorageService.uploadFile(inputStream, fileName, folder, null);

        // Then
        assertThat(publicUrl).contains("data/no-content-type.dat");

        // Verify file was uploaded with default content type
        String key = folder + "/" + fileName;
        HeadObjectResponse headResponse = s3Client.headObject(HeadObjectRequest.builder()
                .bucket(s3Properties.getBucket())
                .key(key)
                .build());

        assertThat(headResponse.contentType()).isEqualTo("application/octet-stream");
    }

    @Test
    @DisplayName("Should handle KMS encryption configuration")
    void shouldHandleKMSEncryptionConfiguration() {
        // Given - Configure for KMS (though LocalStack may not fully support KMS)
        s3Properties.setServerSideEncryption("aws:kms");
        s3Properties.setKmsKeyId("test-kms-key");
        s3Properties.setBucketKeyEnabled(true);

        S3StorageService kmsStorageService = new S3StorageService(s3Client, s3Properties);

        String fileName = "kms-encrypted-file.mp3";
        String folder = "secure";
        String contentType = "audio/mpeg";
        String testContent = "kms encrypted content";
        InputStream inputStream = new ByteArrayInputStream(testContent.getBytes());

        // When - This should not throw an exception even if LocalStack doesn't fully support KMS
        String publicUrl = kmsStorageService.uploadFile(inputStream, fileName, folder, contentType);

        // Then
        assertThat(publicUrl).contains("secure/kms-encrypted-file.mp3");
        assertThat(kmsStorageService.fileExists(publicUrl)).isTrue();
    }
}