package dev.algorithmlearning.api.problems.data;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class JpaProblemRepositoryQueryTest {
    @Test
    void unfilteredListQueryDoesNotEmbedOptionalSearchOrTagSql() throws Exception {
        var source = Files.readString(Path.of("src/main/java/dev/algorithmlearning/api/problems/data/JpaProblemRepository.java"));
        assertThat(source).contains("if (search.difficulty() != null)");
        assertThat(source).contains("if (search.platform() != null)");
        assertThat(source).contains("if (search.reviewStatus() != null)");
        assertThat(source).contains("if (!q.isEmpty())");
        assertThat(source).contains("if (!tagIds.isEmpty())");
        assertThat(source).doesNotContain("(:difficulty is null");
        assertThat(source).doesNotContain("(:platform is null");
        assertThat(source).doesNotContain("(:reviewStatus is null");
        assertThat(source).doesNotContain("(:q='' or");
        assertThat(source).doesNotContain("(:tagCount=0 or");
    }

    @Test
    void flushesProblemBeforeWritingItsJdbcTagLinks() throws Exception {
        var source = Files.readString(Path.of("src/main/java/dev/algorithmlearning/api/problems/data/JpaProblemRepository.java"));

        assertThat(source).contains("problems.saveAndFlush(entity(problem))");
        assertThat(source.indexOf("problems.saveAndFlush(entity(problem))"))
                .isLessThan(source.indexOf("insert into problem_tags"));
    }
}
