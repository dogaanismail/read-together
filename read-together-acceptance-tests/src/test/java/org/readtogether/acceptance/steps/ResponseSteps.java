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
}