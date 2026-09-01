package dev.algorithmlearning.api.app.security;

import java.util.UUID;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/** Resolves the bearer-authenticated owner for repository/application calls. */
@Component
public class CurrentUser {
    public UUID id() { return UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName()); }
}
