package ru.java.maryan.cryptomessenger.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    @NotBlank(message = "The email address cannot be empty.")
    @Email(message = "Uncorrected email.")
    @Size(max = 40)
    private String email;

    @NotBlank(message = "The password cannot be empty.")
    private String password;
}
