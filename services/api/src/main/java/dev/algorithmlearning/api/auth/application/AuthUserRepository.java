package dev.algorithmlearning.api.auth.application;

import java.util.Optional;
import java.util.UUID;

public interface AuthUserRepository {
    AuthUser save(AuthUser user);
    Optional<AuthUser> findByNormalizedEmail(String email);
    Optional<AuthUser> findById(UUID id);
}
