package dev.algorithmlearning.api.courses.data;
import static org.assertj.core.api.Assertions.assertThat;
import java.nio.file.Files; import java.nio.file.Path; import org.junit.jupiter.api.Test;
class CourseMigrationTest { @Test void definesOnlyIndependentCourseStorage(){try {var sql=Files.readString(Path.of("src/main/resources/db/migration/V3__course_reading_system.sql"));assertThat(sql).contains("create table course_categories", "create table course_lessons", "references course_categories");assertThat(sql).doesNotContain("references problems", "references solutions", "references tags", "references reviews");}catch(Exception e){throw new AssertionError(e);}} }
