package dev.algorithmlearning.api.auth.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import dev.algorithmlearning.api.app.security.CurrentUser;
import dev.algorithmlearning.api.auth.application.AllowedOriginValidator;
import dev.algorithmlearning.api.auth.application.AuthRateLimiter;
import dev.algorithmlearning.api.auth.application.AuthSessionService;
import dev.algorithmlearning.api.auth.application.AuthService;
import java.lang.reflect.Parameter;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.CookieValue;

class AuthControllerAuthTest {

    @Test
    void usesFirebaseForwardableSessionCookieForRotationAndLogout() throws Exception {
        var auth = mock(AuthService.class);
        var rateLimiter = mock(AuthRateLimiter.class);
        when(rateLimiter.allows(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString())).thenReturn(true);
        var controller = new AuthController(auth, rateLimiter,
                new AllowedOriginValidator(List.of("https://algorithmlearning.web.app")), new CurrentUser());
        var expiry = Instant.parse("2026-10-01T00:00:00Z");
        when(auth.refresh("presented-token")).thenReturn(new AuthService.RefreshedAccessToken(
                "access-token", expiry, new AuthSessionService.SessionToken(UUID.randomUUID(), "rotated-token", expiry)));

        var refreshCookieParameter = AuthController.class.getDeclaredMethod(
                "refresh", String.class, String.class, jakarta.servlet.http.HttpServletRequest.class).getParameters()[0];
        var logoutCookieParameter = AuthController.class.getDeclaredMethod(
                "logout", String.class, String.class).getParameters()[0];

        assertThat(cookieName(refreshCookieParameter)).isEqualTo("__session");
        assertThat(cookieName(logoutCookieParameter)).isEqualTo("__session");
        assertThat(controller.refresh("presented-token", "https://algorithmlearning.web.app", request("127.0.0.1"))
                .getHeaders().getFirst("Set-Cookie"))
                .startsWith("__session=rotated-token; Path=/api/v1/auth; Max-Age=2592000; Expires=")
                .contains("; Secure", "; HttpOnly", "; SameSite=Lax");
        assertThat(controller.logout("presented-token", "https://algorithmlearning.web.app")
                .getHeaders().getFirst("Set-Cookie"))
                .startsWith("__session=; Path=/api/v1/auth; Max-Age=0; Expires=")
                .contains("; Secure", "; HttpOnly", "; SameSite=Lax")
                .doesNotContain("refresh_token");
    }

    private static String cookieName(Parameter parameter) {
        return parameter.getAnnotation(CookieValue.class).name();
    }

    private static jakarta.servlet.http.HttpServletRequest request(String remoteAddress) {
        var request = mock(jakarta.servlet.http.HttpServletRequest.class);
        when(request.getRemoteAddr()).thenReturn(remoteAddress);
        return request;
    }
}
