package org.readtogether.readingroom.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.readtogether.common.BaseIntegrationTest;
import org.readtogether.readingroom.common.enums.InvitationType;
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

import java.util.Arrays;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag("integration")
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
        String hostEmail = "host_email_" + System.currentTimeMillis() + "@test.local";
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
                .andExpect(jsonPath("$.response").isArray())
                .andExpect(jsonPath("$.response.length()").value(2))
                .andExpect(jsonPath("$.response[0].invitationType").value("EMAIL"))
                .andExpect(jsonPath("$.response[0].readingRoomId").value(roomId))
                .andExpect(jsonPath("$.response[0].invitationToken").exists())
                .andExpect(jsonPath("$.response[1].invitationType").value("EMAIL"))
                .andExpect(jsonPath("$.response[1].readingRoomId").value(roomId))
                .andExpect(jsonPath("$.response[1].invitationToken").exists());
    }

    @Test
    @DisplayName("POST /api/v1/rooms/{roomId}/invitations should create direct invitations")
    void shouldCreateDirectInvitations() throws Exception {
        // Given: host creates room, and we have other users to invite
        String hostEmail = "host_direct_" + System.currentTimeMillis() + "@test.local";
        String hostPassword = "Password1!";
        String hostToken = registerAndLogin(hostEmail, hostPassword, "Host", "User");

        String user1Email = "user1_" + System.currentTimeMillis() + "@test.local";
        String user1Password = "Password1!";
        registerAndLogin(user1Email, user1Password, "User1", "Test");
        UUID user1Id = getUserIdByEmail(user1Email);

        String user2Email = "user2_" + System.currentTimeMillis() + "@test.local";
        String user2Password = "Password1!";
        registerAndLogin(user2Email, user2Password, "User2", "Test");
        UUID user2Id = getUserIdByEmail(user2Email);

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

        // When: create direct invitations with actual user IDs
        InviteToRoomRequest inviteRequest = InviteToRoomRequest.builder()
                .invitationType(InvitationType.DIRECT_INVITE)
                .invitedUserIds(Arrays.asList(user1Id, user2Id))
                .message("Direct invitation to reading room")
                .expirationHours(48)
                .build();

        mockMvc.perform(post("/api/v1/rooms/" + roomId + "/invitations")
                        .header("Authorization", "Bearer " + hostToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inviteRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.response").isArray())
                .andExpect(jsonPath("$.response.length()").value(2))
                .andExpect(jsonPath("$.response[0].invitationType").value("DIRECT_INVITE"))
                .andExpect(jsonPath("$.response[0].readingRoomId").value(roomId))
                .andExpect(jsonPath("$.response[0].invitationToken").exists())
                .andExpect(jsonPath("$.response[1].invitationType").value("DIRECT_INVITE"))
                .andExpect(jsonPath("$.response[1].readingRoomId").value(roomId))
                .andExpect(jsonPath("$.response[1].invitationToken").exists());
    }

    @Test
    @DisplayName("POST /api/v1/rooms/{roomId}/invitations should create share link invitation")
    void shouldCreateShareLinkInvitation() throws Exception {
        // Given: user creates room
        String hostEmail = "host_share_" + System.currentTimeMillis() + "@test.local";
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

        // When: create a share link invitation
        InviteToRoomRequest inviteRequest = ReadingRoomRequestFixtures.createShareLinkInviteRequest();

        mockMvc.perform(post("/api/v1/rooms/" + roomId + "/invitations")
                        .header("Authorization", "Bearer " + hostToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inviteRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.response").isArray())
                .andExpect(jsonPath("$.response.length()").value(1))
                .andExpect(jsonPath("$.response[0].invitationType").value("LINK_SHARE"))
                .andExpect(jsonPath("$.response[0].readingRoomId").value(roomId))
                .andExpect(jsonPath("$.response[0].invitationToken").exists())
                .andExpect(jsonPath("$.response[0].shareLink").exists());
    }

    @Test
    @DisplayName("GET /api/v1/rooms/{roomId}/invitations should return room invitations for host")
    void shouldGetRoomInvitations() throws Exception {
        // Given: host creates room and sends invitations
        String hostEmail = "host_invite_" + System.currentTimeMillis() + "@test.local";
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
                .andExpect(jsonPath("$.response").isArray())
                .andExpect(jsonPath("$.response.length()").value(2));
    }

    @Test
    @DisplayName("POST /api/v1/rooms/{roomId}/invitations/share-link should generate share link")
    void shouldGenerateShareLink() throws Exception {
        // Given: user creates room
        String hostEmail = "host_generate_" + System.currentTimeMillis() + "@test.local";
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

        // When: generate a share link
        mockMvc.perform(post("/api/v1/rooms/" + roomId + "/invitations/share-link")
                        .header("Authorization", "Bearer " + hostToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.shareLink").exists())
                .andExpect(jsonPath("$.response.shareLink").value(containsString("/room/join")));
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

    private String registerAndLogin(
            String email,
            String password,
            String firstName,
            String lastName) throws Exception {

        RegisterRequest register = RequestFixtures.createRegisterRequest(
                email,
                password,
                firstName,
                lastName
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

    private UUID getUserIdByEmail(String email) throws Exception {
        // Get a user profile to extract user ID
        String token = loginAndGetAccessToken(email, "Password1!");

        MvcResult profileResult = mockMvc.perform(get("/api/v1/users/current-user")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode profileNode = objectMapper.readTree(profileResult.getResponse().getContentAsString());

        return UUID.fromString(profileNode.path("response").path("id").asText());
    }
}