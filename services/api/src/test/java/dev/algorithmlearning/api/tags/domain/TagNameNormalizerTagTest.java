package dev.algorithmlearning.api.tags.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class TagNameNormalizerTagTest {

    @Test
    void normalizesUnicodeWhitespaceAndCaseWithoutLosingTheDisplayName() {
        var normalized = TagNameNormalizer.normalize("  Ｄａｔａ\t\n  Structures  ");

        assertThat(normalized.displayName()).isEqualTo("Data Structures");
        assertThat(normalized.comparisonKey()).isEqualTo("data structures");
    }
}
