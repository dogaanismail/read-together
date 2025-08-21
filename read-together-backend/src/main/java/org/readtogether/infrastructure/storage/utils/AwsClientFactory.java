package org.readtogether.infrastructure.storage.utils;

import lombok.experimental.UtilityClass;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@UtilityClass
public class AwsClientFactory {

    public static S3Client createS3Client(
            String accessKey,
            String secretKey,
            String region,
            String endpoint) {

        var builder = S3Client.builder();

        if (accessKey != null && !accessKey.isEmpty()) {
            AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
            builder.credentialsProvider(StaticCredentialsProvider.create(credentials));
        }

        if (endpoint != null && !endpoint.isEmpty()) {
            builder.endpointOverride(URI.create(endpoint));
        }

        builder.region(Region.of(region));

        return builder.build();
    }
}
