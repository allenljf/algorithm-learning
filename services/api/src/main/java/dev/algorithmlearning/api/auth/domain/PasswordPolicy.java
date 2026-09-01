package dev.algorithmlearning.api.auth.domain;

/** Password-length boundary for the credential transport contract. */
public final class PasswordPolicy {
    private PasswordPolicy() {
    }

    public static void requireValid(String password) {
        if (password == null || password.length() < 12 || password.length() > 128) {
            throw new IllegalArgumentException("Password must contain 12 to 128 characters.");
        }
    }
}
