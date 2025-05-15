package ru.java.maryan.cryptomessenger.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class RegisterRequest {
    @NotBlank(message = "The email address cannot be empty.")
    @Email(message = "Uncorrected email.")
    @Size(max = 40)
    private String email;

    @NotBlank(message = "The login cannot be empty.")
    @Size(max = 20)
    private String login;

    @NotBlank(message = "The password cannot be empty.")
    @Size(max=200, message = "The password can be max 200 symbols.")
    private String password;
}
