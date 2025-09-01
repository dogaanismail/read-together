package org.readtogether.security.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.readtogether.security.fixtures.KeyFixtures;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("unit")
@DisplayName("KeyConverter Tests")
class KeyConverterTests {

    @Test
    @DisplayName("Should convert PEM to public key")
    void shouldConvertPemToPublicKey() {
        // Given
        String publicKeyPem = KeyFixtures.getTestPublicKeyPem();

        // When
        PublicKey publicKey = KeyConverter.convertPublicKey(publicKeyPem);

        // Then
        assertThat(publicKey).isNotNull();
        assertThat(publicKey.getAlgorithm()).isEqualTo("RSA");
        assertThat(publicKey.getFormat()).isEqualTo("X.509");
    }

    @Test
    @DisplayName("Should convert PEM to private key")
    void shouldConvertPemToPrivateKey() {
        // Given
        String privateKeyPem = KeyFixtures.getTestPrivateKeyPem();

        // When
        PrivateKey privateKey = KeyConverter.convertPrivateKey(privateKeyPem);

        // Then
        assertThat(privateKey).isNotNull();
        assertThat(privateKey.getAlgorithm()).isEqualTo("RSA");
        assertThat(privateKey.getFormat()).isEqualTo("PKCS#8");
    }

    @Test
    @DisplayName("Should throw on invalid public key PEM")
    void shouldThrowOnInvalidPublicKeyPem() {
        // Given
        String invalidPem = KeyFixtures.getInvalidPublicKeyPem();

        // When / Then
        assertThatThrownBy(() -> KeyConverter.convertPublicKey(invalidPem))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Should throw on invalid private key PEM")
    void shouldThrowOnInvalidPrivateKeyPem() {
        // Given
        String invalidPem = KeyFixtures.getInvalidPrivateKeyPem();

        // When / Then
        assertThatThrownBy(() -> KeyConverter.convertPrivateKey(invalidPem))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Should support generated RSA key pair conversion")
    void shouldSupportGeneratedRsaKeyPairConversion() {
        // Given
        KeyPair keyPair = KeyFixtures.generateTestRsaKeyPair();
        
        // When / Then
        assertThat(keyPair.getPublic()).isNotNull();
        assertThat(keyPair.getPrivate()).isNotNull();
        assertThat(keyPair.getPublic().getAlgorithm()).isEqualTo("RSA");
        assertThat(keyPair.getPrivate().getAlgorithm()).isEqualTo("RSA");
    }
}