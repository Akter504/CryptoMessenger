package ru.java.maryan.cryptomessenger.services.Impl;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.java.maryan.cryptomessenger.dto.request.RegisterRequest;
import ru.java.maryan.cryptomessenger.dto.response.TokenResponse;
import ru.java.maryan.cryptomessenger.models.User;
import ru.java.maryan.cryptomessenger.services.RegisterService;
import ru.java.maryan.cryptomessenger.utils.TokenUtils;

@Service
public class RegisterServiceImpl implements RegisterService {
    private final UserServiceImpl userService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final TokenUtils tokenUtils;

    public RegisterServiceImpl(UserServiceImpl userService, BCryptPasswordEncoder passwordEncoder, TokenUtils tokenUtils) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.tokenUtils = tokenUtils;
    }

    @Override
    public TokenResponse register(RegisterRequest request) {
        User user = new User();
        user.setLogin(request.getLogin());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        userService.createUser(user);

        String stringId = user.getId().toString();
        return new TokenResponse(tokenUtils.generateJwtToken(stringId));
    }
}
