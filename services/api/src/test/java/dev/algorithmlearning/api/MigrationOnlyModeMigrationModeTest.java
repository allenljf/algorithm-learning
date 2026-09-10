package dev.algorithmlearning.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.WebApplicationType;

class MigrationOnlyModeMigrationModeTest {

    @Test
    void usesANonWebApplicationAndExitsAfterMigrationWhenEnabled() {
        var mode = MigrationOnlyMode.fromEnvironmentValue("true");

        assertThat(mode.webApplicationType()).isEqualTo(WebApplicationType.NONE);
        assertThat(mode.exitsAfterStartup()).isTrue();
    }

    @Test
    void preservesNormalServletStartupWhenMigrationOnlyModeIsNotEnabled() {
        var mode = MigrationOnlyMode.fromEnvironmentValue("false");

        assertThat(mode.webApplicationType()).isEqualTo(WebApplicationType.SERVLET);
        assertThat(mode.exitsAfterStartup()).isFalse();
    }
}
