# Spring Boot API

Java 21 Spring Boot modular monolith for the Algorithm Learning Platform.

## Toolchain and commands

Java 21 is pinned in [`.java-version`](../../.java-version). The checked-in
Maven Wrapper downloads Maven 3.9.16 as needed.

```sh
./mvnw test
./mvnw spring-boot:run
```

## Local configuration

The API requires PostgreSQL when it runs. Use ignored local environment values;
never commit credentials or signing keys.

| Variable | Default | Purpose |
| --- | --- | --- |
| `DATABASE_URL` | `jdbc:postgresql://localhost:5432/algorithm_learning` | JDBC URL |
| `DATABASE_USERNAME` | `algorithm_learning` | Database user |
| `DATABASE_PASSWORD` | empty | Database password |
| `APP_CORS_ALLOWED_ORIGINS` | `http://localhost:3000` | Comma-separated allowed browser origins |

Flyway owns the PostgreSQL schema in `src/main/resources/db/migration`; Hibernate
validates it and must not generate DDL. Migration integration tests use a real
PostgreSQL Testcontainers database, so they require a Docker-compatible daemon:

```sh
./mvnw -q test -Dtest='*MigrationTest,*RepositoryIntegrationTest'
```

Docker Compose is introduced by ALG-016. This task establishes the composition
root, health endpoint exposure, security boundary, request-ID propagation, and
Problem Details error seam only.
