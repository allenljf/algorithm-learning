package dev.algorithmlearning.api.courses.data;
import java.util.*; import org.springframework.data.jpa.repository.JpaRepository;
interface CourseCategoryJpaRepository extends JpaRepository<CourseCategoryEntity,UUID>{ Optional<CourseCategoryEntity> findBySlug(String slug); List<CourseCategoryEntity> findAllByOrderBySortOrderAscSlugAsc(); }
interface CourseLessonJpaRepository extends JpaRepository<CourseLessonEntity,UUID>{ Optional<CourseLessonEntity> findBySourceIdentity(String sourceIdentity); List<CourseLessonEntity> findAllByOrderBySortOrderAscIdAsc(); }
