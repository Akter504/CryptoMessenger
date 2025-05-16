package ru.java.maryan.cryptomessenger.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import ru.java.maryan.cryptomessenger.dto.request.LoginRequest;
import ru.java.maryan.cryptomessenger.dto.response.LoginResponse;

@Tag(
        name = "Authentication Controller",
        description = "Endpoints for user login by email or phone"
)
public interface AuthController {
    @Operation(
            summary = "Login by email",
            description = "Authenticates a user using their email and password",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Login successful",
                            content = @Content(schema = @Schema(implementation = LoginResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content
                    )
            }
    )
    @PostMapping("/login-by-email")
    ResponseEntity<LoginResponse> loginByEmail(LoginRequest request);
}
