package ru.java.maryan.cryptomessenger.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.java.maryan.cryptomessenger.utils.TokenUtils;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenFilterImpl extends OncePerRequestFilter {
    private static final String TOKEN_HEADER = "Authorization";
    private final JwtVerifierService jwtVerifierService;
    private final TokenUtils tokenUtils;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        log.info("URI: " + request.getRequestURI());
        if (request.getRequestURI().startsWith("/api/auth")
                || request.getRequestURI().startsWith("/api/users/register")
                || request.getRequestURI().startsWith("/swagger-ui")
                || request.getRequestURI().startsWith("/v3")
                || request.getRequestURI().startsWith("/actuator")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = tokenUtils.extractToken(request.getHeader(TOKEN_HEADER));
        if (jwtVerifierService.verify(token)) {
            String id = tokenUtils.getSubject(token);
            Authentication auth = new TokenAuthentication(id);
            SecurityContextHolder.getContext().setAuthentication(auth);
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token verification failed");
        }
    }
}
