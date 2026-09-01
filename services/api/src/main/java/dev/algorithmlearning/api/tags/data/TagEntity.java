package dev.algorithmlearning.api.tags.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tags")
class TagEntity {
    @Id UUID id;
    @Column(name = "user_id", nullable = false) UUID userId;
    @Column(nullable = false) String name;
    @Column(name = "normalized_name", nullable = false) String normalizedName;
    @Column(name = "created_at", nullable = false) Instant createdAt;
    @Column(name = "updated_at", nullable = false) Instant updatedAt;

    protected TagEntity() { }
    TagEntity(UUID id, UUID userId, String name, String normalizedName, Instant createdAt, Instant updatedAt) {
        this.id = id; this.userId = userId; this.name = name; this.normalizedName = normalizedName; this.createdAt = createdAt; this.updatedAt = updatedAt;
    }
}
