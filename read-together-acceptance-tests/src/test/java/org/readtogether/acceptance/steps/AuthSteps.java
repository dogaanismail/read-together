package org.readtogether.acceptance.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.readtogether.acceptance.support.ApiClient;
import org.readtogether.acceptance.support.Fixtures;
import org.readtogether.acceptance.support.JwtUtils;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Step definitions for authentication-related scenarios.
 */
@Slf4j
public class AuthSteps {

    private Response lastResponse;
    private String currentAccessToken;
    private String currentRefreshToken;
    private String originalAccessToken;
    private Map<String, Object> currentUser;

    @Given("a user exists with email {string} and password {string}")
    public void a_user_exists_with_email_and_password(String email, String password) {

        Map<String, Object> registerRequest = Fixtures.User.createRegisterRequest(
                email,
                password,
                "John",
                "Doe"
        );

        Response registerResponse = ApiClient.post("/users/register", registerRequest);

        if (registerResponse.getStatusCode() == 200) {
            log.debug("Successfully registered new user with email: {}", email);
        } else if (registerResponse.getStatusCode() == 409) {
            log.debug("User already exists with email: {}, proceeding to login", email);
        } else {
            log.warn("Unexpected registration response for {}: {} - {}",
                    email, registerResponse.getStatusCode(), registerResponse.getBody().asString());
        }

        Map<String, Object> loginRequest = Fixtures.Auth.createLoginRequest(email, password);
        Response loginResponse = ApiClient.post("/users/login", loginRequest);

        assertThat(loginResponse.getStatusCode())
                .as("User should be able to login after registration")
                .isEqualTo(200);

        if (loginResponse.getStatusCode() == 200) {
            currentUser = loginResponse.jsonPath().getMap("response.user");
            log.debug("User verified and logged in successfully: {}", email);
        }
    }

    @When("I login with email {string} and password {string}")
    public void i_login_with_email_and_password(String email, String password) {
        Map<String, Object> loginRequest = Fixtures.Auth.createLoginRequest(email, password);

        lastResponse = ApiClient.post("/users/login", loginRequest);

        // Extract tokens if login was successful
        if (lastResponse.getStatusCode() == 200) {
            extractTokensFromResponse();
        }

        log.debug("Login attempt with email: {} resulted in status: {}", email, lastResponse.getStatusCode());
    }

    @Given("I have logged in and obtained tokens")
    public void i_have_logged_in_and_obtained_tokens() {
        String email = Fixtures.User.generateUniqueEmail();
        String password = Fixtures.Common.DEFAULT_PASSWORD;

        // First, register the user
        a_user_exists_with_email_and_password(email, password);

        // Then login to get tokens
        i_login_with_email_and_password(email, password);

        assertThat(lastResponse.getStatusCode())
                .as("Login should succeed")
                .isEqualTo(200);

        assertThat(currentAccessToken)
                .as("Access token should be obtained")
                .isNotNull();

        assertThat(currentRefreshToken)
                .as("Refresh token should be obtained")
                .isNotNull();
    }

    @Given("I have logged in and obtained an access token")
    public void i_have_logged_in_and_obtained_an_access_token() {
        i_have_logged_in_and_obtained_tokens();

        // Set the access token in ApiClient for future authenticated requests
        ApiClient.setAccessToken(currentAccessToken);
    }

    @When("I use the refresh token to get a new access token")
    public void i_use_the_refresh_token_to_get_a_new_access_token() {
        assertThat(currentRefreshToken)
                .as("Refresh token should be available")
                .isNotNull();

        // Store original access token for comparison
        originalAccessToken = currentAccessToken;

        Map<String, Object> refreshRequest = Fixtures.Auth.createTokenRefreshRequest(currentRefreshToken);

        lastResponse = ApiClient.post("/auth/refresh-token", refreshRequest);

        // Extract new tokens if refresh was successful
        if (lastResponse.getStatusCode() == 200) {
            extractTokensFromResponse();
        }

        log.debug("Token refresh resulted in status: {}", lastResponse.getStatusCode());
    }

    @When("I use an invalid refresh token {string}")
    public void i_use_an_invalid_refresh_token(String invalidToken) {
        Map<String, Object> refreshRequest = Fixtures.Auth.createTokenRefreshRequest(invalidToken);

        lastResponse = ApiClient.post("/auth/refresh-token", refreshRequest);

        log.debug("Invalid token refresh resulted in status: {}", lastResponse.getStatusCode());
    }

    @When("I use an empty refresh token")
    public void i_use_an_empty_refresh_token() {
        Map<String, Object> refreshRequest = Fixtures.Auth.createTokenRefreshRequest("");

        lastResponse = ApiClient.post("/auth/refresh-token", refreshRequest);
    }

