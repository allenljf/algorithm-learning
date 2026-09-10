package dev.algorithmlearning.api.auth.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
class UserEntity {
    @Id UUID id;
    @Column(nullable = false, columnDefinition = "citext") String email;
    @Column(name = "password_hash", nullable = false) String passwordHash;
    @Column(name = "created_at", nullable = false) Instant createdAt;

    protected UserEntity() { }
    UserEntity(UUID id, String email, String passwordHash, Instant createdAt) {
        this.id = id; this.email = email; this.passwordHash = passwordHash; this.createdAt = createdAt;
    }
}
