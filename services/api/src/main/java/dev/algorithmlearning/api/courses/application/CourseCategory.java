package dev.algorithmlearning.api.courses.application;
import java.util.UUID;
public record CourseCategory(UUID id, String slug, String displayName, int sortOrder) {}
