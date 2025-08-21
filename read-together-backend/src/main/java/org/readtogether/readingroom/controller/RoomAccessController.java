package org.readtogether.readingroom.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.readtogether.common.utils.AuthenticationUtils;
import org.readtogether.readingroom.model.request.JoinRoomRequest;
import org.readtogether.readingroom.model.response.InvitationResponse;
import org.readtogether.readingroom.model.response.ReadingRoomResponse;
import org.readtogether.readingroom.service.RoomAccessService;
import org.springframework.http.ResponseEntity;
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
    @Operation(summary = "Get room information from invitation token (public access)")
    public ResponseEntity<InvitationResponse> getRoomFromInvitation(
            @Parameter(description = "Invitation token") @RequestParam String token) {

        InvitationResponse invitation = roomAccessService.getRoomFromInvitation(token);
        return ResponseEntity.ok(invitation);
    }

    @PostMapping("/join")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @Operation(summary = "Join room via invitation token")
    public ResponseEntity<ReadingRoomResponse> joinRoomViaInvitation(
            @RequestParam String token,
            @RequestBody(required = false) JoinRoomRequest request,
            Authentication authentication) {

        UUID userId = AuthenticationUtils.extractUserIdFromAuth(authentication);
        ReadingRoomResponse roomResponse = roomAccessService.joinRoomViaInvitation(token, request, userId);
        return ResponseEntity.ok(roomResponse);
    }

    @PostMapping("/join-by-code")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @Operation(summary = "Join room by room code")
    public ResponseEntity<ReadingRoomResponse> joinRoomByCode(
            @RequestBody JoinRoomRequest request,
            Authentication authentication) {

        UUID userId = AuthenticationUtils.extractUserIdFromAuth(authentication);
        ReadingRoomResponse roomResponse = roomAccessService.joinRoomByCode(request, userId);
        return ResponseEntity.ok(roomResponse);
    }
}
