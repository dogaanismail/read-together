package org.readtogether.user.factory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.readtogether.user.entity.UserEntity;
import org.readtogether.user.model.request.RegisterRequest;
import org.readtogether.user.common.enums.UserType;
import org.readtogether.user.fixtures.RequestFixtures;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserEntityFactory Tests")
class UserEntityFactoryTests {

    @Test
    @DisplayName("Should create user entity from register request with USER role")
    void shouldCreateUserEntityFromRegisterRequestWithUserRole() {
        // Given
        RegisterRequest request = RequestFixtures.createDefaultRegisterRequest();

        // When
        UserEntity result = UserEntityFactory.getUserEntityByRegisterRequest(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("newuser@example.com");
        assertThat(result.getFirstName()).isEqualTo("New");
        assertThat(result.getLastName()).isEqualTo("User");
        assertThat(result.getUserType()).isEqualTo(UserType.USER);
    }

    @Test
    @DisplayName("Should create user entity from register request with ADMIN role")
    void shouldCreateUserEntityFromRegisterRequestWithAdminRole() {
        // Given
        RegisterRequest request = RequestFixtures.createAdminRegisterRequest();

        // When
        UserEntity result = UserEntityFactory.getUserEntityByRegisterRequest(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("newadmin@example.com");
        assertThat(result.getFirstName()).isEqualTo("New");
        assertThat(result.getLastName()).isEqualTo("Admin");
        assertThat(result.getUserType()).isEqualTo(UserType.ADMIN);
    }

    @Test
    @DisplayName("Should default to USER role when role is not admin")
    void shouldDefaultToUserRoleWhenRoleIsNotAdmin() {
        // Given
        RegisterRequest request = RequestFixtures.createRegisterRequest(
                "test@example.com", "Test", "User", "regular"
        );

        // When
        UserEntity result = UserEntityFactory.getUserEntityByRegisterRequest(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUserType()).isEqualTo(UserType.USER);
    }

    @Test
    @DisplayName("Should handle case insensitive admin role")
    void shouldHandleCaseInsensitiveAdminRole() {
        // Given
        RegisterRequest request = RequestFixtures.createRegisterRequest(
                "admin@example.com", "Admin", "User", "ADMIN"
        );

        // When
        UserEntity result = UserEntityFactory.getUserEntityByRegisterRequest(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUserType()).isEqualTo(UserType.ADMIN);
    }

    @Test
    @DisplayName("Should handle mixed case admin role")
    void shouldHandleMixedCaseAdminRole() {
        // Given
        RegisterRequest request = RequestFixtures.createRegisterRequest(
                "admin@example.com", "Admin", "User", "Admin"
        );

        // When
        UserEntity result = UserEntityFactory.getUserEntityByRegisterRequest(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUserType()).isEqualTo(UserType.ADMIN);
    }

    @Test
    @DisplayName("Should handle null role as user")
    void shouldHandleNullRoleAsUser() {
        // Given
        RegisterRequest request = RequestFixtures.createRegisterRequest(
                "test@example.com", "Test", "User", null
        );

        // When
        UserEntity result = UserEntityFactory.getUserEntityByRegisterRequest(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUserType()).isEqualTo(UserType.USER);
    }

    @Test
    @DisplayName("Should handle empty role as user")
    void shouldHandleEmptyRoleAsUser() {
        // Given
        RegisterRequest request = RequestFixtures.createRegisterRequest(
                "test@example.com", "Test", "User", ""
        );

        // When
        UserEntity result = UserEntityFactory.getUserEntityByRegisterRequest(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUserType()).isEqualTo(UserType.USER);
    }

    @Test
    @DisplayName("Should handle special characters in names")
    void shouldHandleSpecialCharactersInNames() {
        // Given
        RegisterRequest request = RequestFixtures.createRegisterRequest(
                "jose@example.com", "José", "García-López", "user"
        );

        // When
        UserEntity result = UserEntityFactory.getUserEntityByRegisterRequest(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("José");
        assertThat(result.getLastName()).isEqualTo("García-López");
        assertThat(result.getUserType()).isEqualTo(UserType.USER);
    }

    @Test
    @DisplayName("Should handle long names")
    void shouldHandleLongNames() {
        // Given
        RegisterRequest request = RequestFixtures.createRegisterRequest(
                "longname@example.com",
                "VeryLongFirstNameThatExceedsNormalLength",
                "VeryLongLastNameThatAlsoExceedsNormalLength",
                "user"
        );

        // When
        UserEntity result = UserEntityFactory.getUserEntityByRegisterRequest(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("VeryLongFirstNameThatExceedsNormalLength");
        assertThat(result.getLastName()).isEqualTo("VeryLongLastNameThatAlsoExceedsNormalLength");
        assertThat(result.getUserType()).isEqualTo(UserType.USER);
    }
}
