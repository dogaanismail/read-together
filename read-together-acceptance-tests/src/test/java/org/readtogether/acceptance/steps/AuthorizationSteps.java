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
 * Step definitions for authorization and access control scenarios.
 */
@Slf4j
public class AuthorizationSteps {
    
    private Response lastResponse;
    private String privateRoomId;
    private String publicRoomId;
    private String targetUserId;
    
    @When("I attempt to access the second user's private information")
    public void i_attempt_to_access_the_second_users_private_information() {
        // Try to access sensitive endpoints that should be restricted
        lastResponse = ApiClient.getAuthenticated("/users/sensitive-info");
        
        log.debug("Attempted to access second user's private info, status: {}", lastResponse.getStatusCode());
    }
    
    @When("I attempt to update the second user's profile")
    public void i_attempt_to_update_the_second_users_profile() {
        // Try to update another user's profile (this would need the target user ID)
        Map<String, Object> updateRequest = Fixtures.Profile.createDefaultProfileUpdateRequest();
        
        // Attempt to update via admin endpoint or with user ID parameter
        lastResponse = ApiClient.putAuthenticated("/admin/users/profile", updateRequest);
        
        log.debug("Attempted to update second user's profile, status: {}", lastResponse.getStatusCode());
    }
    
    @When("I attempt to access admin endpoint {string}")
    public void i_attempt_to_access_admin_endpoint(String endpoint) {
        lastResponse = ApiClient.getAuthenticated(endpoint);
        
        log.debug("Attempted to access admin endpoint {}, status: {}", endpoint, lastResponse.getStatusCode());
    }
    
    @When("I attempt to update my user type to {string}")
    public void i_attempt_to_update_my_user_type_to(String userType) {
        Map<String, Object> updateRequest = Map.of("userType", userType);
        
        lastResponse = ApiClient.putAuthenticated("/users/profile", updateRequest);
        
        log.debug("Attempted to update user type to {}, status: {}", userType, lastResponse.getStatusCode());
    }
    
    @When("I attempt to delete the second user's account")
    public void i_attempt_to_delete_the_second_users_account() {
        // Try to delete another user's account
        lastResponse = ApiClient.deleteAuthenticated("/admin/users/" + targetUserId);
        
        log.debug("Attempted to delete second user's account, status: {}", lastResponse.getStatusCode());
    }
    
    @Given("the user creates a private reading room")
    public void the_user_creates_a_private_reading_room() {
        // Create a private reading room
        Map<String, Object> roomRequest = Map.of(
                "name", "Private Room",
                "description", "A private reading room",
                "isPublic", false,
                "password", "roompass123"
        );
        
        Response response = ApiClient.postAuthenticated("/rooms", roomRequest);
        
        if (response.getStatusCode() == 200 || response.getStatusCode() == 201) {
            privateRoomId = response.jsonPath().getString("id");
        }
        
        log.debug("Created private reading room with ID: {}", privateRoomId);
    }
    
    @Given("the user creates a public reading room")
    public void the_user_creates_a_public_reading_room() {
        // Create a public reading room
        Map<String, Object> roomRequest = Map.of(
                "name", "Public Room",
                "description", "A public reading room",
                "isPublic", true
        );
        
        Response response = ApiClient.postAuthenticated("/rooms", roomRequest);
        
        if (response.getStatusCode() == 200 || response.getStatusCode() == 201) {
            publicRoomId = response.jsonPath().getString("id");
        }
        
        log.debug("Created public reading room with ID: {}", publicRoomId);
    }
    
    @When("I attempt to access the private reading room")
    public void i_attempt_to_access_the_private_reading_room() {
        assertThat(privateRoomId)
                .as("Private room ID should be available")
                .isNotNull();
        
        lastResponse = ApiClient.getAuthenticated("/rooms/" + privateRoomId);
        
        log.debug("Attempted to access private room {}, status: {}", privateRoomId, lastResponse.getStatusCode());
    }
    
    @When("I attempt to access the public reading room")
    public void i_attempt_to_access_the_public_reading_room() {
        assertThat(publicRoomId)
                .as("Public room ID should be available")
                .isNotNull();
        
        lastResponse = ApiClient.getAuthenticated("/rooms/" + publicRoomId);
        
        log.debug("Attempted to access public room {}, status: {}", publicRoomId, lastResponse.getStatusCode());
    }
    
    @When("I attempt to modify the first user's privacy settings")
    public void i_attempt_to_modify_the_first_users_privacy_settings() {
        Map<String, Object> privacyRequest = Map.of(
                "profileVisibility", "private",
                "showEmail", false
        );
        
        // Try to modify another user's privacy settings (would need admin privileges)
        lastResponse = ApiClient.putAuthenticated("/admin/privacy-settings", privacyRequest);
        
        log.debug("Attempted to modify first user's privacy settings, status: {}", lastResponse.getStatusCode());
    }
    
    @When("I attempt to access the first user's notification preferences")
    public void i_attempt_to_access_the_first_users_notification_preferences() {
        // Try to access another user's notification preferences
        lastResponse = ApiClient.getAuthenticated("/admin/notification-preferences");
        
        log.debug("Attempted to access first user's notification preferences, status: {}", lastResponse.getStatusCode());
    }
    
    @Then("the response should contain the reading room information")
    public void the_response_should_contain_the_reading_room_information() {
        assertThat(lastResponse.jsonPath().getString("id"))
                .as("Response should contain room ID")
                .isNotNull();
        
        assertThat(lastResponse.jsonPath().getString("name"))
                .as("Response should contain room name")
                .isNotNull();
        
        assertThat(lastResponse.jsonPath().getString("description"))
                .as("Response should contain room description")
                .isNotNull();
    }
}