package org.readtogether.acceptance.steps;

import io.cucumber.java.en.Then;
import lombok.extern.slf4j.Slf4j;
import org.readtogether.acceptance.support.ApiClient;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Step definitions for common HTTP response assertions.
 * Handles status codes and common response validations.
 */
@Slf4j
public class ResponseSteps {
    
    @Then("I should receive a 200 OK response")
    public void i_should_receive_a_200_ok_response() {
        var response = ApiClient.getLastResponse();
        assertThat(response.getStatusCode())
                .as("Should receive 200 OK response")
                .isEqualTo(200);
        log.debug("Received expected 200 OK response");
    }
    
    @Then("I should receive a 400 bad request response")
    public void i_should_receive_a_400_bad_request_response() {
        var response = ApiClient.getLastResponse();
        assertThat(response.getStatusCode())
                .as("Should receive 400 Bad Request response")
                .isEqualTo(400);
        log.debug("Received expected 400 Bad Request response");
    }
    
    @Then("I should receive a 401 unauthorized response")
    public void i_should_receive_a_401_unauthorized_response() {
        var response = ApiClient.getLastResponse();
        assertThat(response.getStatusCode())
                .as("Should receive 401 Unauthorized response")
                .isEqualTo(401);
        log.debug("Received expected 401 Unauthorized response");
    }
    
    @Then("I should receive a 403 forbidden response")
    public void i_should_receive_a_403_forbidden_response() {
        var response = ApiClient.getLastResponse();
        assertThat(response.getStatusCode())
                .as("Should receive 403 Forbidden response")
                .isEqualTo(403);
        log.debug("Received expected 403 Forbidden response");
    }
    
    @Then("I should receive a 404 not found response")
    public void i_should_receive_a_404_not_found_response() {
        var response = ApiClient.getLastResponse();
        assertThat(response.getStatusCode())
                .as("Should receive 404 Not Found response")
                .isEqualTo(404);
        log.debug("Received expected 404 Not Found response");
    }
    
    @Then("I should receive a 409 conflict response")
    public void i_should_receive_a_409_conflict_response() {
        var response = ApiClient.getLastResponse();
        assertThat(response.getStatusCode())
                .as("Should receive 409 Conflict response")
                .isEqualTo(409);
        log.debug("Received expected 409 Conflict response");
    }
    
    @Then("the response should contain an error message")
    public void the_response_should_contain_an_error_message() {
        var response = ApiClient.getLastResponse();
        var body = response.getBody().asString();
        assertThat(body)
                .as("Response should contain an error message")
                .containsAnyOf("error", "message", "Error", "Message", "invalid", "failed");
        log.debug("Response contains error message: {}", body);
    }
    
    @Then("the response should contain validation errors")
    public void the_response_should_contain_validation_errors() {
        var response = ApiClient.getLastResponse();
        var body = response.getBody().asString();
        assertThat(body)
                .as("Response should contain validation errors")
                .containsAnyOf("validation", "Validation", "required", "invalid", "error", "errors");
        log.debug("Response contains validation errors: {}", body);
    }
    
    @Then("the response should contain an authentication error")
    public void the_response_should_contain_an_authentication_error() {
        var response = ApiClient.getLastResponse();
        var body = response.getBody().asString();
        assertThat(body)
                .as("Response should contain authentication error")
                .containsAnyOf("authentication", "unauthorized", "Unauthorized", "token", "login", "credential");
        log.debug("Response contains authentication error: {}", body);
    }
    
    @Then("the response should contain an authentication error about expired token")
    public void the_response_should_contain_an_authentication_error_about_expired_token() {
        var response = ApiClient.getLastResponse();
        var body = response.getBody().asString();
        assertThat(body)
                .as("Response should contain expired token error")
                .containsAnyOf("expired", "Expired", "token", "Token", "invalid", "authentication");
        log.debug("Response contains expired token error: {}", body);
    }
    
    @Then("the response should contain an authorization error")
    public void the_response_should_contain_an_authorization_error() {
        var response = ApiClient.getLastResponse();
        var body = response.getBody().asString();
        assertThat(body)
                .as("Response should contain authorization error")
                .containsAnyOf("authorization", "forbidden", "Forbidden", "access", "permission", "denied");
        log.debug("Response contains authorization error: {}", body);
    }
}