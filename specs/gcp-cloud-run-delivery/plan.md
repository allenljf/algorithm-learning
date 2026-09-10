# GCP Cloud Run Delivery — Feature Plan

## Planning record

- Feature ID: `gcp-cloud-run-delivery`
- Specification: [spec.md](spec.md)
- Planning method: `work-graph` with three perspectives (planner,
  implementer, evaluator). This is required because delivery crosses GitHub
  OIDC, GCP IAM, Cloud SQL networking, Spring Boot startup, and an external
  production environment.
- Execution contract: selected per ready task by `$execution-strategy`.

## Delivery strategy

First add a reviewable, idempotent GCP bootstrap script and operator document;
they define resources and IAM bindings but never accept or print secret values.
In parallel, make the API prove that its non-web Cloud Run Job mode completes
after Flyway. Next add the OIDC deployment workflow, which serializes releases,
executes the migration Job, and deploys the Flyway-disabled service. Only the
final acceptance task writes to the authorized GCP project or requires the
user's existing GCP login and manually entered Secret Manager values.

## Planned phases

| Phase | Tasks | Outcome |
|---|---|---|
| 1. Delivery foundations | GCP-001, GCP-002 | Reviewable resource/IAM bootstrap and a tested migration-only API mode |
| 2. Continuous delivery | GCP-003 | OIDC/WIF production workflow and operator runbook |
| 3. First controlled release | GCP-004 | Provisioned GCP resources, successful migration, deployed API, and health evidence |

## Dependency and parallelization rules

- `GCP-001` and `GCP-002` are independently ready: one changes only
  infrastructure documentation/scripts and the other only the API migration
  mode plus its tests.
- `GCP-003` consumes both foundations and is the sole writer of the GitHub
  deployment workflow.
- `GCP-004` is the only task allowed to execute GCP-mutating bootstrap or
  deployment commands. It depends on the reviewed workflow and requires the
  user to have configured non-secret GitHub Environment variables and Secret
  Manager values without exposing those values in this conversation.
- No task adds a service-account key, GitHub Secret containing GCP credentials,
  GKE resource, or Flutter hosting.

## Architecture guardrails carried into execution

- GCP project: `alert-study-508214-s5` / `730295148186`; region: `asia-east1`.
- WIF accepts only `allenljf/algorithm-learning`, `refs/heads/main`, and the
  `production` GitHub Environment.
- `algorithm-learning-migrate` is the only component that enables Flyway;
  `algorithm-learning-api` always sets `SPRING_FLYWAY_ENABLED=false`.
- Runtime secrets remain in Secret Manager and are referenced by name only.
- Cloud SQL PostgreSQL 16 uses private IP and Cloud Run Direct VPC egress.

## Completion definition

The feature is complete when all four tasks are closed through
`$verification-closeout`, the GCP resource script and workflow contain no
secret value or long-lived credential path, the migration Job succeeds once,
and the deployed API reports ready against Cloud SQL.