    @When("I use a malformed refresh token {string}")
    public void i_use_a_malformed_refresh_token(String malformedToken) {
        Map<String, Object> refreshRequest = Fixtures.Auth.createTokenRefreshRequest(malformedToken);

        lastResponse = ApiClient.post("/auth/refresh-token", refreshRequest);
    }

    @Given("I have an expired refresh token")
    public void i_have_an_expired_refresh_token() {
        // For testing purposes, we'll use a clearly expired token
        // In a real scenario, this would be an actual expired token from the system
        currentRefreshToken = "expired.refresh.token";
    }

    @Given("I have an expired access token")
    public void i_have_an_expired_access_token() {
        // For testing purposes, we'll use a clearly expired token
        currentAccessToken = "expired.access.token";
        ApiClient.setAccessToken(currentAccessToken);
    }

    @When("I use the expired refresh token to get a new access token")
    public void i_use_the_expired_refresh_token_to_get_a_new_access_token() {
        i_use_the_refresh_token_to_get_a_new_access_token();
    }

    @Then("I should receive a successful login response")
    public void i_should_receive_a_successful_login_response() {
        assertThat(lastResponse.getStatusCode())
                .as("Login should return 200 OK")
                .isEqualTo(200);

        assertThat(lastResponse.jsonPath().getBoolean("isSuccess"))
                .as("Login response should indicate success")
                .isTrue();
    }

    @Then("I should receive a successful token refresh response")
    public void i_should_receive_a_successful_token_refresh_response() {
        assertThat(lastResponse.getStatusCode())
                .as("Token refresh should return 200 OK")
                .isEqualTo(200);

        assertThat(lastResponse.jsonPath().getBoolean("isSuccess"))
                .as("Token refresh response should indicate success")
                .isTrue();
    }

    @Then("I should receive an access token")
    public void i_should_receive_an_access_token() {
        assertThat(currentAccessToken)
                .as("Access token should be present")
                .isNotNull()
                .isNotEmpty();
    }

    @Then("I should receive a refresh token")
    public void i_should_receive_a_refresh_token() {
        assertThat(currentRefreshToken)
                .as("Refresh token should be present")
                .isNotNull()
                .isNotEmpty();
    }

    @Then("I should receive a new access token")
    public void i_should_receive_a_new_access_token() {
        i_should_receive_an_access_token();
    }

    @Then("I should receive a new refresh token")
    public void i_should_receive_a_new_refresh_token() {
        i_should_receive_a_refresh_token();
    }

    @Then("the access token should be structurally valid")
    public void the_access_token_should_be_structurally_valid() {
        assertThat(JwtUtils.hasValidStructure(currentAccessToken))
                .as("Access token should have valid JWT structure")
                .isTrue();
    }

    @Then("the refresh token should be structurally valid")
    public void the_refresh_token_should_be_structurally_valid() {
        assertThat(JwtUtils.hasValidStructure(currentRefreshToken))
                .as("Refresh token should have valid JWT structure")
                .isTrue();
    }

    @Then("the new access token should be structurally valid")
    public void the_new_access_token_should_be_structurally_valid() {
        the_access_token_should_be_structurally_valid();
    }

    @Then("the new refresh token should be structurally valid")
    public void the_new_refresh_token_should_be_structurally_valid() {
        the_refresh_token_should_be_structurally_valid();
    }

    @Then("the new access token should be different from the original")
    public void the_new_access_token_should_be_different_from_the_original() {
        assertThat(currentAccessToken)
                .as("New access token should be different from original")
                .isNotEqualTo(originalAccessToken);
    }

    @Then("I should not receive any tokens")
    public void i_should_not_receive_any_tokens() {
        assertThat(lastResponse.jsonPath().getString("response.accessToken"))
                .as("Should not receive access token on failed login")
                .isNullOrEmpty();

        assertThat(lastResponse.jsonPath().getString("response.refreshToken"))
                .as("Should not receive refresh token on failed login")
                .isNullOrEmpty();
    }

    @Then("I should not receive new tokens")
    public void i_should_not_receive_new_tokens() {
        i_should_not_receive_any_tokens();
    }

    /**
     * Helper method to extract tokens from login/refresh response.
     */
    private void extractTokensFromResponse() {
        currentAccessToken = lastResponse.jsonPath().getString("response.accessToken");
        currentRefreshToken = lastResponse.jsonPath().getString("response.refreshToken");

        // Extract user information if available
        currentUser = lastResponse.jsonPath().getMap("response.user");

        log.debug("Extracted tokens - Access token present: {}, Refresh token present: {}, User info present: {}",
                currentAccessToken != null, currentRefreshToken != null, currentUser != null);
    }
}