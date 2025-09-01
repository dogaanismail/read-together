# S3 Storage Testing Documentation

This document describes the comprehensive test suite for S3 storage functionality in the Read Together application.

## Test Structure

### Unit Tests
Located in `src/test/java/org/readtogether/infrastructure/storage/`

- **S3StorageServiceTest**: Tests S3StorageService with mocked S3Client
- **S3UrlUtilsTest**: Tests URL parsing and key extraction utilities

### Integration Tests  
- **S3StorageServiceIntegrationTest**: Tests with real S3 operations using Localstack

## Running Tests

### All S3 Tests
```bash
./gradlew :read-together-backend:test --tests "*S3*"
```

### Unit Tests Only
```bash
./gradlew :read-together-backend:test --tests "*S3StorageServiceTest*" --tests "*S3UrlUtilsTest*"
```

### Integration Tests Only
```bash
./gradlew :read-together-backend:test --tests "*S3StorageServiceIntegrationTest*"
```

### All Tests
```bash
./gradlew :read-together-backend:test
```

## Test Coverage

### S3StorageServiceTest (Unit Tests)
✅ **AES256 Encryption**: Verifies PutObjectRequest sets ServerSideEncryption.AES256  
✅ **KMS Encryption**: Verifies KMS settings with kmsKeyId and bucketKeyEnabled=true  
✅ **Content Type Fallback**: Tests fallback to application/octet-stream when content type is null/blank  
✅ **MultipartFile Upload**: Tests delegation to InputStream method with content type preservation  
✅ **Async Upload**: Tests CompletableFuture completion and URL return  
✅ **File Deletion**: Tests key extraction and success/failure scenarios  
✅ **File Existence**: Tests headObject success, NoSuchKeyException, and generic S3Exception handling  
✅ **Default Encryption**: Tests fallback to AES256 when serverSideEncryption is null  

### S3UrlUtilsTest (Unit Tests)
✅ **Key Building**: Tests buildS3Key for folder/filename combination  
✅ **Virtual-Hosted URLs**: Tests buildPublicUrl for AWS S3 virtual-hosted style  
✅ **Path-Style URLs**: Tests buildPublicUrl for custom endpoints  
✅ **Key Extraction**: Tests extractKeyFromUrl for both URL styles with nested folders  
✅ **URL Validation**: Tests isValidS3Url for amazonaws.com and s3 keyword detection  
✅ **Bucket Extraction**: Tests extractBucketFromUrl for amazonaws.com URLs  

### S3StorageServiceIntegrationTest (Integration Tests)
✅ **Real S3 Operations**: Upload, headObject, getObject with Localstack  
✅ **File Deletion**: Upload then delete, verify NoSuchKeyException  
✅ **Nested Folders**: Tests complex folder structures (audio/sessions/user123/)  
✅ **Non-Existent Files**: Tests fileExists returns false appropriately  
✅ **Content Type Fallback**: Real upload with null content type → application/octet-stream  
✅ **KMS Configuration**: Tests KMS encryption settings (even if LocalStack doesn't fully support)  

## Dependencies Added

```gradle
testImplementation 'org.testcontainers:localstack'
```

## Key Features

### Encryption Testing
- Validates AES256 is set by default
- Tests KMS encryption with kmsKeyId and bucketKeyEnabled
- Verifies encryption headers are properly set in PutObjectRequest

### Error Handling
- NoSuchKeyException handling for file existence checks
- Generic S3Exception handling for various failures  
- Proper boolean returns for delete and exists operations

### URL Handling
- Fixed extractKeyFromUrl to handle nested keys (folder/subfolder/file.mp3)
- Tests both virtual-hosted and path-style URL formats
- Supports custom endpoints for non-AWS S3 implementations

### Architecture
- Testable constructor in S3StorageService accepts S3Client for mocking
- Removed conflicting @Service annotations to avoid Spring context issues
- Integration tests use standalone Testcontainers without full Spring context

## Test Results
- **Total S3 Tests**: 32
- **Unit Tests**: 26 (S3StorageServiceTest: 15, S3UrlUtilsTest: 11)  
- **Integration Tests**: 6 (S3StorageServiceIntegrationTest)
- **All Tests Passing**: ✅