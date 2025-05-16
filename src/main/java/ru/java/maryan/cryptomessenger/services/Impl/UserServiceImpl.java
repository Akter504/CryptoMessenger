package ru.java.maryan.cryptomessenger.services.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.java.maryan.cryptomessenger.models.User;
import ru.java.maryan.cryptomessenger.repositories.UserRepository;
import ru.java.maryan.cryptomessenger.services.UserService;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean isUserExist(UUID id) {
        return userRepository.existsById(id);
    }

    @Override
    public void createUser(User user) {
        userRepository.save(user);
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    @Override
    public Optional<User> findUserById(UUID userId) {
        return userRepository.findById(userId);
    }

    @Override
    public void deleteUser(UUID userId) {
        User user = findUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Can`t find user: " + userId));
        userRepository.delete(user);
    }

    @Override
    public Optional<User> findUserByLogin(String login) {
        return userRepository.findUserByLogin(login);
    }
}
