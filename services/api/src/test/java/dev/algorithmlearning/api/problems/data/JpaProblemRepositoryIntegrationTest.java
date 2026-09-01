package dev.algorithmlearning.api.problems.data;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.DriverManager;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/** Exercises the PostgreSQL-only search index and the tag AND-filter semantics. */
@Testcontainers(disabledWithoutDocker = true)
class JpaProblemRepositoryIntegrationTest {
    @Container static final PostgreSQLContainer<?> postgresql = new PostgreSQLContainer<>("postgres:16-alpine");

    @Test
    void searchesOwnerScopedProblemTextAndRequiresEverySelectedTag() throws Exception {
        Flyway.configure().dataSource(postgresql.getJdbcUrl(), postgresql.getUsername(), postgresql.getPassword()).load().migrate();
        try (var connection = DriverManager.getConnection(postgresql.getJdbcUrl(), postgresql.getUsername(), postgresql.getPassword()); var statement = connection.createStatement()) {
            statement.execute("insert into users (id,email,password_hash) values ('00000000-0000-0000-0000-000000000001','owner@example.test','x'),('00000000-0000-0000-0000-000000000002','other@example.test','x')");
            statement.execute("insert into tags (id,user_id,name,normalized_name) values ('00000000-0000-0000-0000-000000000011','00000000-0000-0000-0000-000000000001','array','array'),('00000000-0000-0000-0000-000000000012','00000000-0000-0000-0000-000000000001','hash map','hash map')");
            statement.execute("insert into problems (id,user_id,title,platform,difficulty,notes) values ('00000000-0000-0000-0000-000000000021','00000000-0000-0000-0000-000000000001','Two Sum','leetcode','easy','use a complement map'),('00000000-0000-0000-0000-000000000022','00000000-0000-0000-0000-000000000001','Array only','other','easy','scan'),('00000000-0000-0000-0000-000000000023','00000000-0000-0000-0000-000000000002','Private','other','easy','complement')");
            statement.execute("insert into problem_tags (problem_id,tag_id) values ('00000000-0000-0000-0000-000000000021','00000000-0000-0000-0000-000000000011'),('00000000-0000-0000-0000-000000000021','00000000-0000-0000-0000-000000000012'),('00000000-0000-0000-0000-000000000022','00000000-0000-0000-0000-000000000011')");
            try (var result = statement.executeQuery("select p.id from problems p where p.user_id = '00000000-0000-0000-0000-000000000001' and problem_search_vector(p.title,p.description,p.notes,p.key_insight,p.mistakes,p.interview_notes) @@ plainto_tsquery('simple','complement') and (select count(distinct pt.tag_id) from problem_tags pt where pt.problem_id=p.id and pt.tag_id in ('00000000-0000-0000-0000-000000000011','00000000-0000-0000-0000-000000000012'))=2 order by p.updated_at desc,p.id desc limit 20 offset 0")) {
                assertThat(result.next()).isTrue();
                assertThat(result.getString(1)).isEqualTo("00000000-0000-0000-0000-000000000021");
                assertThat(result.next()).isFalse();
            }
        }
    }
}
