package dev.algorithmlearning.api.app.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers(disabledWithoutDocker = true)
class SchemaMigrationTest {

    @Container
    static final PostgreSQLContainer<?> postgresql = new PostgreSQLContainer<>("postgres:16-alpine");

    @Test
    void migratesAnEmptyPostgresDatabaseToTheInitialProductSchema() throws Exception {
        migrate();

        try (var connection = DriverManager.getConnection(
                postgresql.getJdbcUrl(), postgresql.getUsername(), postgresql.getPassword())) {
            assertThat(tableNames(connection)).contains(
                    "auth_sessions", "problem_tags", "problems", "reviews", "solutions", "tags", "users");
            assertThat(connection.getMetaData().getColumns(null, "public", "users", "email").next()).isTrue();
            assertThat(connection.getMetaData().getColumns(null, "public", "reviews", "interval_days").next()).isTrue();
            assertThat(connection.getMetaData().getColumns(null, "public", "reviews", "ease_factor").next()).isTrue();

            var userId = "00000000-0000-0000-0000-000000000001";
            execute(connection, "insert into users (id, email, password_hash) values ('" + userId
                    + "', 'learner@example.test', 'argon2-hash')");

            assertThatThrownBy(() -> execute(connection,
                    "insert into problems (id, user_id, title, platform, difficulty) values "
                            + "('00000000-0000-0000-0000-000000000002', '" + userId
                            + "', 'Valid title', 'invalid', 'easy')"))
                    .isInstanceOf(SQLException.class);

            execute(connection,
                    "insert into problems (id, user_id, title, platform, external_problem_id, difficulty) values "
                            + "('00000000-0000-0000-0000-000000000002', '" + userId
                            + "', 'Valid title', 'leetcode', '1', 'easy')");
            assertThatThrownBy(() -> execute(connection,
                    "insert into solutions (id, problem_id, language, code) values "
                            + "('00000000-0000-0000-0000-000000000003', "
                            + "'00000000-0000-0000-0000-000000000002', 'java', '   ')")).isInstanceOf(SQLException.class);

            execute(connection,
                    "insert into solutions (id, problem_id, language, code) values "
                            + "('00000000-0000-0000-0000-000000000003', "
                            + "'00000000-0000-0000-0000-000000000002', 'java', 'class Solution {}')");
            execute(connection,
                    "insert into reviews (id, problem_id, confidence, reviewed_at, next_review_at, policy_version) values "
                            + "('00000000-0000-0000-0000-000000000004', "
                            + "'00000000-0000-0000-0000-000000000002', 4, now(), now(), 'fixed-v1')");
            execute(connection,
                    "insert into reviews (id, problem_id, confidence, reviewed_at, next_review_at, policy_version, interval_days, ease_factor, repetitions) values "
                            + "('00000000-0000-0000-0000-000000000005', "
                            + "'00000000-0000-0000-0000-000000000002', 3, now(), now(), 'adaptive-v1', 4, 2.50, 1)");
            assertThatThrownBy(() -> execute(connection,
                    "insert into reviews (id, problem_id, confidence, reviewed_at, next_review_at, policy_version) values "
                            + "('00000000-0000-0000-0000-000000000006', "
                            + "'00000000-0000-0000-0000-000000000002', 3, now(), now(), 'adaptive-v1')"))
                    .isInstanceOf(SQLException.class);
            assertThatThrownBy(() -> execute(connection,
                    "insert into reviews (id, problem_id, confidence, reviewed_at, next_review_at, policy_version, interval_days, ease_factor, repetitions) values "
                            + "('00000000-0000-0000-0000-000000000007', "
                            + "'00000000-0000-0000-0000-000000000002', 3, now(), now(), 'adaptive-v1', 4, 3.50, 1)"))
                    .isInstanceOf(SQLException.class);
            execute(connection, "delete from users where id = '" + userId + "'");

            assertThat(rowCount(connection, "problems")).isZero();
            assertThat(rowCount(connection, "solutions")).isZero();
            assertThat(rowCount(connection, "reviews")).isZero();
        }
    }

    private static void migrate() {
        Flyway.configure()
                .dataSource(postgresql.getJdbcUrl(), postgresql.getUsername(), postgresql.getPassword())
                .load()
                .migrate();
    }

    private static List<String> tableNames(java.sql.Connection connection) throws SQLException {
        try (var statement = connection.prepareStatement(
                "select table_name from information_schema.tables where table_schema = 'public' order by table_name");
                var resultSet = statement.executeQuery()) {
            var tables = new java.util.ArrayList<String>();
            while (resultSet.next()) {
                tables.add(resultSet.getString(1));
            }
            return tables;
        }
    }

    private static int rowCount(java.sql.Connection connection, String table) throws SQLException {
        try (var statement = connection.createStatement(); var resultSet = statement.executeQuery("select count(*) from " + table)) {
            resultSet.next();
            return resultSet.getInt(1);
        }
    }

    private static void execute(java.sql.Connection connection, String sql) throws SQLException {
        try (var statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }
}
