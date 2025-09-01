package org.readtogether.infrastructure.storage.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.readtogether.infrastructure.storage.config.StorageProperties;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("S3StorageService Tests")
class S3StorageServiceTest {

    @Mock
    private S3Client s3Client;

    @Mock
    private MultipartFile multipartFile;

    private S3StorageService s3StorageService;
    private StorageProperties.S3Properties s3Properties;

    @BeforeEach
    void setUp() {
        s3Properties = new StorageProperties.S3Properties();
        s3Properties.setBucket("test-bucket");
        s3Properties.setRegion("us-east-1");
        s3Properties.setServerSideEncryption("AES256");
        s3Properties.setKmsKeyId("test-kms-key");
        s3Properties.setBucketKeyEnabled(true);

        s3StorageService = new S3StorageService(s3Client, s3Properties);
    }

    @Test
    @DisplayName("Should upload file with AES256 encryption")
    void shouldUploadFileWithAES256Encryption() throws IOException {
        // Given
        String fileName = "test-file.mp3";
        String folder = "audio";
        String contentType = "audio/mpeg";
        InputStream inputStream = new ByteArrayInputStream("test content".getBytes());
        
        s3Properties.setServerSideEncryption("AES256");
        s3StorageService = new S3StorageService(s3Client, s3Properties);

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        // When
        String result = s3StorageService.uploadFile(inputStream, fileName, folder, contentType);

        // Then
        ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client).putObject(requestCaptor.capture(), any(RequestBody.class));

        PutObjectRequest capturedRequest = requestCaptor.getValue();
        assertThat(capturedRequest.bucket()).isEqualTo("test-bucket");
        assertThat(capturedRequest.key()).isEqualTo("audio/test-file.mp3");
        assertThat(capturedRequest.contentType()).isEqualTo("audio/mpeg");
        assertThat(capturedRequest.serverSideEncryption()).isEqualTo(ServerSideEncryption.AES256);
        assertThat(capturedRequest.ssekmsKeyId()).isNull();
        assertThat(capturedRequest.bucketKeyEnabled()).isNull();

