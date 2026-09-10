package dev.algorithmlearning.api;

import org.springframework.boot.WebApplicationType;

record MigrationOnlyMode(boolean enabled) {

    static MigrationOnlyMode fromEnvironmentValue(String value) {
        return new MigrationOnlyMode(Boolean.parseBoolean(value));
    }

    WebApplicationType webApplicationType() {
        return enabled ? WebApplicationType.NONE : WebApplicationType.SERVLET;
    }

    boolean exitsAfterStartup() {
        return enabled;
    }
}
