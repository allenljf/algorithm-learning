package dev.algorithmlearning.api.app.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class ApiPropertiesTest {

    @Test
    void retainsConfiguredCorsOriginsWithoutAddingAWildcard() {
        var properties = new ApiProperties(new ApiProperties.Cors(List.of("https://app.example.test")),
                new ApiProperties.Security("jwt", "issuer", "audience", "refresh"));

        assertThat(properties.cors().allowedOrigins()).containsExactly("https://app.example.test");
        assertThat(properties.cors().allowedOrigins()).doesNotContain("*");
    }
}
