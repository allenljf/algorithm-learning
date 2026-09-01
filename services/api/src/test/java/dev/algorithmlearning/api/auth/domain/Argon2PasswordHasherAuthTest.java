package dev.algorithmlearning.api.auth.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class Argon2PasswordHasherAuthTest {

    @Test
    void hashesPasswordsWithArgon2idAndVerifiesTheOriginalOnly() {
        var hasher = new Argon2PasswordHasher();

        var encoded = hasher.hash("a-correct-horse-battery-password");

        assertThat(encoded).startsWith("$argon2id$");
        assertThat(hasher.matches("a-correct-horse-battery-password", encoded)).isTrue();
        assertThat(hasher.matches("another-correct-horse-password", encoded)).isFalse();
    }
}
