package org.readtogether.readingroom.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.readtogether.common.BaseIntegrationTest;
import org.readtogether.readingroom.fixtures.ReadingRoomRequestFixtures;
import org.readtogether.readingroom.model.request.CreateReadingRoomRequest;
import org.readtogether.readingroom.model.request.InviteToRoomRequest;
import org.readtogether.user.fixtures.RequestFixtures;
import org.readtogether.user.model.request.LoginRequest;
import org.readtogether.user.model.request.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("RoomInvitationController Integration Tests")
class RoomInvitationControllerIntegrationTests extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/v1/rooms/{roomId}/invitations should create email invitations")
    void shouldCreateEmailInvitations() throws Exception {
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

        // When: create email invitations
        InviteToRoomRequest inviteRequest = ReadingRoomRequestFixtures.createDefaultEmailInviteRequest();
        
        mockMvc.perform(post("/api/v1/rooms/" + roomId + "/invitations")
                        .header("Authorization", "Bearer " + hostToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inviteRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2)) // 2 email invitations
                .andExpect(jsonPath("$[0].invitationType").value("EMAIL"))
                .andExpect(jsonPath("$[0].readingRoomId").value(roomId))
                .andExpect(jsonPath("$[0].invitationToken").exists())
                .andExpect(jsonPath("$[1].invitationType").value("EMAIL"))
                .andExpect(jsonPath("$[1].readingRoomId").value(roomId))
                .andExpect(jsonPath("$[1].invitationToken").exists());
    }

    @Test
    @DisplayName("POST /api/v1/rooms/{roomId}/invitations should create direct invitations")
    void shouldCreateDirectInvitations() throws Exception {
        // Given: host creates room, and we have other users to invite
        String hostEmail = "host@test.local";
        String hostPassword = "Password1!";
        String hostToken = registerAndLogin(hostEmail, hostPassword, "Host", "User");

        String user1Email = "user1@test.local";
        String user1Password = "Password1!";
        String user1Token = registerAndLogin(user1Email, user1Password, "User1", "Test");

        String user2Email = "user2@test.local";
        String user2Password = "Password1!";
        String user2Token = registerAndLogin(user2Email, user2Password, "User2", "Test");

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

        // When: create direct invitations
        InviteToRoomRequest inviteRequest = ReadingRoomRequestFixtures.createDirectInviteRequest();
        
        mockMvc.perform(post("/api/v1/rooms/" + roomId + "/invitations")
                        .header("Authorization", "Bearer " + hostToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inviteRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2)) // 2 direct invitations
                .andExpect(jsonPath("$[0].invitationType").value("DIRECT_INVITE"))
                .andExpect(jsonPath("$[0].readingRoomId").value(roomId))
                .andExpect(jsonPath("$[0].invitationToken").exists())
                .andExpect(jsonPath("$[1].invitationType").value("DIRECT_INVITE"))
                .andExpect(jsonPath("$[1].readingRoomId").value(roomId))
                .andExpect(jsonPath("$[1].invitationToken").exists());
    }

    @Test
    @DisplayName("POST /api/v1/rooms/{roomId}/invitations should create share link invitation")
    void shouldCreateShareLinkInvitation() throws Exception {
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

        // When: create share link invitation
        InviteToRoomRequest inviteRequest = ReadingRoomRequestFixtures.createShareLinkInviteRequest();
        
        mockMvc.perform(post("/api/v1/rooms/" + roomId + "/invitations")
                        .header("Authorization", "Bearer " + hostToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inviteRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1)) // 1 share link invitation
                .andExpect(jsonPath("$[0].invitationType").value("LINK_SHARE"))
                .andExpect(jsonPath("$[0].readingRoomId").value(roomId))
                .andExpect(jsonPath("$[0].invitationToken").exists())
                .andExpect(jsonPath("$[0].shareLink").exists());
    }

    @Test
    @DisplayName("GET /api/v1/rooms/{roomId}/invitations should return room invitations for host")
    void shouldGetRoomInvitations() throws Exception {
        // Given: host creates room and sends invitations
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

        // Create some invitations
        InviteToRoomRequest inviteRequest = ReadingRoomRequestFixtures.createDefaultEmailInviteRequest();
        mockMvc.perform(post("/api/v1/rooms/" + roomId + "/invitations")
                        .header("Authorization", "Bearer " + hostToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inviteRequest)))
                .andExpect(status().isCreated());

        // When: get room invitations
        mockMvc.perform(get("/api/v1/rooms/" + roomId + "/invitations")
                        .header("Authorization", "Bearer " + hostToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2)); // 2 email invitations
    }

    @Test
    @DisplayName("POST /api/v1/rooms/{roomId}/invitations/share-link should generate share link")
    void shouldGenerateShareLink() throws Exception {
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

        // When: generate share link
        mockMvc.perform(post("/api/v1/rooms/" + roomId + "/invitations/share-link")
                        .header("Authorization", "Bearer " + hostToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.invitationType").value("LINK_SHARE"))
                .andExpect(jsonPath("$.readingRoomId").value(roomId))
                .andExpect(jsonPath("$.shareLink").exists())
                .andExpect(jsonPath("$.shareLink").value(org.hamcrest.Matchers.containsString("/room/join")))
                .andExpect(jsonPath("$.invitationToken").exists());
    }

    @Test
    @DisplayName("POST /api/v1/rooms/{roomId}/invitations should return 401 when not authenticated")
    void shouldReturn401WhenNotAuthenticated() throws Exception {
        // Given
        String roomId = java.util.UUID.randomUUID().toString();
        InviteToRoomRequest inviteRequest = ReadingRoomRequestFixtures.createDefaultEmailInviteRequest();

        // When / Then
        mockMvc.perform(post("/api/v1/rooms/" + roomId + "/invitations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inviteRequest)))
                .andExpect(status().isUnauthorized());
    }

    private String registerAndLogin(String email, String password, String firstName, String lastName) throws Exception {
        RegisterRequest register = RequestFixtures.createRegisterRequest(email, password, firstName, lastName, "user");

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        return loginAndGetAccessToken(email, password);
    }

    private String loginAndGetAccessToken(String email, String password) throws Exception {
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