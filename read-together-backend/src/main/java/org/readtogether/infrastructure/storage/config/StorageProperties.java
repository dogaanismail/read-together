package org.readtogether.infrastructure.storage.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "storage")
public class StorageProperties {

    private String provider = "local"; // s3, local, azure, gcp
    private S3Properties s3 = new S3Properties();
    private LocalProperties local = new LocalProperties();

    @Data
    public static class S3Properties {
        private String bucket;
        private String region = "us-east-1";
        private String accessKey;
        private String secretKey;
        private String endpoint; // For S3-compatible services
        // Encryption settings
        // Values: "AES256" for SSE-S3 or "aws:kms" for SSE-KMS. Default to AES256 for safety.
        private String serverSideEncryption = "AES256";
        // Provide when using SSE-KMS
        private String kmsKeyId;
        // Use S3 Bucket Keys for AWS-KMS to reduce cost
        private boolean bucketKeyEnabled = true;
    }

    @Data
    public static class LocalProperties {
        private String basePath = "/tmp/read-together/uploads";
        private String publicUrlBase = "http://localhost:5006/api/v1/files";
    }
}
