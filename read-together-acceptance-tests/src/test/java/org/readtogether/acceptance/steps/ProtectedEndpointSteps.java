package org.readtogether.acceptance.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.readtogether.acceptance.support.ApiClient;
import org.readtogether.acceptance.support.Fixtures;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Step definitions for accessing protected endpoints.
 */
@Slf4j
public class ProtectedEndpointSteps {
    
    private Response lastResponse;
    
    @When("I access the protected endpoint {string} with the access token")
    public void i_access_the_protected_endpoint_with_the_access_token(String endpoint) {
        lastResponse = ApiClient.getAuthenticated(endpoint);
        
        log.debug("Accessed protected endpoint {} with status: {}", endpoint, lastResponse.getStatusCode());
    }
    
    @When("I access the protected endpoint {string} without authentication")
    public void i_access_the_protected_endpoint_without_authentication(String endpoint) {
        lastResponse = ApiClient.get(endpoint);
        
        log.debug("Accessed endpoint {} without auth with status: {}", endpoint, lastResponse.getStatusCode());
    }
    
    @When("I access the protected endpoint {string} with invalid token {string}")
    public void i_access_the_protected_endpoint_with_invalid_token(String endpoint, String invalidToken) {
        lastResponse = ApiClient.getAuthenticatedRequest(invalidToken)
                .when()
                .get(endpoint);
        
        log.debug("Accessed endpoint {} with invalid token, status: {}", endpoint, lastResponse.getStatusCode());
    }
    
    @When("I access the protected endpoint {string} with the expired token")
    public void i_access_the_protected_endpoint_with_the_expired_token(String endpoint) {
        // The expired token is already set in ApiClient from previous steps
        lastResponse = ApiClient.getAuthenticated(endpoint);
        
        log.debug("Accessed endpoint {} with expired token, status: {}", endpoint, lastResponse.getStatusCode());
    }
    
    @When("I access the protected endpoint {string} with malformed token {string}")
    public void i_access_the_protected_endpoint_with_malformed_token(String endpoint, String malformedToken) {
        lastResponse = ApiClient.getAuthenticatedRequest(malformedToken)
                .when()
                .get(endpoint);
        
        log.debug("Accessed endpoint {} with malformed token, status: {}", endpoint, lastResponse.getStatusCode());
    }
    
    @Given("I have logged in as a regular user and obtained an access token")
    public void i_have_logged_in_as_a_regular_user_and_obtained_an_access_token() {
        // Register and login as a regular user (not admin)
        String email = Fixtures.User.generateUniqueEmail();
        String password = "Password1!";
        
        // Create regular user via registration
        Map<String, Object> registerRequest = Fixtures.User.createRegisterRequest(
                email, password, "Test", "User", "user"
        );
        
        Response regResponse = ApiClient.post("/users/register", registerRequest);
        log.debug("User registration status: {}", regResponse.getStatusCode());
        
        // Login to get access token
        Map<String, Object> loginRequest = Fixtures.Auth.createLoginRequest(email, password);
        lastResponse = ApiClient.post("/users/login", loginRequest);
        
        if (lastResponse.getStatusCode() == 200) {
            String accessToken = lastResponse.jsonPath().getString("response.accessToken");
            ApiClient.setAccessToken(accessToken);
            log.debug("Regular user logged in successfully");
        }
    }
    
    @When("I access the admin endpoint {string} with the access token")
    public void i_access_the_admin_endpoint_with_the_access_token(String endpoint) {
        i_access_the_protected_endpoint_with_the_access_token(endpoint);
    }
    
    @Then("the response should contain my user information")
    public void the_response_should_contain_my_user_information() {
        // Verify the response contains user information structure
        assertThat(lastResponse.jsonPath().getString("id"))
                .as("Response should contain user ID")
                .isNotNull();
        
        assertThat(lastResponse.jsonPath().getString("email"))
                .as("Response should contain user email")
                .isNotNull();
        
        assertThat(lastResponse.jsonPath().getString("firstName"))
                .as("Response should contain user first name")
                .isNotNull();
        
        assertThat(lastResponse.jsonPath().getString("lastName"))
                .as("Response should contain user last name")
                .isNotNull();
    }
}