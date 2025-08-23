package org.readtogether.readingroom.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.readtogether.common.BaseIntegrationTest;
import org.readtogether.readingroom.fixtures.ReadingRoomRequestFixtures;
import org.readtogether.readingroom.model.request.CreateReadingRoomRequest;
import org.readtogether.readingroom.model.request.UpdateRoomSettingsRequest;
import org.readtogether.user.fixtures.RequestFixtures;
import org.readtogether.user.model.request.LoginRequest;
import org.readtogether.user.model.request.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("RoomSettingsController Integration Tests")
class RoomSettingsControllerIntegrationTests extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("PUT /api/v1/rooms/{roomId}/settings should update room settings")
    void shouldUpdateRoomSettings() throws Exception {
        // Given: user creates room
        String hostEmail = "host@test.local";
        String hostPassword = "Password1!";
        String hostToken = registerAndLogin(hostEmail, hostPassword, "Host", "User");

        // Create room first
        CreateReadingRoomRequest createRequest = ReadingRoomRequestFixtures.createDefaultCreateReadingRoomRequest();
        MvcResult roomResult = mockMvc.perform(post("/api/v1/rooms")
                        .header("Authorization", "Bearer " + hostToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode roomNode = objectMapper.readTree(roomResult.getResponse().getContentAsString());
        String roomId = roomNode.path("id").asText();

        // When: update room settings
        UpdateRoomSettingsRequest updateRequest = ReadingRoomRequestFixtures.createPrivateUpdateRoomSettingsRequest();

        mockMvc.perform(put("/api/v1/rooms/" + roomId + "/settings")
                        .header("Authorization", "Bearer " + hostToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isPublic").value(false))
                .andExpect(jsonPath("$.requireHostApproval").value(true))
                .andExpect(jsonPath("$.enableChat").value(true))
                .andExpect(jsonPath("$.enableAudio").value(true))
                .andExpect(jsonPath("$.enableVideo").value(false))
                .andExpect(jsonPath("$.autoMuteNewJoiners").value(true))
                .andExpect(jsonPath("$.roomVolume").value(70))
                .andExpect(jsonPath("$.enableLiveTranscription").value(true))
                .andExpect(jsonPath("$.transcriptionLanguage").value("SPANISH"));
    }

    @Test
    @DisplayName("GET /api/v1/rooms/{roomId}/settings should return room settings")
    void shouldGetRoomSettings() throws Exception {
        // Given: user creates room
        String hostEmail = "host_user@test.local";
        String hostPassword = "Password1!";
        String hostToken = registerAndLogin(hostEmail, hostPassword, "Host", "User");

        // Create room first
        CreateReadingRoomRequest createRequest = ReadingRoomRequestFixtures.createDefaultCreateReadingRoomRequest();
        MvcResult roomResult = mockMvc.perform(post("/api/v1/rooms")
                        .header("Authorization", "Bearer " + hostToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode roomNode = objectMapper.readTree(roomResult.getResponse().getContentAsString());
        String roomId = roomNode.path("id").asText();

        mockMvc.perform(get("/api/v1/rooms/" + roomId + "/settings")
                        .header("Authorization", "Bearer " + hostToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isPublic").value(true))
                .andExpect(jsonPath("$.requireHostApproval").value(false))
                .andExpect(jsonPath("$.enableChat").value(true))
                .andExpect(jsonPath("$.enableAudio").value(true))
                .andExpect(jsonPath("$.enableVideo").value(true))
                .andExpect(jsonPath("$.autoMuteNewJoiners").value(true))
                .andExpect(jsonPath("$.roomVolume").value(80))
                .andExpect(jsonPath("$.enableLiveTranscription").value(false))
                .andExpect(jsonPath("$.transcriptionLanguage").value("ENGLISH"));
    }

    @Test
    @DisplayName("POST /api/v1/rooms/{roomId}/settings/validate-password should validate room password")
    void shouldValidateRoomPassword() throws Exception {
        // Given: user creates private room with password
        String hostEmail = "host_user1@test.local";
        String hostPassword = "Password1!";
        String hostToken = registerAndLogin(hostEmail, hostPassword, "Host", "User");

        // Create room first
        CreateReadingRoomRequest createRequest = ReadingRoomRequestFixtures.createPrivateCreateReadingRoomRequest();
        MvcResult roomResult = mockMvc.perform(post("/api/v1/rooms")
                        .header("Authorization", "Bearer " + hostToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode roomNode = objectMapper.readTree(roomResult.getResponse().getContentAsString());
        String roomId = roomNode.path("id").asText();

        // Set room password
        UpdateRoomSettingsRequest settingsRequest = ReadingRoomRequestFixtures.createPrivateUpdateRoomSettingsRequest();
        mockMvc.perform(put("/api/v1/rooms/" + roomId + "/settings")
                        .header("Authorization", "Bearer " + hostToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(settingsRequest)))
                .andExpect(status().isOk());

        // When: validate the correct password
        mockMvc.perform(post("/api/v1/rooms/" + roomId + "/settings/validate-password")
                        .header("Authorization", "Bearer " + hostToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"password\":\"newPassword123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        // And: validate incorrect password
        mockMvc.perform(post("/api/v1/rooms/" + roomId + "/settings/validate-password")
                        .header("Authorization", "Bearer " + hostToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"password\":\"wrongPassword\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false));
    }

    @Test
    @DisplayName("PUT /api/v1/rooms/{roomId}/settings should update password and enable private access")
    void shouldUpdatePasswordAndEnablePrivateAccess() throws Exception {
        // Given: user creates a public room
        String hostEmail = "host_user2@test.local";
        String hostPassword = "Password1!";
        String hostToken = registerAndLogin(hostEmail, hostPassword, "Host", "User");

        // Create a public room first
        CreateReadingRoomRequest createRequest = ReadingRoomRequestFixtures.createDefaultCreateReadingRoomRequest();
        MvcResult roomResult = mockMvc.perform(post("/api/v1/rooms")
                        .header("Authorization", "Bearer " + hostToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode roomNode = objectMapper.readTree(roomResult.getResponse().getContentAsString());
        String roomId = roomNode.path("id").asText();

        // When: update settings to private with password
        UpdateRoomSettingsRequest updateRequest = UpdateRoomSettingsRequest.builder()
                .isPublic(false)
                .password("securePassword123")
                .requireHostApproval(true)
                .enableChat(true)
                .enableAudio(true)
                .enableVideo(false)
                .autoMuteNewJoiners(true)
                .roomVolume(75)
                .build();

        mockMvc.perform(put("/api/v1/rooms/" + roomId + "/settings")
                        .header("Authorization", "Bearer " + hostToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isPublic").value(false))
                .andExpect(jsonPath("$.requireHostApproval").value(true));

        // Then: verify password can be validated
        mockMvc.perform(post("/api/v1/rooms/" + roomId + "/settings/validate-password")
                        .header("Authorization", "Bearer " + hostToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"password\":\"securePassword123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true));
    }

    @Test
    @DisplayName("PUT /api/v1/rooms/{roomId}/settings should return 401 when not authenticated")
    void shouldReturn401WhenNotAuthenticated() throws Exception {
        // Given
        String roomId = java.util.UUID.randomUUID().toString();
        UpdateRoomSettingsRequest updateRequest = ReadingRoomRequestFixtures.createDefaultUpdateRoomSettingsRequest();

        // When / Then
        mockMvc.perform(put("/api/v1/rooms/" + roomId + "/settings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isUnauthorized());
    }

    private String registerAndLogin(
            String email,
            String password,
            String firstName,
            String lastName) throws Exception {

        RegisterRequest register = RequestFixtures.createRegisterRequest(
                email,
                password,
                firstName,
                lastName,
                "user"
        );

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        return loginAndGetAccessToken(email, password);
    }

    private String loginAndGetAccessToken(
            String email,
            String password) throws Exception {

        LoginRequest login = RequestFixtures.createLoginRequest(email, password);

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
}