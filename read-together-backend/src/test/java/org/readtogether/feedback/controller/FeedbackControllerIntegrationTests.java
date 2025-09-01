package org.readtogether.feedback.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.readtogether.common.BaseIntegrationTest;
import org.readtogether.feedback.common.enums.FeatureRequestCategory;
import org.readtogether.feedback.common.enums.FeatureRequestStatus;
import org.readtogether.feedback.fixtures.RequestFixtures;
import org.readtogether.feedback.model.request.BugReportSubmitRequest;
import org.readtogether.feedback.model.request.FeatureRequestSubmitRequest;
import org.readtogether.user.entity.UserEntity;
import org.readtogether.user.model.request.LoginRequest;
import org.readtogether.user.model.request.RegisterRequest;
import org.readtogether.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.readtogether.feedback.common.enums.FeatureRequestCategory.UI_UX_IMPROVEMENTS;
import static org.readtogether.feedback.common.enums.FeatureRequestStatus.SUBMITTED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("FeedbackController Integration Tests")
class FeedbackControllerIntegrationTests extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("POST /api/v1/feedback/feature-requests should submit feature request with authentication")
    void shouldSubmitFeatureRequestWithAuth() throws Exception {
        // Given: register and login user
        String email = "feature.user" + System.currentTimeMillis() + "@test.local";
        String password = "Password1!";
        String token = registerAndLogin(email, password, "Feature", "User");

        FeatureRequestSubmitRequest request = RequestFixtures.createDefaultFeatureRequestSubmitRequest();

        // When & Then
        mockMvc.perform(post("/api/v1/feedback/feature-requests")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.response").exists())
                .andExpect(jsonPath("$.response.id").exists())
                .andExpect(jsonPath("$.response.title").value(request.getTitle()))
                .andExpect(jsonPath("$.response.description").value(request.getDescription()))
                .andExpect(jsonPath("$.response.category").value(request.getCategory().toString()))
                .andExpect(jsonPath("$.response.priority").value(request.getPriority().toString()))
                .andExpect(jsonPath("$.response.status").value("SUBMITTED"))
                .andExpect(jsonPath("$.response.votes").value(0));
    }

    @Test
    @DisplayName("POST /api/v1/feedback/feature-requests without authentication should return 401")
    void shouldReturn401WhenSubmittingFeatureRequestWithoutAuth() throws Exception {
        FeatureRequestSubmitRequest request = RequestFixtures.createDefaultFeatureRequestSubmitRequest();

        mockMvc.perform(post("/api/v1/feedback/feature-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/v1/feedback/feature-requests with invalid data should return 400")
    void shouldReturn400WhenSubmittingInvalidFeatureRequest() throws Exception {
        // Given: register and login user
        String email = "invalid.feature" + System.currentTimeMillis() + "@test.local";
        String password = "Password1!";
        String token = registerAndLogin(email, password, "Invalid", "Feature");

        // Request with missing required fields
        FeatureRequestSubmitRequest invalidRequest = FeatureRequestSubmitRequest.builder()
                .title("") // Invalid: empty title
                .description(null) // Invalid: null description
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/feedback/feature-requests")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/feedback/bug-reports should submit bug report with authentication")
    void shouldSubmitBugReportWithAuth() throws Exception {
        // Given: register and login user
        String email = "bug.user" + System.currentTimeMillis() + "@test.local";
        String password = "Password1!";
        String token = registerAndLogin(email, password, "Bug", "User");

        BugReportSubmitRequest request = RequestFixtures.createDefaultBugReportSubmitRequest();

        // When & Then
        mockMvc.perform(post("/api/v1/feedback/bug-reports")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.response").exists())
                .andExpect(jsonPath("$.response.id").exists())
                .andExpect(jsonPath("$.response.title").value(request.getTitle()))
                .andExpect(jsonPath("$.response.severity").value(request.getSeverity().toString()))
                .andExpect(jsonPath("$.response.stepsToReproduce").value(request.getStepsToReproduce()))
                .andExpect(jsonPath("$.response.status").value("SUBMITTED"));
    }

    @Test
    @DisplayName("POST /api/v1/feedback/bug-reports without authentication should return 401")
    void shouldReturn401WhenSubmittingBugReportWithoutAuth() throws Exception {
        BugReportSubmitRequest request = RequestFixtures.createDefaultBugReportSubmitRequest();

        mockMvc.perform(post("/api/v1/feedback/bug-reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/v1/feedback/feature-requests should return feature requests without authentication")
    void shouldReturnFeatureRequestsWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/v1/feedback/feature-requests")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.response").exists())
                .andExpect(jsonPath("$.response.content").isArray());
    }

    @Test
    @DisplayName("GET /api/v1/feedback/feature-requests with category filter should work")
    void shouldReturnFeatureRequestsWithCategoryFilter() throws Exception {
        mockMvc.perform(get("/api/v1/feedback/feature-requests")
                        .param("category", UI_UX_IMPROVEMENTS.toString())
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.response").exists())
                .andExpect(jsonPath("$.response.content").isArray());
    }

    @Test
    @DisplayName("GET /api/v1/feedback/feature-requests with status filter should work")
    void shouldReturnFeatureRequestsWithStatusFilter() throws Exception {
        mockMvc.perform(get("/api/v1/feedback/feature-requests")
                        .param("status", SUBMITTED.toString())
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.response").exists())
                .andExpect(jsonPath("$.response.content").isArray());
    }

    @Test
    @DisplayName("GET /api/v1/feedback/bug-reports should return bug reports without authentication")
    void shouldReturnBugReportsWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/v1/feedback/bug-reports")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.response").exists())
                .andExpect(jsonPath("$.response.content").isArray());
    }

    @Test
    @DisplayName("PUT /api/v1/feedback/feature-requests/{id}/vote should vote for feature request with authentication")
    void shouldVoteForFeatureRequestWithAuth() throws Exception {
        // Given: register and login user, submit a feature request
        String email = "vote.user" + System.currentTimeMillis() + "@test.local";
        String password = "Password1!";
        String token = registerAndLogin(email, password, "Vote", "User");

        FeatureRequestSubmitRequest request = RequestFixtures.createDefaultFeatureRequestSubmitRequest();
        MvcResult submitResult = mockMvc.perform(post("/api/v1/feedback/feature-requests")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode submitNode = objectMapper.readTree(submitResult.getResponse().getContentAsString());
        String featureRequestId = submitNode.path("response").path("id").asText();

        // When & Then: vote for the feature request
        mockMvc.perform(put("/api/v1/feedback/feature-requests/{id}/vote", featureRequestId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true));
    }

    @Test
    @DisplayName("PUT /api/v1/feedback/feature-requests/{id}/vote without authentication should return 401")
    void shouldReturn401WhenVotingWithoutAuth() throws Exception {
        UUID fakeId = UUID.randomUUID();

        mockMvc.perform(put("/api/v1/feedback/feature-requests/{id}/vote", fakeId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/v1/feedback/statistics should return statistics without authentication")
    void shouldReturnStatisticsWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/v1/feedback/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.response").exists())
                .andExpect(jsonPath("$.response.totalFeatureRequests").isNumber())
                .andExpect(jsonPath("$.response.featuresImplemented").isNumber())
                .andExpect(jsonPath("$.response.totalBugReports").isNumber())
                .andExpect(jsonPath("$.response.bugsFixed").isNumber())
                .andExpect(jsonPath("$.response.criticalBugs").isNumber())
                .andExpect(jsonPath("$.response.bugsInProgress").isNumber());
    }

    private String registerAndLogin(
            String email,
            String password,
            String firstName,
            String lastName) throws Exception {

        // Register user
        RegisterRequest registerRequest = org.readtogether.user.fixtures.RequestFixtures.createRegisterRequest(
                email,
                password,
                firstName,
                lastName,
                "user"
        );

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        // Login and get token
        return loginAndGetAccessToken(email, password);
    }

    private String loginAndGetAccessToken(
            String email,
            String password) throws Exception {

        LoginRequest login = org.readtogether.user.fixtures.RequestFixtures.createLoginRequest(email, password);

        MvcResult loginResult = mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode loginNode = objectMapper.readTree(loginResult.getResponse().getContentAsString());

        return loginNode
                .path("response")
                .path("accessToken")
                .asText();
    }

    private UUID getUserIdByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(UserEntity::getId)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }
}