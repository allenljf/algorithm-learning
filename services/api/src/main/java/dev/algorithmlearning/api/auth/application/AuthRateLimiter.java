package dev.algorithmlearning.api.auth.application;

public interface AuthRateLimiter {
    boolean allows(String remoteAddress, String normalizedAccountKey);
}
