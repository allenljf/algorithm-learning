package dev.algorithmlearning.api.auth.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class RefreshTokenSecurityTest {

    @Test
    void createsOpaqueTokensAndVerifiesTheirKeyedHash() {
        var tokens = new RefreshTokenService("test-server-side-key-material");

        var rawToken = tokens.newToken();

        assertThat(rawToken).doesNotContain(".");
        assertThat(tokens.matches(rawToken, tokens.hash(rawToken))).isTrue();
    }
}
