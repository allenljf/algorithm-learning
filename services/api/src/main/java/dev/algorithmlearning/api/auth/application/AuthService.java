package dev.algorithmlearning.api.auth.application;

import dev.algorithmlearning.api.auth.domain.Argon2PasswordHasher;
import dev.algorithmlearning.api.auth.domain.JwtTokenService;
import dev.algorithmlearning.api.auth.domain.PasswordPolicy;
import java.time.Clock;
import java.util.Locale;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

public class AuthService {
    private final AuthUserRepository users;
    private final AuthSessionService sessions;
    private final Argon2PasswordHasher passwords;
    private final JwtTokenService jwt;
    private final Clock clock;

    public AuthService(AuthUserRepository users, AuthSessionService sessions, Argon2PasswordHasher passwords, JwtTokenService jwt, Clock clock) {
        this.users = users; this.sessions = sessions; this.passwords = passwords; this.jwt = jwt; this.clock = clock;
    }

    @Transactional
    public AuthenticatedSession register(String email, String password) {
        var normalized = normalizeEmail(email);
        PasswordPolicy.requireValid(password);
        if (users.findByNormalizedEmail(normalized).isPresent()) throw new AuthException("Account already exists");
        var user = users.save(new AuthUser(UUID.randomUUID(), normalized, passwords.hash(password), clock.instant()));
        return authenticate(user);
    }

    @Transactional
    public AuthenticatedSession login(String email, String password) {
        var user = users.findByNormalizedEmail(normalizeEmail(email)).filter(candidate -> passwords.matches(password, candidate.passwordHash()))
                .orElseThrow(() -> new AuthException("Invalid credentials"));
        return authenticate(user);
    }

    @Transactional
    public RefreshedAccessToken refresh(String refreshToken) {
        var session = sessions.rotate(refreshToken).orElseThrow(() -> new AuthException("Invalid refresh session"));
        var user = users.findById(session.userId()).orElseThrow(() -> new AuthException("Invalid refresh session"));
        var access = jwt.issue(user.id());
        return new RefreshedAccessToken(access, jwt.expiresAt(access), session);
    }

    @Transactional
    public void logout(String refreshToken) { sessions.revoke(refreshToken); }

    public AuthUser me(UUID userId) { return users.findById(userId).orElseThrow(() -> new AuthException("Unknown user")); }

    private AuthenticatedSession authenticate(AuthUser user) {
        var access = jwt.issue(user.id());
        return new AuthenticatedSession(user, access, jwt.expiresAt(access), sessions.create(user.id()));
    }

    private static String normalizeEmail(String email) {
        if (email == null) throw new AuthException("Invalid credentials");
        var normalized = email.trim().toLowerCase(Locale.ROOT);
        if (normalized.isEmpty() || normalized.codePointCount(0, normalized.length()) > 254 || !normalized.contains("@")) {
            throw new AuthException("Invalid credentials");
        }
        return normalized;
    }

    public record AuthenticatedSession(AuthUser user, String accessToken, java.time.Instant expiresAt, AuthSessionService.SessionToken refreshSession) { }
    public record RefreshedAccessToken(String accessToken, java.time.Instant expiresAt, AuthSessionService.SessionToken refreshSession) { }
}
