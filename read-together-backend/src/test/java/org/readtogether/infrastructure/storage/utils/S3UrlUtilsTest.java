package org.readtogether.infrastructure.storage.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
@DisplayName("S3UrlUtils Tests")
class S3UrlUtilsTest {

    @Test
    @DisplayName("Should build S3 key correctly")
    void shouldBuildS3KeyCorrectly() {
        // Given
        String folder = "audio";
        String fileName = "test-file.mp3";

        // When
        String result = S3UrlUtils.buildS3Key(folder, fileName);

        // Then
        assertThat(result).isEqualTo("audio/test-file.mp3");
    }

    @Test
    @DisplayName("Should build public URL for virtual-hosted style")
    void shouldBuildPublicUrlForVirtualHostedStyle() {
        // Given
        String bucket = "test-bucket";
        String region = "us-east-1";
        String key = "audio/test-file.mp3";
        String customEndpoint = null;

        // When
        String result = S3UrlUtils.buildPublicUrl(bucket, region, key, customEndpoint);

        // Then
        assertThat(result).isEqualTo("https://test-bucket.s3.us-east-1.amazonaws.com/audio/test-file.mp3");
    }

    @Test
    @DisplayName("Should build public URL for custom endpoint (path-style)")
    void shouldBuildPublicUrlForCustomEndpoint() {
        // Given
        String bucket = "test-bucket";
        String region = "us-east-1";
        String key = "audio/test-file.mp3";
        String customEndpoint = "https://minio.example.com";

        // When
        String result = S3UrlUtils.buildPublicUrl(bucket, region, key, customEndpoint);

        // Then
        assertThat(result).isEqualTo("https://minio.example.com/test-bucket/audio/test-file.mp3");
    }

    @Test
    @DisplayName("Should extract key from virtual-hosted style URL")
    void shouldExtractKeyFromVirtualHostedStyleUrl() {
        // Given
        String fileUrl = "https://test-bucket.s3.us-east-1.amazonaws.com/audio/test-file.mp3";
        String bucket = "test-bucket";

        // When
        String result = S3UrlUtils.extractKeyFromUrl(fileUrl, bucket);

        // Then
        assertThat(result).isEqualTo("audio/test-file.mp3");
    }

    @Test
    @DisplayName("Should extract key from virtual-hosted style URL with nested folders")
    void shouldExtractKeyFromVirtualHostedStyleUrlWithNestedFolders() {
        // Given
        String fileUrl = "https://test-bucket.s3.us-east-1.amazonaws.com/folder/subfolder/test-file.mp3";
        String bucket = "test-bucket";

        // When
        String result = S3UrlUtils.extractKeyFromUrl(fileUrl, bucket);

        // Then
        assertThat(result).isEqualTo("folder/subfolder/test-file.mp3");
    }

    @Test
    @DisplayName("Should extract key from path-style URL")
    void shouldExtractKeyFromPathStyleUrl() {
        // Given
        String fileUrl = "https://minio.example.com/test-bucket/audio/test-file.mp3";
        String bucket = "test-bucket";

        // When
        String result = S3UrlUtils.extractKeyFromUrl(fileUrl, bucket);

        // Then
        assertThat(result).isEqualTo("audio/test-file.mp3");
    }

    @Test
    @DisplayName("Should extract key from path-style URL with nested folders")
    void shouldExtractKeyFromPathStyleUrlWithNestedFolders() {
        // Given
        String fileUrl = "https://minio.example.com/test-bucket/folder/subfolder/test-file.mp3";
        String bucket = "test-bucket";

        // When
        String result = S3UrlUtils.extractKeyFromUrl(fileUrl, bucket);

        // Then
        assertThat(result).isEqualTo("folder/subfolder/test-file.mp3");
    }

    @Test
    @DisplayName("Should return original URL when extraction patterns don't match")
    void shouldReturnOriginalUrlWhenExtractionPatternsDontMatch() {
        // Given
        String fileUrl = "https://example.com/some/other/path/file.mp3";
        String bucket = "test-bucket";

        // When
        String result = S3UrlUtils.extractKeyFromUrl(fileUrl, bucket);

        // Then
        assertThat(result).isEqualTo(fileUrl);
    }

    @Test
    @DisplayName("Should validate S3 URL with amazonaws.com")
    void shouldValidateS3UrlWithAmazonawsCom() {
        // Given
        String url = "https://test-bucket.s3.us-east-1.amazonaws.com/audio/test-file.mp3";

        // When
        boolean result = S3UrlUtils.isValidS3Url(url);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should validate S3 URL with s3 keyword")
    void shouldValidateS3UrlWithS3Keyword() {
        // Given
        String url = "https://minio.example.com/bucket/s3/test-file.mp3";

        // When
        boolean result = S3UrlUtils.isValidS3Url(url);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should invalidate non-S3 URL")
    void shouldInvalidateNonS3Url() {
        // Given
        String url = "https://example.com/files/test-file.mp3";

        // When
        boolean result = S3UrlUtils.isValidS3Url(url);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should invalidate null URL")
    void shouldInvalidateNullUrl() {
        // When & Then
        assertThat(S3UrlUtils.isValidS3Url(null)).isFalse();
    }

    @Test
    @DisplayName("Should extract bucket from amazonaws.com URL")
    void shouldExtractBucketFromAmazonawsComUrl() {
        // Given
        String url = "https://test-bucket.s3.us-east-1.amazonaws.com/audio/test-file.mp3";

        // When
        String result = S3UrlUtils.extractBucketFromUrl(url);

        // Then
        assertThat(result).isEqualTo("test-bucket");
    }

    @Test
    @DisplayName("Should return null for non-amazonaws.com URL")
    void shouldReturnNullForNonAmazonawsComUrl() {
        // Given
        String url = "https://minio.example.com/test-bucket/audio/test-file.mp3";

        // When
        String result = S3UrlUtils.extractBucketFromUrl(url);

        // Then
        assertThat(result).isNull();
    }

}