package org.readtogether.readingroom.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.readtogether.common.model.response.CustomResponse;
import org.readtogether.common.utils.AuthenticationUtils;
import org.readtogether.readingroom.model.request.JoinRoomRequest;
import org.readtogether.readingroom.model.response.InvitationResponse;
import org.readtogether.readingroom.model.response.ReadingRoomResponse;
import org.readtogether.readingroom.service.RoomAccessService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/room")
@RequiredArgsConstructor
@Tag(name = "Room Access", description = "Room joining and access endpoints")
public class RoomAccessController {

    private final RoomAccessService roomAccessService;

    @GetMapping("/join")
    @PermitAll
    @Operation(summary = "Get room information from invitation token (public access)")
    public CustomResponse<InvitationResponse> getRoomFromInvitation(
            @Parameter(description = "Invitation token") @RequestParam String token) {

        InvitationResponse invitation = roomAccessService.getRoomFromInvitation(token);

        return CustomResponse.successOf(invitation);
    }

    @PostMapping("/join")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @Operation(summary = "Join room via invitation token")
    public CustomResponse<ReadingRoomResponse> joinRoomViaInvitation(
            @RequestParam String token,
            @RequestBody(required = false) JoinRoomRequest request,
            Authentication authentication) {

        UUID userId = AuthenticationUtils.extractUserIdFromAuth(authentication);
        ReadingRoomResponse roomResponse = roomAccessService.joinRoomViaInvitation(token, request, userId);

        return CustomResponse.successOf(roomResponse);
    }

    @PostMapping("/join-by-code")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @Operation(summary = "Join room by room code")
    public CustomResponse<ReadingRoomResponse> joinRoomByCode(
            @RequestBody JoinRoomRequest request,
            Authentication authentication) {

        UUID userId = AuthenticationUtils.extractUserIdFromAuth(authentication);
        ReadingRoomResponse roomResponse = roomAccessService.joinRoomByCode(request, userId);

        return CustomResponse.successOf(roomResponse);
    }
}
