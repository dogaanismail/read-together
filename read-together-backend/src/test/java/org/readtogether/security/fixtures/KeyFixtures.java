package org.readtogether.security.fixtures;

import lombok.experimental.UtilityClass;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@UtilityClass
public class KeyFixtures {

    public static KeyPair generateTestRsaKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to generate test RSA key pair", e);
        }
    }

    public static String getTestPublicKeyPem() {

        return "-----BEGIN PUBLIC KEY-----\n" +
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwU2xJYJNGbVdOOhm9uX4\n" +
                "6s8fFcE2sOZUjPh+8LQl2tGd8IVw8YV3OBF5nQ+K1L4jF2Q4nYG8UQ+4XFo4k1ZS\n" +
                "rU+9L+wYHOHjQOkPFqGQyN3VJUw+JQgH9OWpJZfj3x1WqGzOQ5FJ8V+L7rGQOgJD\n" +
                "cQYlYZJ7+4QbS9DgJ+fQ8VJk4dGQ5L5n+nO+zJKH6zS0lJ+9O0p+K+9VqFj3xL5J\n" +
                "jJoQ0zGz5hK4vF0z4ZJj+5B9oJ8L7r3x3y0kP2O7dG3w4u6d7cG9O4gP3A5B2vF8\n" +
                "e0KjQwIDAQAB\n" +
                "-----END PUBLIC KEY-----";
    }

    public static String getTestPrivateKeyPem() {

        return "-----BEGIN PRIVATE KEY-----\n" +
                "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDBTbElgk0ZtV04\n" +
                "6Gb25fjqzx8VwTaw5lSM+H7wtCXa0Z3whXDxhXc4EXmdD4rUviMXZDidgbxRD7hc\n" +
                "WjiTVlKtT70v7Bgc4eNA6Q8WoZDI3dUlTD4lCAf05akll+PfHVaobM5DkUnxX4vu\n" +
                "sZA6AkNxBiVhknv7hBtL0OAn59DxUmTh0ZDkvmf6c77MkofrNLSUn707Sn4r71Wo\n" +
                "WPfEvkmMmhDTMbPmEri8XTPhkmP7kH2gnwvuvfHfLSQ/Y7t0bfDi7p3twb07iA/c\n" +
                "DkHa8Xx7QqNDAgMBAAECggEBAIqJ4tJZJGd9M3VBQ4vHd+7J+KzX5QKQ8+qpFQ9O\n" +
                "L3ZJj+3N1Q+8F5Q0J7F4G9N8D4P6T3u8GqJ7+K9Q4r8N2Q0F8L4VJ+9K3P5v8Q1s\n" +
                "T6+J8Q+9P4L5Q7+J3V8N4Q9K2P7U5Q8V3N+J9L4Q6F8P2V0J4Q5N7+K8L3Q9V2F\n" +
                "J8Q4U6P9V5N7Q0L8G4J3V2Q5U8F9P6T3Q7J4L1V8Q2N9K5P0U3Q6V7J8L4F9T1Q\n" +
                "Y5N2P8U4J6V9Q3L7F0T2Q8N5V1J4P6U9Q7L3F8T0Q2V5J1N9P4U6Q8L7V3F2T9Q\n" +
                "0N4J5P8V1U6Q3L9F7T2Q5V8J0N3P4U7Q1L6F9T8Q2V0J4N5P1U8Q6L3F7T9Q4V2\n" +
                "ECgYEA4Q0P7V8J3L9F6T2Q5N8U1V4Q7L0F3T9P6V2J8N5U4Q1L7F0T3P8V9J2N6\n" +
                "U5Q4L8F1T7P0V3J9N2U6Q5L4F8T1P7V0J3N9U2Q6L5F7T8P1V4J0N3U9Q2L6F5T\n" +
                "P8V1J4N0U7Q3L9F2T6P5V8J1N4U0Q7L3F9T2P6V5J8N1U4Q0L7F3T9P2V6J5N8U\n" +
                "ECgYEA2sZFJGd9M3VBQ4vHd+7J+KzX5QKQ8+qpFQ9OL3ZJj+3N1Q+8F5Q0J7F4G\n" +
                "9N8D4P6T3u8GqJ7+K9Q4r8N2Q0F8L4VJ+9K3P5v8Q1sT6+J8Q+9P4L5Q7+J3V8N\n" +
                "4Q9K2P7U5Q8V3N+J9L4Q6F8P2V0J4Q5N7+K8L3Q9V2FJ8Q4U6P9V5N7Q0L8G4J3\n" +
                "V2Q5U8F9P6T3Q7J4L1V8Q2N9K5P0U3Q6V7J8L4F9T1QY5N2P8U4J6V9Q3L7F0T2\n" +
                "ECgYBvQ8N5V1J4P6U9Q7L3F8T0Q2V5J1N9P4U6Q8L7V3F2T9Q0N4J5P8V1U6Q3L\n" +
                "9F7T2Q5V8J0N3P4U7Q1L6F9T8Q2V0J4N5P1U8Q6L3F7T9Q4V2J0N8U5P2Q9L1F6\n" +
                "T3P0V7J4N1U8Q5L2F9T6P3V0J7N4U1Q8L5F2T9P6V3J0N7U4Q1L8F5T2P9V6J3N\n" +
                "0U7Q4L1F8T5P2V9J6N3U0Q7L4F1T8P5V2J9N6U3Q0L7F4T1P8V5J2N9U6Q3L0F7\n" +
                "ECgYEAqJ7+K9Q4r8N2Q0F8L4VJ+9K3P5v8Q1sT6+J8Q+9P4L5Q7+J3V8N4Q9K2P\n" +
                "7U5Q8V3N+J9L4Q6F8P2V0J4Q5N7+K8L3Q9V2FJ8Q4U6P9V5N7Q0L8G4J3V2Q5U8\n" +
                "F9P6T3Q7J4L1V8Q2N9K5P0U3Q6V7J8L4F9T1QY5N2P8U4J6V9Q3L7F0T2Q8N5V1\n" +
                "J4P6U9Q7L3F8T0Q2V5J1N9P4U6Q8L7V3F2T9Q0N4J5P8V1U6Q3L9F7T2Q5V8J0N\n" +
                "ECgYB3P4U7Q1L6F9T8Q2V0J4N5P1U8Q6L3F7T9Q4V2J0N8U5P2Q9L1F6T3P0V7J\n" +
                "4N1U8Q5L2F9T6P3V0J7N4U1Q8L5F2T9P6V3J0N7U4Q1L8F5T2P9V6J3N0U7Q4L1\n" +
                "F8T5P2V9J6N3U0Q7L4F1T8P5V2J9N6U3Q0L7F4T1P8V5J2N9U6Q3L0F7T4P1V8J\n" +
                "5N2U9Q6L3F0T7P4V1J8N5U2Q9L6F3T0P7V4J1N8U5Q2L9F6T3P0V7J4N1U8Q5L2\n" +
                "-----END PRIVATE KEY-----";
    }

    public static String getInvalidPublicKeyPem() {

        return "-----BEGIN PUBLIC KEY-----\n" +
                "INVALID_KEY_CONTENT\n" +
                "-----END PUBLIC KEY-----";
    }

    public static String getInvalidPrivateKeyPem() {

        return "-----BEGIN PRIVATE KEY-----\n" +
                "INVALID_KEY_CONTENT\n" +
                "-----END PRIVATE KEY-----";
    }
}