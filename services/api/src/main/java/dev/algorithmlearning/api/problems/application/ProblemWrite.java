package dev.algorithmlearning.api.problems.application;

import java.util.List;
import java.util.UUID;

public record ProblemWrite(String title, String platform, String externalProblemId, String externalUrl, String difficulty,
        String description, String notes, String keyInsight, String timeComplexity, String spaceComplexity, String mistakes, String interviewNotes,
        List<UUID> tagIds) { }
