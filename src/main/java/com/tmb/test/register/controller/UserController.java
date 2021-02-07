package com.tmb.test.register.controller;

import com.tmb.test.register.service.AuthenticationService;
import com.tmb.test.register.service.UserService;
import com.tmb.test.register.service.model.User;
import com.tmb.test.register.service.model.UserLogin;
import com.tmb.test.register.service.model.UserRegistration;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@AllArgsConstructor
public class UserController {
    private static final String RESOURCE_URI = "/v1/users";

    private final AuthenticationService authService;
    private final UserService userService;

    @PostMapping(RESOURCE_URI + "/register")
    public ResponseEntity<UserRegistration.Response> createUserAccount(@RequestBody UserRegistration.Request request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    @PostMapping(RESOURCE_URI + "/login")
    public ResponseEntity<UserLogin.Response> doLoginUser(@RequestBody UserLogin.Request request) throws Exception {
        return ResponseEntity.ok(UserLogin.Response.builder()
                .accessToken(authService.signToken(request.getUserName(), request.getPassword()))
                .build());
    }

    @GetMapping(RESOURCE_URI + "/me/{refCode}")
    @Transactional(readOnly = true)
    public ResponseEntity<User> getCurrentUserByRefCode(@PathVariable(name = "refCode") String refCode, Principal principal) {
        return ResponseEntity.ok(userService.getUserByIdAndRefCode(getCurrentUserId(principal), refCode));
    }

    @GetMapping(RESOURCE_URI + "/me")
    @Transactional(readOnly = true)
    public ResponseEntity<User> getCurrentUserProfile(Principal principal) {
        return ResponseEntity.ok(userService.getUserById(getCurrentUserId(principal)));
    }

    private Integer getCurrentUserId(Principal principal) {
        return Integer.parseInt(principal.getName());
    }
}
