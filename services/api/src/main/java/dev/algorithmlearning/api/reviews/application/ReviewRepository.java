package dev.algorithmlearning.api.reviews.application;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface ReviewRepository {
    Review save(Review review);

    Optional<Review> latest(UUID userId, UUID problemId);

    /**
     * Owned problem ids that are due, ordered oldest-due first. A never-reviewed
     * problem is due from its creation time.
     */
    List<UUID> dueProblemIds(UUID userId, Instant now, int limit, int offset);

    Map<UUID, ProblemReviewState> reviewStates(UUID userId, Collection<UUID> problemIds);

    List<Review> history(UUID userId, UUID problemId, int limit, int offset);
}
