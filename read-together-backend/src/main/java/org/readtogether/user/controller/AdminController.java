package org.readtogether.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.readtogether.common.model.response.CustomResponse;
import org.readtogether.user.model.User;
import org.readtogether.user.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final UserService userService;

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('ADMIN')")
    public CustomResponse<List<User>> getAllUsers() {
        log.info("Received a request to get all users (admin only)");
        // For now, return empty list since this is just for testing authorization
        return CustomResponse.successOf(Collections.emptyList());
    }

    @DeleteMapping("/users/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public CustomResponse<Void> deleteUser(@PathVariable String userId) {
        log.info("Received a request to delete user: {} (admin only)", userId);
        // For now, just return success since this is for testing authorization
        return CustomResponse.SUCCESS;
    }

}