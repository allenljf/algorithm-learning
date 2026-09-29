package dev.algorithmlearning.api.courses.application;
import java.util.List;
public record CourseImport(String categorySlug, String categoryDisplayName, int categorySortOrder, String sourceIdentity, String sourceMarkdownPath, String englishTitle, List<String> tags, String detail, String sourceUrl, String sourceType, int sortOrder) {}
