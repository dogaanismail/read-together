package org.readtogether.infrastructure.storage.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class S3UrlUtils {

    public static String buildS3Key(
            String folder,
            String fileName) {

        return folder + "/" + fileName;
    }

    public static String buildPublicUrl(
            String bucket,
            String region,
            String key,
            String customEndpoint) {

        if (customEndpoint != null && !customEndpoint.isEmpty()) {
            return customEndpoint + "/" + bucket + "/" + key;
        }
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, key);
    }

    public static String extractKeyFromUrl(
            String fileUrl,
            String bucket) {

        if (fileUrl.contains("amazonaws.com/")) {
            return fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        } else if (fileUrl.contains(bucket + "/")) {
            int index = fileUrl.indexOf(bucket + "/") + bucket.length() + 1;
            return fileUrl.substring(index);
        }
        return fileUrl;
    }

    public static boolean isValidS3Url(String url) {

        return url != null &&
               (url.contains("amazonaws.com/") || url.contains("s3"));
    }

    public static String extractBucketFromUrl(String url) {

        if (url.contains("amazonaws.com/")) {
            String[] parts = url.split("\\.");
            if (parts.length > 0) {
                return parts[0].substring(parts[0].lastIndexOf("/") + 1);
            }
        }
        return null;
    }
}
