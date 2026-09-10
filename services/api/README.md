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
| `APP_JWT_KEY` | development-only local value | HS256 access-token signing key; replace in every deployed environment |
| `APP_JWT_ISSUER` | `algorithm-learning-api` | Access-token issuer |
| `APP_JWT_AUDIENCE` | `algorithm-learning-client` | Access-token audience |
| `APP_REFRESH_HASH_KEY` | development-only local value | Server-side HMAC key for refresh-session hashes; replace in every deployed environment |

Flyway owns the PostgreSQL schema in `src/main/resources/db/migration`; Hibernate
validates it and must not generate DDL. Migration integration tests use a real
PostgreSQL Testcontainers database, so they require a Docker-compatible daemon:

```sh
./mvnw -q test -Dtest='*MigrationTest,*RepositoryIntegrationTest'
```

## Authentication API

`/api/v1/auth/register`, `/login`, `/refresh`, `/logout`, and `/me` implement
the session contract. Passwords use Argon2id (19 MiB, two iterations, one lane).
Access tokens are HS256 JWTs with a 15-minute lifetime; refresh values are opaque,
rotated on use, and persisted only as keyed hashes. The server emits refresh values
only through a `Secure`, `HttpOnly`, `SameSite=Lax` cookie under `/api/v1/auth`.
Browser-origin requests to refresh and logout must match `APP_CORS_ALLOWED_ORIGINS`.

## Tags API

Authenticated callers can create and list their own tags at `/api/v1/tags`.
`POST` accepts `{ "name": "..." }`; names are NFKC-normalized, trimmed,
whitespace-collapsed, and case-folded for owner-local idempotency. A new tag
returns `201` with `Location`; a normalized duplicate returns the existing tag
with `200`. `GET /api/v1/tags?q=...` searches only the caller's tags.

## Docker Compose

For the full local API and PostgreSQL lifecycle, follow the instructions in
[`infra/README.md`](../../infra/README.md). Compose builds this Dockerfile,
waits for PostgreSQL to become healthy, then starts the API so Flyway applies
forward migrations before Hibernate validates the schema. API readiness is
available at `/actuator/health/readiness`.
