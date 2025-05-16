package ru.java.maryan.cryptomessenger.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import ru.java.maryan.cryptomessenger.dto.request.RegisterRequest;
import ru.java.maryan.cryptomessenger.dto.response.TokenResponse;

@Tag(
        name = "User Controller",
        description = "Handles user registration and deletion"
)
public interface UserController {
    @Operation(
            summary = "Register new user",
            description = "Registers a user and returns a JWT token",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User registered successfully"),
                    @ApiResponse(responseCode = "409", description = "User already exists"),
                    @ApiResponse(responseCode = "500", description = "Internal error")
            }
    )
    @PostMapping("/register")
    ResponseEntity<TokenResponse> registerUser(@RequestBody RegisterRequest request);

    @Operation(
            summary = "Delete user",
            description = "Deletes a user account by token",
            responses = {
                    @ApiResponse(responseCode = "204", description = "User deleted"),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "500", description = "Internal error")
            }
    )
    @DeleteMapping("/delete")
    ResponseEntity<Void> deleteUser(@RequestHeader(name = "Authorization") String authHeader);
}
