package dev.algorithmlearning.api.problems.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ProblemsControllerProblemControllerTest {
    @Test
    void declaresTheVersionedProblemCollectionPath() {
        assertThat(ProblemsController.PATH).isEqualTo("/api/v1/problems");
    }
}
