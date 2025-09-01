package org.readtogether.feedback.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.readtogether.common.BaseIntegrationTest;
import org.readtogether.feedback.fixtures.RequestFixtures;
import org.readtogether.feedback.model.request.BugReportSubmitRequest;
import org.readtogether.feedback.model.request.FeatureRequestSubmitRequest;
import org.readtogether.user.model.request.LoginRequest;
import org.readtogether.user.model.request.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.readtogether.feedback.common.enums.FeatureRequestCategory.UI_UX_IMPROVEMENTS;
import static org.readtogether.feedback.common.enums.FeatureRequestStatus.SUBMITTED;
import static org.readtogether.user.fixtures.RequestFixtures.createRegisterRequest;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag("integration")
@DisplayName("FeedbackController Integration Tests")
class FeedbackControllerIntegrationTests extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
                        .contentType(APPLICATION_JSON)
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
                        .contentType(APPLICATION_JSON)
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
                        .contentType(APPLICATION_JSON)
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
                        .contentType(APPLICATION_JSON)
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
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/v1/feedback/feature-requests should return feature requests with authentication")
    void shouldReturnFeatureRequestsWithAuth() throws Exception {
        // Given: register and login user
        String email = "public.feature" + System.currentTimeMillis() + "@test.local";
        String password = "Password1!";
        String token = registerAndLogin(email, password, "Public", "Feature");

        mockMvc.perform(get("/api/v1/feedback/feature-requests")
                        .header("Authorization", "Bearer " + token)
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.response").exists())
                .andExpect(jsonPath("$.response.content").isArray());
    }

    @Test
    @DisplayName("GET /api/v1/feedback/feature-requests without authentication should return 401")
    void shouldReturn401WhenGettingFeatureRequestsWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/v1/feedback/feature-requests")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/v1/feedback/feature-requests with category filter should work with authentication")
    void shouldReturnFeatureRequestsWithCategoryFilter() throws Exception {
        // Given: register and login user
        String email = "category.filter" + System.currentTimeMillis() + "@test.local";
        String password = "Password1!";
        String token = registerAndLogin(email, password, "Category", "Filter");

        mockMvc.perform(get("/api/v1/feedback/feature-requests")
                        .header("Authorization", "Bearer " + token)
                        .param("category", UI_UX_IMPROVEMENTS.toString())
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.response").exists())
                .andExpect(jsonPath("$.response.content").isArray());
    }

    @Test
    @DisplayName("GET /api/v1/feedback/feature-requests with status filter should work with authentication")
    void shouldReturnFeatureRequestsWithStatusFilter() throws Exception {
        // Given: register and login user
        String email = "status.filter" + System.currentTimeMillis() + "@test.local";
        String password = "Password1!";
        String token = registerAndLogin(email, password, "Status", "Filter");

        mockMvc.perform(get("/api/v1/feedback/feature-requests")
                        .header("Authorization", "Bearer " + token)
                        .param("status", SUBMITTED.toString())
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.response").exists())
                .andExpect(jsonPath("$.response.content").isArray());
    }

    @Test
    @DisplayName("GET /api/v1/feedback/bug-reports should return bug reports with authentication")
    void shouldReturnBugReportsWithAuth() throws Exception {
        // Given: register and login user  
        String email = "public.bug" + System.currentTimeMillis() + "@test.local";
        String password = "Password1!";
        String token = registerAndLogin(email, password, "Public", "Bug");

        mockMvc.perform(get("/api/v1/feedback/bug-reports")
                        .header("Authorization", "Bearer " + token)
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.response").exists())
                .andExpect(jsonPath("$.response.content").isArray());
    }

    @Test
    @DisplayName("GET /api/v1/feedback/bug-reports without authentication should return 401")
    void shouldReturn401WhenGettingBugReportsWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/v1/feedback/bug-reports")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isUnauthorized());
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
                        .contentType(APPLICATION_JSON)
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
    @DisplayName("GET /api/v1/feedback/statistics should return statistics with authentication")
    void shouldReturnStatisticsWithAuth() throws Exception {
        // Given: register and login user
        String email = "public.stats" + System.currentTimeMillis() + "@test.local";
        String password = "Password1!";
        String token = registerAndLogin(email, password, "Public", "Stats");

        mockMvc.perform(get("/api/v1/feedback/statistics")
                        .header("Authorization", "Bearer " + token))
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

    @Test
    @DisplayName("GET /api/v1/feedback/statistics without authentication should return 401")
    void shouldReturn401WhenGettingStatisticsWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/v1/feedback/statistics"))
                .andExpect(status().isUnauthorized());
    }

    private String registerAndLogin(
            String email,
            String password,
            String firstName,
            String lastName) throws Exception {

        // Register user
        RegisterRequest registerRequest = createRegisterRequest(
                email,
                password,
                firstName,
                lastName,
                "user"
        );

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(APPLICATION_JSON)
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
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode loginNode = objectMapper.readTree(loginResult.getResponse().getContentAsString());

        return loginNode
                .path("response")
                .path("accessToken")
                .asText();
    }

}