        assertThat(result).isEqualTo("https://test-bucket.s3.us-east-1.amazonaws.com/audio/test-file.mp3");
    }

    @Test
    @DisplayName("Should upload file with KMS encryption")
    void shouldUploadFileWithKMSEncryption() throws IOException {
        // Given
        String fileName = "test-file.mp3";
        String folder = "audio";
        String contentType = "audio/mpeg";
        InputStream inputStream = new ByteArrayInputStream("test content".getBytes());
        
        s3Properties.setServerSideEncryption("aws:kms");
        s3StorageService = new S3StorageService(s3Client, s3Properties);

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        // When
        String result = s3StorageService.uploadFile(inputStream, fileName, folder, contentType);

        // Then
        ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client).putObject(requestCaptor.capture(), any(RequestBody.class));

        PutObjectRequest capturedRequest = requestCaptor.getValue();
        assertThat(capturedRequest.serverSideEncryption()).isEqualTo(ServerSideEncryption.AWS_KMS);
        assertThat(capturedRequest.ssekmsKeyId()).isEqualTo("test-kms-key");
        assertThat(capturedRequest.bucketKeyEnabled()).isEqualTo(true);
    }

    @Test
    @DisplayName("Should fallback to default content type when null")
    void shouldFallbackToDefaultContentTypeWhenNull() throws IOException {
        // Given
        String fileName = "test-file.mp3";
        String folder = "audio";
        InputStream inputStream = new ByteArrayInputStream("test content".getBytes());

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        // When
        s3StorageService.uploadFile(inputStream, fileName, folder, null);

        // Then
        ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client).putObject(requestCaptor.capture(), any(RequestBody.class));

        PutObjectRequest capturedRequest = requestCaptor.getValue();
        assertThat(capturedRequest.contentType()).isEqualTo("application/octet-stream");
    }

    @Test
    @DisplayName("Should fallback to default content type when blank")
    void shouldFallbackToDefaultContentTypeWhenBlank() throws IOException {
        // Given
        String fileName = "test-file.mp3";
        String folder = "audio";
        InputStream inputStream = new ByteArrayInputStream("test content".getBytes());

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        // When
        s3StorageService.uploadFile(inputStream, fileName, folder, "   ");

        // Then
        ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client).putObject(requestCaptor.capture(), any(RequestBody.class));

        PutObjectRequest capturedRequest = requestCaptor.getValue();
        assertThat(capturedRequest.contentType()).isEqualTo("application/octet-stream");
    }

    @Test
    @DisplayName("Should delegate MultipartFile upload to InputStream method")
    void shouldDelegateMultipartFileUploadToInputStreamMethod() throws IOException {
        // Given
        String fileName = "test-file.mp3";
        String folder = "audio";
        String contentType = "audio/mpeg";
        byte[] fileContent = "test content".getBytes();

        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(fileContent));
        when(multipartFile.getContentType()).thenReturn(contentType);
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        // When
        String result = s3StorageService.uploadFile(multipartFile, fileName, folder);

        // Then
        verify(multipartFile).getInputStream();
        verify(multipartFile).getContentType();
        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));

        assertThat(result).isEqualTo("https://test-bucket.s3.us-east-1.amazonaws.com/audio/test-file.mp3");
    }

    @Test
    @DisplayName("Should complete uploadFileAsync normally and return expected URL")
    void shouldCompleteUploadFileAsyncNormallyAndReturnExpectedURL() throws IOException {
        // Given
        String fileName = "test-file.mp3";
        String folder = "audio";
        String contentType = "audio/mpeg";
        byte[] fileContent = "test content".getBytes();

        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(fileContent));
        when(multipartFile.getContentType()).thenReturn(contentType);
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        // When
        CompletableFuture<String> result = s3StorageService.uploadFileAsync(multipartFile, fileName, folder);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.join()).isEqualTo("https://test-bucket.s3.us-east-1.amazonaws.com/audio/test-file.mp3");
    }

    @Test
    @DisplayName("Should delete file and return true on success")
    void shouldDeleteFileAndReturnTrueOnSuccess() {
        // Given
        String fileUrl = "https://test-bucket.s3.us-east-1.amazonaws.com/audio/test-file.mp3";

        when(s3Client.deleteObject(any(DeleteObjectRequest.class)))
                .thenReturn(DeleteObjectResponse.builder().build());

        // When
        boolean result = s3StorageService.deleteFile(fileUrl);

        // Then
        ArgumentCaptor<DeleteObjectRequest> requestCaptor = ArgumentCaptor.forClass(DeleteObjectRequest.class);
        verify(s3Client).deleteObject(requestCaptor.capture());

        DeleteObjectRequest capturedRequest = requestCaptor.getValue();
        assertThat(capturedRequest.bucket()).isEqualTo("test-bucket");
        assertThat(capturedRequest.key()).isEqualTo("audio/test-file.mp3");
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should delete file and return false on exception")
    void shouldDeleteFileAndReturnFalseOnException() {
        // Given
        String fileUrl = "https://test-bucket.s3.us-east-1.amazonaws.com/audio/test-file.mp3";

        when(s3Client.deleteObject(any(DeleteObjectRequest.class)))
                .thenThrow(new RuntimeException("Delete failed"));

        // When
        boolean result = s3StorageService.deleteFile(fileUrl);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should return true when file exists")
    void shouldReturnTrueWhenFileExists() {
        // Given
        String fileUrl = "https://test-bucket.s3.us-east-1.amazonaws.com/audio/test-file.mp3";

        when(s3Client.headObject(any(HeadObjectRequest.class)))
                .thenReturn(HeadObjectResponse.builder().build());

        // When
        boolean result = s3StorageService.fileExists(fileUrl);

        // Then
        ArgumentCaptor<HeadObjectRequest> requestCaptor = ArgumentCaptor.forClass(HeadObjectRequest.class);
        verify(s3Client).headObject(requestCaptor.capture());

        HeadObjectRequest capturedRequest = requestCaptor.getValue();
        assertThat(capturedRequest.bucket()).isEqualTo("test-bucket");
        assertThat(capturedRequest.key()).isEqualTo("audio/test-file.mp3");
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should return false when file does not exist (NoSuchKeyException)")
    void shouldReturnFalseWhenFileDoesNotExist() {
        // Given
        String fileUrl = "https://test-bucket.s3.us-east-1.amazonaws.com/audio/test-file.mp3";

        when(s3Client.headObject(any(HeadObjectRequest.class)))
                .thenThrow(NoSuchKeyException.builder().build());

        // When
        boolean result = s3StorageService.fileExists(fileUrl);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should return false on generic S3Exception during file existence check")
    void shouldReturnFalseOnGenericS3Exception() {
        // Given
        String fileUrl = "https://test-bucket.s3.us-east-1.amazonaws.com/audio/test-file.mp3";

        when(s3Client.headObject(any(HeadObjectRequest.class)))
                .thenThrow(S3Exception.builder().message("Access denied").build());

        // When
        boolean result = s3StorageService.fileExists(fileUrl);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should use default AES256 encryption when serverSideEncryption is null")
    void shouldUseDefaultAES256EncryptionWhenServerSideEncryptionIsNull() throws IOException {
        // Given
        String fileName = "test-file.mp3";
        String folder = "audio";
        String contentType = "audio/mpeg";
        InputStream inputStream = new ByteArrayInputStream("test content".getBytes());
        
        s3Properties.setServerSideEncryption(null);
        s3StorageService = new S3StorageService(s3Client, s3Properties);

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        // When
        s3StorageService.uploadFile(inputStream, fileName, folder, contentType);

        // Then
        ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client).putObject(requestCaptor.capture(), any(RequestBody.class));

        PutObjectRequest capturedRequest = requestCaptor.getValue();
        assertThat(capturedRequest.serverSideEncryption()).isEqualTo(ServerSideEncryption.AES256);
    }
}