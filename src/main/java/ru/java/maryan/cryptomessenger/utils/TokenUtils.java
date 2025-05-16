package ru.java.maryan.cryptomessenger.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.java.maryan.cryptomessenger.exceptions.ParseTokenException;

import java.util.Date;

@Component
public class TokenUtils {
    private static final String TOKEN_PREFIX = "Bearer ";
    private final String secret;
    private final String issuer;

    public TokenUtils(
            @Value("${token.signing.secret}") String secret,
            @Value("${token.issuer}") String issuer
    ) {
        this.secret = secret;
        this.issuer = issuer;
    }

    public String getSubject(String token) {
        return JWT.decode(token).getSubject();
    }

    public String extractToken(String header) {
        if (StringUtils.isBlank(header) || !header.startsWith(TOKEN_PREFIX)) {
            return null;
        }

        try {
            return header.substring(TOKEN_PREFIX.length());
        } catch (Exception ex) {
            throw new ParseTokenException("Invalid token");
        }
    }

    public String generateJwtToken(String id) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withIssuer(issuer)
                .withSubject(id)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .sign(algorithm);
    }
}
