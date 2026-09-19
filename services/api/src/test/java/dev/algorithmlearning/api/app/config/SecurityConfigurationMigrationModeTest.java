package dev.algorithmlearning.api.app.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import dev.algorithmlearning.api.auth.application.AuthSessionRepository;
import dev.algorithmlearning.api.auth.application.AuthUserRepository;
import dev.algorithmlearning.api.dashboard.application.DashboardRepository;
import dev.algorithmlearning.api.problems.application.ProblemRepository;
import dev.algorithmlearning.api.problems.application.SolutionRepository;
import dev.algorithmlearning.api.reviews.application.ReviewRepository;
import dev.algorithmlearning.api.tags.application.TagRepository;
import dev.algorithmlearning.api.tags.application.TagService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.security.web.SecurityFilterChain;

class SecurityConfigurationMigrationModeTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withUserConfiguration(SecurityConfiguration.class)
            .withBean(ApiProperties.class, () -> new ApiProperties(
                    new ApiProperties.Cors(List.of()),
                    new ApiProperties.Security(
                            "migration-mode-jwt-key-0123456789abcdef",
                            "algorithm-learning-api",
                            "algorithm-learning-client",
                            "migration-mode-refresh-key-0123456789abcdef")))
            .withBean(AuthSessionRepository.class, () -> mock(AuthSessionRepository.class))
            .withBean(AuthUserRepository.class, () -> mock(AuthUserRepository.class))
            .withBean(TagRepository.class, () -> mock(TagRepository.class))
            .withBean(ProblemRepository.class, () -> mock(ProblemRepository.class))
            .withBean(SolutionRepository.class, () -> mock(SolutionRepository.class))
            .withBean(ReviewRepository.class, () -> mock(ReviewRepository.class))
            .withBean(DashboardRepository.class, () -> mock(DashboardRepository.class));

    @Test
    void nonWebStartupSkipsTheServletSecurityChainButKeepsServiceBeans() {
        runner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).doesNotHaveBean(SecurityFilterChain.class);
            assertThat(context).hasSingleBean(TagService.class);
        });
    }
}
