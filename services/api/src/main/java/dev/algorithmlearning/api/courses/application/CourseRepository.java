package dev.algorithmlearning.api.courses.application;
import java.util.List; import java.util.Optional;
public interface CourseRepository { CourseCategory saveCategory(CourseCategory category); Optional<CourseCategory> findCategoryBySlug(String slug); CourseLesson saveLesson(CourseLesson lesson); Optional<CourseLesson> findLessonBySourceIdentity(String sourceIdentity); Optional<CourseLesson> findLessonByIdentity(String sourceIdentity); List<CourseCategory> categories(); List<CourseLesson> lessons(String category, String tag, String keyword); }
