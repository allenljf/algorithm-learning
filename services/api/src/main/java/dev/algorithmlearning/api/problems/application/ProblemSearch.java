package dev.algorithmlearning.api.problems.application;

import java.util.List;
import java.util.UUID;

public record ProblemSearch(String query, String difficulty, String platform, List<UUID> tagIds, String reviewStatus, String sort, int page, int pageSize) { }
