# Neon Least-Privilege Role — Feature Plan

## Planning record

- Feature ID: `neon-least-privilege-role`
- Specification: [spec.md](spec.md)
- Planning method: `work-graph` with three perspectives (planner, implementer,
  evaluator), because the change touches Neon role ownership and grants, GCP
  Secret Manager, GitHub Environment variables, and a serialized production
  redeploy while the agent must never handle the password.
- Execution contract: selected per ready task by `$execution-strategy`.

## Delivery strategy

Prepare a password-free operator artifact and runbook first, then perform the
operator-gated rotation. `NLP-001` adds the committed SQL ownership/grant
template and the `infra/gcp/README.md` rotation procedure; it changes no
workflow or application code because the delivery already reads the
`NEON_DATABASE_USERNAME` variable and the `algorithm-learning-db-password`
secret by name. `NLP-002` is the only operator-gated release: the operator
creates the role and sets the secret, the GitHub variable is updated, and the
serialized workflow redeploys and must report ready under the new credential.

## Planned phases

| Phase | Tasks | Outcome |
|---|---|---|
| 1. Role tooling | NLP-001 | Password-free ownership/grant artifact and rotation runbook |
| 2. Credential rotation | NLP-002 | Neon role created, secret/variable rotated, redeployed API ready as the dedicated role |

## Dependency and parallelization rules

- `NLP-001` has no dependency; it writes only `infra/gcp` and may extend
  `scripts/neon-cloud-run-setup-wizard.sh`; it never handles a secret value.
- `NLP-002` depends on `NLP-001` and is the only task allowed to rotate the
  credential and trigger the production redeploy; it requires the operator's
  private Neon and Secret Manager setup.
- No other task touches `infra/gcp`, the workflow, or the Neon credential, so
  the two tasks are strictly sequential.

## Architecture guardrails carried into execution

- One dedicated application role owns the application schema and its existing
  objects; it is not `neondb_owner` and holds no
  `SUPERUSER`/`CREATEDB`/`CREATEROLE`/`REPLICATION`/`BYPASSRLS`.
- Flyway runs only in `algorithm-learning-migrate` on the direct endpoint; the
  service keeps `SPRING_FLYWAY_ENABLED=false` and the pooled endpoint.
- `NEON_DATABASE_USERNAME` names the dedicated role and
  `algorithm-learning-db-password` holds its password; no secret value is read,
  printed, committed, or stored in a GitHub variable.
- WIF, SHA-pinned actions, serialized deploy, and forward-only migrations are
  unchanged.

## Completion definition

The feature is complete when `NLP-001` passes its task-limited verification, the
operator has created the dedicated role and rotated the secret and variable
without exposing a password, and a serialized release migrates once and serves
`/actuator/health/readiness` as ready under the new credential.
