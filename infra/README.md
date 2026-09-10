# Infrastructure

This directory holds non-application operational assets and the local API plus
PostgreSQL runtime.

- [`env/.env.example`](env/.env.example) documents required local environment
  variable names with placeholders only.
- [`compose.yaml`](compose.yaml) builds the API and starts PostgreSQL on an
  isolated Compose network. PostgreSQL data uses the named `postgres-data`
  volume; Flyway runs once during API startup before Hibernate validates the
  schema.
- [Kubernetes deployment target](kubernetes/README.md) documents the future
  provider-neutral API, networking, probes, migration, and managed-database
  architecture. It intentionally contains no production manifests.

## Run locally with Docker Compose

Docker Compose requires local secret values but no committed secrets. Create the
ignored environment file, set the same strong database password in both database
password fields, and replace the JWT and refresh-hash placeholders with distinct
random values of at least 32 characters:

```sh
cp infra/env/.env.example infra/env/.env
```

Start and inspect the two services from the repository root:

```sh
docker compose -f infra/compose.yaml up -d --build
docker compose -f infra/compose.yaml ps
```

Wait until both services report `healthy`. The API is then at
`http://localhost:8080`; the public probes are
`/actuator/health/liveness` and `/actuator/health/readiness`. The latter checks
whether the API is ready to serve against PostgreSQL.

The API receives its database host, credentials, CORS origin, and signing/hash
keys only through environment variables. Keep `POSTGRES_*` and `DATABASE_*`
values paired if you change the default database or user. It does not log those
values. Flutter runs from the host for hot reload; set its API base URL to the
host API address.

Stop the local stack while preserving its database volume:

```sh
docker compose -f infra/compose.yaml down
```

To intentionally discard all local database data, add `--volumes`. That action
is irreversible for the Compose-managed database.
