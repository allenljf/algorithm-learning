package dev.algorithmlearning.api.auth.domain;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

/** OWASP minimum Argon2id profile: 19 MiB memory, two iterations, one lane. */
public final class Argon2PasswordHasher {
    private final Argon2PasswordEncoder encoder = new Argon2PasswordEncoder(16, 32, 1, 19 * 1024, 2);

    public String hash(String password) {
        PasswordPolicy.requireValid(password);
        return encoder.encode(password);
    }

    public boolean matches(String password, String encodedHash) {
        return password != null && encodedHash != null && encoder.matches(password, encodedHash);
    }
}
