package dev.algorithmlearning.api.courses.application;
import java.util.List;
public record CoursePage(List<CourseLesson> items, int page, int pageSize, long totalItems) {}
