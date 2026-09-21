package dev.algorithmlearning.api.auth.api;

import dev.algorithmlearning.api.app.security.CurrentUser;
import dev.algorithmlearning.api.auth.application.AllowedOriginValidator;
import dev.algorithmlearning.api.auth.application.AuthException;
import dev.algorithmlearning.api.auth.application.AuthRateLimiter;
import dev.algorithmlearning.api.auth.application.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Duration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private static final String REFRESH_COOKIE = "__session";
    private final AuthService auth;
    private final AuthRateLimiter rateLimiter;
    private final AllowedOriginValidator origins;
    private final CurrentUser currentUser;

    public AuthController(AuthService auth, AuthRateLimiter rateLimiter, AllowedOriginValidator origins, CurrentUser currentUser) {
        this.auth = auth; this.rateLimiter = rateLimiter; this.origins = origins; this.currentUser = currentUser;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody Credentials request, HttpServletRequest servletRequest) {
        limit(servletRequest, request.email());
        var session = auth.register(request.email(), request.password());
        return ResponseEntity.status(HttpStatus.CREATED).header(HttpHeaders.SET_COOKIE, refreshCookie(session.refreshSession().rawToken()).toString())
                .body(AuthResponse.from(session));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody Credentials request, HttpServletRequest servletRequest) {
        limit(servletRequest, request.email());
        var session = auth.login(request.email(), request.password());
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, refreshCookie(session.refreshSession().rawToken()).toString()).body(AuthResponse.from(session));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AccessTokenResponse> refresh(@CookieValue(name = REFRESH_COOKIE, required = false) String refreshToken,
            @RequestHeader(name = HttpHeaders.ORIGIN, required = false) String origin, HttpServletRequest request) {
        requireAllowedOrigin(origin); limit(request, "refresh");
        var refreshed = auth.refresh(refreshToken);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, refreshCookie(refreshed.refreshSession().rawToken()).toString())
                .body(new AccessTokenResponse(refreshed.accessToken(), refreshed.expiresAt()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(name = REFRESH_COOKIE, required = false) String refreshToken,
            @RequestHeader(name = HttpHeaders.ORIGIN, required = false) String origin) {
        requireAllowedOrigin(origin); auth.logout(refreshToken);
        return ResponseEntity.noContent().header(HttpHeaders.SET_COOKIE, expiredCookie().toString()).build();
    }

    @GetMapping("/me")
    public UserResponse me() { return UserResponse.from(auth.me(currentUser.id())); }

    private void limit(HttpServletRequest request, String key) { if (!rateLimiter.allows(request.getRemoteAddr(), key.trim().toLowerCase())) throw new AuthException("Too many requests"); }
    private void requireAllowedOrigin(String origin) { if (!origins.allows(origin)) throw new AuthException("Invalid request origin"); }
    private static ResponseCookie refreshCookie(String value) { return ResponseCookie.from(REFRESH_COOKIE, value).httpOnly(true).secure(true).sameSite("Lax").path("/api/v1/auth").maxAge(Duration.ofDays(30)).build(); }
    private static ResponseCookie expiredCookie() { return ResponseCookie.from(REFRESH_COOKIE, "").httpOnly(true).secure(true).sameSite("Lax").path("/api/v1/auth").maxAge(Duration.ZERO).build(); }

    public record Credentials(@Email @NotBlank String email, @NotBlank @Size(min = 12, max = 128) String password) { }
    public record UserResponse(java.util.UUID id, String email, java.time.Instant createdAt) { static UserResponse from(dev.algorithmlearning.api.auth.application.AuthUser user) { return new UserResponse(user.id(), user.email(), user.createdAt()); } }
    public record AuthResponse(UserResponse user, String accessToken, java.time.Instant expiresAt) { static AuthResponse from(AuthService.AuthenticatedSession session) { return new AuthResponse(UserResponse.from(session.user()), session.accessToken(), session.expiresAt()); } }
    public record AccessTokenResponse(String accessToken, java.time.Instant expiresAt) { }
}
