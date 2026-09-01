package dev.algorithmlearning.api.auth.application;

import java.util.List;

public final class AllowedOriginValidator {
    private final List<String> allowedOrigins;
    public AllowedOriginValidator(List<String> allowedOrigins) { this.allowedOrigins = List.copyOf(allowedOrigins); }
    public boolean allows(String origin) { return origin == null || allowedOrigins.contains(origin); }
}
