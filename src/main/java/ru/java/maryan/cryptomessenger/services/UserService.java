package ru.java.maryan.cryptomessenger.services;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import ru.java.maryan.cryptomessenger.dto.request.LoginRequest;
import ru.java.maryan.cryptomessenger.models.User;

import java.util.Optional;
import java.util.UUID;

public interface UserService {
    boolean isUserExist(UUID id);

    void createUser(User user);

    Optional<User> findUserByEmail(@NotBlank(message = "The email address cannot be empty.") @Email(message = "Uncorrected email.") @Size(max = 40) String email);

    Optional<User> findUserById(UUID userId);

    void deleteUser(UUID userId);

    Optional<User> findUserByLogin(@NotBlank(message = "The login cannot be empty.") @Size(max = 20) String login);
}
