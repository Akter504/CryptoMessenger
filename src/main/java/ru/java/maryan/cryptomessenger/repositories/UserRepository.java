package ru.java.maryan.cryptomessenger.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.java.maryan.cryptomessenger.models.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findUserByEmail(String email);

    Optional<User> findUserByLogin(String login);
}
