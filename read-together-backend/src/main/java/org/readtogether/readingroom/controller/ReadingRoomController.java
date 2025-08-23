package org.readtogether.readingroom.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.readtogether.common.utils.AuthenticationUtils;
import org.readtogether.readingroom.model.request.CreateReadingRoomRequest;
import org.readtogether.readingroom.model.response.ReadingRoomResponse;
import org.readtogether.readingroom.service.ReadingRoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
@Tag(name = "Reading Rooms", description = "Reading room management endpoints")
public class ReadingRoomController {

    private final ReadingRoomService readingRoomService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @Operation(summary = "Create a new reading room")
    public ResponseEntity<ReadingRoomResponse> createRoom(
            @Valid @RequestBody CreateReadingRoomRequest request,
            Authentication authentication) {

        UUID userId = AuthenticationUtils.extractUserIdFromAuth(authentication);
        ReadingRoomResponse response = readingRoomService.createRoom(request, userId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/{roomId}/join")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @Operation(summary = "Join a reading room")
    public ResponseEntity<ReadingRoomResponse> joinRoom(
            @Parameter(description = "Room ID") @PathVariable UUID roomId,
            Authentication authentication) {

        UUID userId = AuthenticationUtils.extractUserIdFromAuth(authentication);
        ReadingRoomResponse response = readingRoomService.joinRoom(roomId, userId);

        return ResponseEntity
                .ok(response);
    }

    @PostMapping("/{roomId}/leave")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @Operation(summary = "Leave a reading room")
    public ResponseEntity<Void> leaveRoom(
            @Parameter(description = "Room ID") @PathVariable UUID roomId,
            Authentication authentication) {

        UUID userId = AuthenticationUtils.extractUserIdFromAuth(authentication);
        readingRoomService.leaveRoom(roomId, userId);

        return ResponseEntity
                .ok()
                .build();
    }

    @PostMapping("/{roomId}/start")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @Operation(summary = "Start a reading room (host only)")
    public ResponseEntity<ReadingRoomResponse> startRoom(
            @Parameter(description = "Room ID") @PathVariable UUID roomId,
            Authentication authentication) {

        UUID userId = AuthenticationUtils.extractUserIdFromAuth(authentication);
        ReadingRoomResponse response = readingRoomService.startRoom(roomId, userId);

        return ResponseEntity
                .ok(response);
    }

    @GetMapping("/public")
    @Operation(summary = "Get all public waiting rooms")
    public ResponseEntity<List<ReadingRoomResponse>> getPublicWaitingRooms() {

        List<ReadingRoomResponse> rooms = readingRoomService.getPublicWaitingRooms();
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/my-rooms")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @Operation(summary = "Get rooms hosted by current user")
    public ResponseEntity<List<ReadingRoomResponse>> getMyHostedRooms(Authentication authentication) {

        UUID userId = AuthenticationUtils.extractUserIdFromAuth(authentication);
        List<ReadingRoomResponse> rooms = readingRoomService.getUserHostedRooms(userId);
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/code/{roomCode}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @Operation(summary = "Get room by room code")
    public ResponseEntity<ReadingRoomResponse> getRoomByCode(
            @Parameter(description = "Room code") @PathVariable String roomCode) {

        ReadingRoomResponse room = readingRoomService.getRoomByCode(roomCode);
        return ResponseEntity.ok(room);
    }
}
