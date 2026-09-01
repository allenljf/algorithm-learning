package dev.algorithmlearning.api.auth.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class JwtTokenAuthTest {

    @Test
    void issuesFifteenMinuteSignedAccessTokensForTheUser() {
        var now = Instant.parse("2026-09-01T00:00:00Z");
        var jwt = new JwtTokenService("development-signing-key-at-least-thirty-two-bytes", "issuer", "audience",
                Clock.fixed(now, ZoneOffset.UTC));
        var userId = UUID.randomUUID();

        var token = jwt.issue(userId);

        assertThat(jwt.authenticate(token)).isEqualTo(userId);
        assertThat(jwt.expiresAt(token)).isEqualTo(now.plusSeconds(15 * 60));
    }
}
