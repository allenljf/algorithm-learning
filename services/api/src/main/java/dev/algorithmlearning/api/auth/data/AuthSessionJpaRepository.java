package dev.algorithmlearning.api.auth.data;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface AuthSessionJpaRepository extends JpaRepository<AuthSessionEntity, UUID> {
    Optional<AuthSessionEntity> findByTokenHash(byte[] tokenHash);
}
