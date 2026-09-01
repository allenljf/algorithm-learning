package dev.algorithmlearning.api.auth.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "auth_sessions")
class AuthSessionEntity {
    @Id UUID id;
    @Column(name = "user_id", nullable = false) UUID userId;
    @Column(name = "token_hash", nullable = false) byte[] tokenHash;
    @Column(name = "expires_at", nullable = false) Instant expiresAt;
    @Column(name = "revoked_at") Instant revokedAt;
    @Column(name = "last_used_at") Instant lastUsedAt;

    protected AuthSessionEntity() { }
    AuthSessionEntity(UUID id, UUID userId, byte[] tokenHash, Instant expiresAt, Instant revokedAt, Instant lastUsedAt) {
        this.id = id; this.userId = userId; this.tokenHash = tokenHash; this.expiresAt = expiresAt; this.revokedAt = revokedAt; this.lastUsedAt = lastUsedAt;
    }
}
