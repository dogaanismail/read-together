package org.readtogether.infrastructure.storage.factory;

import lombok.experimental.UtilityClass;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;

@UtilityClass
public class S3RequestFactory {

    public static PutObjectRequest createPutObjectRequest(
            String bucket,
            String key,
            String contentType) {

        return PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();
    }

    public static DeleteObjectRequest createDeleteObjectRequest(
            String bucket,
            String key) {

        return DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
    }

    public static HeadObjectRequest createHeadObjectRequest(
            String bucket,
            String key) {

        return HeadObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
    }

    public static PutObjectPresignRequest createPutObjectPresignRequest(
            PutObjectRequest putObjectRequest,
            Duration signatureDuration) {

        return PutObjectPresignRequest.builder()
                .signatureDuration(signatureDuration)
                .putObjectRequest(putObjectRequest)
                .build();
    }

    public static PutObjectPresignRequest createPutObjectPresignRequest(
            String bucket,
            String key,
            String contentType,
            Duration signatureDuration) {

        PutObjectRequest putObjectRequest = createPutObjectRequest(bucket, key, contentType);
        return createPutObjectPresignRequest(putObjectRequest, signatureDuration);
    }
}
