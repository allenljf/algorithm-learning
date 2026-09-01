package dev.algorithmlearning.api.auth.data;

import dev.algorithmlearning.api.auth.application.AuthUser;
import dev.algorithmlearning.api.auth.application.AuthUserRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class JpaAuthUserRepository implements AuthUserRepository {
    private final UserJpaRepository users;
    public JpaAuthUserRepository(UserJpaRepository users) { this.users = users; }
    public AuthUser save(AuthUser user) { return map(users.save(new UserEntity(user.id(), user.email(), user.passwordHash(), user.createdAt()))); }
    public Optional<AuthUser> findByNormalizedEmail(String email) { return users.findByEmail(email).map(this::map); }
    public Optional<AuthUser> findById(UUID id) { return users.findById(id).map(this::map); }
    private AuthUser map(UserEntity entity) { return new AuthUser(entity.id, entity.email, entity.passwordHash, entity.createdAt); }
}
