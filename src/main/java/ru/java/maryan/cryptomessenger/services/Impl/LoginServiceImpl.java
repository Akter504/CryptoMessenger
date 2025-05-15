package ru.java.maryan.cryptomessenger.services.Impl;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.java.maryan.cryptomessenger.dto.response.TokenResponse;
import ru.java.maryan.cryptomessenger.exceptions.AuthException;
import ru.java.maryan.cryptomessenger.models.User;
import ru.java.maryan.cryptomessenger.services.LoginService;
import ru.java.maryan.cryptomessenger.utils.TokenUtils;

@Service
public class LoginServiceImpl implements LoginService {
    private final BCryptPasswordEncoder passwordEncoder;
    private final TokenUtils tokenUtils;

    public LoginServiceImpl(BCryptPasswordEncoder passwordEncoder, TokenUtils tokenUtils) {
        this.passwordEncoder = passwordEncoder;
        this.tokenUtils = tokenUtils;
    }

    @Override
    public TokenResponse login(User user, String password) {
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new AuthException("Invalid credentials");
        }
        String stringId = user.getId().toString();
        return new TokenResponse(tokenUtils.generateJwtToken(stringId));
    }
}
