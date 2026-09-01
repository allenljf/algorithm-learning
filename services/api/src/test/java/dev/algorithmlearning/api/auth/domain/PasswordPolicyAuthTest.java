package dev.algorithmlearning.api.auth.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class PasswordPolicyAuthTest {

    @Test
    void rejectsPasswordsShorterThanTwelveCharacters() {
        assertThatThrownBy(() -> PasswordPolicy.requireValid("shortpass"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
