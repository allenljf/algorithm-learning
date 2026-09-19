# Neon Cloud Run Delivery — Feature Plan

## Planning record

- Feature ID: `neon-cloud-run-delivery`
- Specification: [spec.md](spec.md)
- Planning method: `work-graph` with three perspectives (planner, implementer,
  evaluator), because delivery crosses GitHub OIDC, GCP IAM, Cloud Run job
  lifecycle, Neon networking, and an external production environment.
- Execution contract: selected per ready task by `$execution-strategy`.

## Delivery strategy

Change the database and network path only. `infra/gcp/bootstrap.sh` loses every
Cloud SQL/VPC/private-service-access step while keeping the API enablement,
Artifact Registry, Secret Manager containers, service accounts, WIF, and IAM
bindings. The production workflow keeps its OIDC/serialization/immutable-image
shape but reads `NEON_MIGRATION_JDBC_URL`, `NEON_SERVICE_JDBC_URL`, and
`NEON_DATABASE_USERNAME`, drops the Cloud SQL connector flags, and disables JDBC
prepared-statement caching for the pooled serving datasource. The final task is
the only cloud-mutating first release and requires the user's private setup.

## Planned phases

| Phase | Tasks | Outcome |
|---|---|---|
| 1. Neon delivery foundations | NEO-001, NEO-002 | Neon-compatible bootstrap/runbook and a retargeted OIDC workflow |
| 2. First Neon release | NEO-003 | Provisioned GCP foundation, successful migration, deployed API, health evidence |

## Dependency and parallelization rules

- `NEO-001` has no dependency; it edits only `infra/gcp` bootstrap/runbook.
- `NEO-002` depends on `NEO-001` and carries the completed `GCP-002`
  migration-only mode; it edits only the deployment workflow and API datasource
  configuration.
- `NEO-003` is the only task allowed to run cloud-mutating commands and requires
  the reviewed workflow plus private Secret Manager/Environment configuration.
- The Cloud SQL-based `GCP-004` is superseded by this feature's `NEO-003` and
  must not run.

## Architecture guardrails carried into execution

- GCP project `alert-study-508214-s5`; region `asia-east1`; no VPC, subnet,
  private-service access, or Cloud SQL.
- WIF accepts only `allenljf/algorithm-learning`, `refs/heads/main`, and the
  `production` GitHub Environment.
- `algorithm-learning-migrate` is the only Flyway-enabled component and uses the
  direct Neon endpoint; `algorithm-learning-api` always sets
  `SPRING_FLYWAY_ENABLED=false` and uses the pooled endpoint.
- Runtime secrets stay in Secret Manager and are referenced by name only.
- `GCP_CORS_ALLOWED_ORIGINS` is intentionally absent, never an empty variable.

## Completion definition

The feature is complete when `NEO-001` and `NEO-002` pass their task-limited
verification, the bootstrap and workflow contain no Cloud SQL/private-network or
secret value, the migration Job succeeds once against Neon, and the deployed API
reports ready at `/actuator/health/readiness`.
