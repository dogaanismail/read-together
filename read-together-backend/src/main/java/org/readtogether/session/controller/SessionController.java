package org.readtogether.session.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.readtogether.common.exception.RecordNotFoundException;
import org.readtogether.common.model.response.CustomResponse;
import org.readtogether.session.model.request.SessionCreateRequest;
import org.readtogether.session.model.response.SessionResponse;
import org.readtogether.session.model.request.SessionUpdateRequest;
import org.readtogether.session.service.SessionService;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import jakarta.annotation.security.PermitAll;

@Slf4j
@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    @PostMapping
    @PreAuthorize("hasAuthority('USER')")
    public CompletableFuture<CustomResponse<SessionResponse>> createSessionAsync(
            @Valid @RequestPart("session") SessionCreateRequest request,
            @RequestPart("file") MultipartFile file,
            Authentication authentication) {

        return sessionService.createSessionAsync(request, file, authentication)
                .thenApply(CustomResponse::successOf);
    }

    @PostMapping("/sync")
    @PreAuthorize("hasAuthority('USER')")
    public CustomResponse<SessionResponse> createSessionSync(
            @Valid @RequestPart("session") SessionCreateRequest request,
            @RequestPart("file") MultipartFile file,
            Authentication authentication) {

        SessionResponse session = sessionService.createSession(request, file, authentication);

        return CustomResponse.successOf(session);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('USER')")
    public CustomResponse<Page<SessionResponse>> getUserSessions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {

        Page<SessionResponse> sessions = sessionService.getUserSessions(page, size, authentication);

        return CustomResponse.successOf(sessions);
    }

    @GetMapping("/public")
    @PermitAll
    public CustomResponse<Page<SessionResponse>> getPublicSessions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<SessionResponse> sessions = sessionService.getPublicSessions(page, size);

        return CustomResponse.successOf(sessions);
    }

    @GetMapping("/feed")
    @PreAuthorize("hasAuthority('USER')")
    public CustomResponse<Page<SessionResponse>> getFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String mediaType,
            @RequestParam(required = false) String search) {

        Page<SessionResponse> sessions = sessionService.getFeed(page, size, mediaType, search);

        return CustomResponse.successOf(sessions);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER')")
    public CustomResponse<SessionResponse> getSession(@PathVariable UUID id) {

        return sessionService.getSessionById(id)
                .map(CustomResponse::successOf)
                .orElseThrow(() -> new RecordNotFoundException("Session not found: " + id));
    }

    @GetMapping("/my/{id}")
    @PreAuthorize("hasAuthority('USER')")
    public CustomResponse<SessionResponse> getUserSession(
            @PathVariable UUID id,
            Authentication authentication) {

        return sessionService.getUserSession(id, authentication)
                .map(CustomResponse::successOf)
                .orElseThrow(() -> new RecordNotFoundException("Session not found for current user: " + id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USER')")
    public CustomResponse<SessionResponse> updateSession(
            @PathVariable UUID id,
            @Valid @RequestBody SessionUpdateRequest request,
            Authentication authentication) {

        SessionResponse session = sessionService.updateSession(id, request, authentication);

        return CustomResponse.successOf(session);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USER')")
    public CustomResponse<Void> deleteSession(
            @PathVariable UUID id,
            Authentication authentication) {

        sessionService.deleteSession(id, authentication);

        return CustomResponse.SUCCESS;
    }

}
