package dev.algorithmlearning.api.problems.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class ExternalUrlPolicyProblemTest {
    @Test
    void rejectsAnHttpUrlForALeetCodeProblem() {
        assertThatThrownBy(() -> ExternalUrlPolicy.requireAllowed("leetcode", "http://leetcode.com/problems/two-sum"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
