package org.readtogether.readingroom.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.readtogether.common.BaseIntegrationTest;
import org.readtogether.readingroom.fixtures.ReadingRoomRequestFixtures;
import org.readtogether.readingroom.model.request.CreateReadingRoomRequest;
import org.readtogether.user.fixtures.RequestFixtures;
import org.readtogether.user.model.request.LoginRequest;
import org.readtogether.user.model.request.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("ReadingRoomController Integration Tests")
class ReadingRoomControllerIntegrationTests extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/v1/rooms should create a reading room")
    void shouldCreateRoom() throws Exception {
        // Given: register and login
        String email = "room.creator@test.local";
        String password = "Password1!";
        RegisterRequest register = RequestFixtures.createRegisterRequest(email,
                password,
                "Room",
                "Creator",
                "user"
        );

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        String token = loginAndGetAccessToken(email, password);

        CreateReadingRoomRequest createRequest = ReadingRoomRequestFixtures.createDefaultCreateReadingRoomRequest();

        // When: create room
        mockMvc.perform(post("/api/v1/rooms")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(createRequest.getTitle()))
                .andExpect(jsonPath("$.description").value(createRequest.getDescription()))
                .andExpect(jsonPath("$.maxParticipants").value(createRequest.getMaxParticipants()))
                .andExpect(jsonPath("$.isPublic").value(createRequest.isPublic()))
                .andExpect(jsonPath("$.roomCode").exists())
                .andExpect(jsonPath("$.currentParticipants").value(1)) // host joins automatically
                .andExpect(jsonPath("$.host.email").value(email));
    }

    @Test
    @DisplayName("GET /api/v1/rooms/public should return public waiting rooms")
    void shouldGetPublicWaitingRooms() throws Exception {
        // When: get public rooms
        mockMvc.perform(get("/api/v1/rooms/public"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("POST /api/v1/rooms should return 401 when not authenticated")
    void shouldReturn401WhenNotAuthenticated() throws Exception {
        // Given
        CreateReadingRoomRequest createRequest = ReadingRoomRequestFixtures.createDefaultCreateReadingRoomRequest();

        // When / Then
        mockMvc.perform(post("/api/v1/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isUnauthorized());
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