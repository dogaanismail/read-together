package org.readtogether.session.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.readtogether.session.model.request.SessionCreateRequest;
import org.readtogether.session.model.response.SessionResponse;
import org.readtogether.session.model.request.SessionUpdateRequest;
import org.readtogether.session.service.SessionService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
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
    public CompletableFuture<ResponseEntity<SessionResponse>> createSessionAsync(
            @Valid @RequestPart("session") SessionCreateRequest request,
            @RequestPart("file") MultipartFile file,
            Authentication authentication) {

        return sessionService.createSessionAsync(request, file, authentication)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> {
                    log.error("Failed to create session", ex);
                    return ResponseEntity.badRequest().build();
                });
    }

    @PostMapping("/sync")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<SessionResponse> createSessionSync(
            @Valid @RequestPart("session") SessionCreateRequest request,
            @RequestPart("file") MultipartFile file,
            Authentication authentication) {

        try {
            SessionResponse session = sessionService.createSession(request, file, authentication);
            return ResponseEntity.ok(session);
        } catch (Exception e) {
            log.error("Failed to create session synchronously", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Page<SessionResponse>> getUserSessions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {

        Page<SessionResponse> sessions = sessionService.getUserSessions(page, size, authentication);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/public")
    @PermitAll
    public ResponseEntity<Page<SessionResponse>> getPublicSessions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<SessionResponse> sessions = sessionService.getPublicSessions(page, size);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/feed")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Page<SessionResponse>> getFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String mediaType,
            @RequestParam(required = false) String search) {

        Page<SessionResponse> sessions = sessionService.getFeed(page, size, mediaType, search);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<SessionResponse> getSession(@PathVariable UUID id) {

        return sessionService.getSessionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/my/{id}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<SessionResponse> getUserSession(
            @PathVariable UUID id,
            Authentication authentication) {

        return sessionService.getUserSession(id, authentication)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<SessionResponse> updateSession(
            @PathVariable UUID id,
            @Valid @RequestBody SessionUpdateRequest request,
            Authentication authentication) {

        try {
            SessionResponse session = sessionService.updateSession(id, request, authentication);
            return ResponseEntity.ok(session);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Void> deleteSession(
            @PathVariable UUID id,
            Authentication authentication) {

        try {
            sessionService.deleteSession(id, authentication);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
