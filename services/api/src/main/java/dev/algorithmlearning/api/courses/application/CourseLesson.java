package dev.algorithmlearning.api.courses.application;
import java.time.Instant; import java.util.List; import java.util.UUID;
public record CourseLesson(UUID id, CourseCategory category, String sourceIdentity, String sourceMarkdownPath, String englishTitle, List<String> tags, String detail, String sourceUrl, String sourceType, int sortOrder, Instant createdAt, Instant updatedAt) {}
