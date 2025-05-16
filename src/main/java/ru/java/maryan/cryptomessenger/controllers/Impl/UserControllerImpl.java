package ru.java.maryan.cryptomessenger.controllers.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.java.maryan.cryptomessenger.controllers.UserController;
import ru.java.maryan.cryptomessenger.dto.request.RegisterRequest;
import ru.java.maryan.cryptomessenger.dto.response.TokenResponse;
import ru.java.maryan.cryptomessenger.models.User;
import ru.java.maryan.cryptomessenger.services.RegisterService;
import ru.java.maryan.cryptomessenger.services.UserService;
import ru.java.maryan.cryptomessenger.utils.TokenUtils;

import java.util.Optional;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api/users")
public class UserControllerImpl implements UserController {
    private final UserService userService;
    private final RegisterService registerService;
    private final TokenUtils tokenUtils;

    @Autowired
    public UserControllerImpl(UserService userService, RegisterService registerService, TokenUtils tokenUtils) {
        this.userService = userService;
        this.registerService = registerService;
        this.tokenUtils = tokenUtils;
    }

    @PostMapping("/register")
    public ResponseEntity<TokenResponse> registerUser(@Validated @RequestBody RegisterRequest request) {
        Optional<User> existingUser = userService.findUserByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        TokenResponse token = registerService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(token);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        String stringId = tokenUtils.getSubject(authHeader.substring(7));
        UUID userId = UUID.fromString(stringId);
        Optional<User> user = userService.findUserById(userId);
        if (user.isPresent()) {
            userService.deleteUser(userId);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
