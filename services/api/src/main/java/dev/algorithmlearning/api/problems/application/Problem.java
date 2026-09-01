package dev.algorithmlearning.api.problems.application;

import java.time.Instant;
import java.util.UUID;
import java.util.List;
import dev.algorithmlearning.api.tags.application.Tag;

public record Problem(UUID id, UUID userId, String title, String platform, String externalProblemId, String externalUrl, String difficulty,
        String description, String notes, String keyInsight, String timeComplexity, String spaceComplexity, String mistakes, String interviewNotes,
        Instant createdAt, Instant updatedAt, List<Tag> tags) { }
