# Neon Migration/Runtime Role Separation — Feature Plan

## Planning record

- Feature ID: `neon-role-separation`
- Specification: [spec.md](spec.md)
- Planning method: `work-graph` with three perspectives (planner, implementer,
  evaluator), because the change crosses Neon role ownership/grants, GCP Secret
  Manager, GitHub Environment variables, the production workflow, and a serialized
  production release while the agent must never handle a password.
- Execution contract: selected per ready task by `$execution-strategy`.

## Delivery strategy

Prepare the password-free tooling first, then perform the operator-gated split
release. `NRS-001` adds the committed SQL artifact that creates/reconciles the
DML-only runtime role and the DDL migration role and the split runbook in
`infra/gcp/README.md`. `NRS-002` adds the second Secret Manager container and its
Secret Accessor to `infra/gcp/bootstrap.sh` and rebinds the production workflow so
the migration Job uses the migration username/secret while the service keeps the
runtime username/secret. `NRS-003` is the only operator-gated release: the
operator creates/resets the roles and sets both secrets, the GitHub variables are
updated, and the serialized workflow migrates once and serves readiness as the
runtime role, with the operator attesting the DDL-denial probe.

## Planned phases

| Phase | Tasks | Outcome |
|---|---|---|
| 1. Split tooling | NRS-001, NRS-002 | Password-free split SQL artifact + runbook; bootstrap and workflow carry the second username/secret |
| 2. Split release | NRS-003 | Runtime role serves with no DDL; migration role migrates; readiness proven |

## Dependency and parallelization rules

- `NRS-001` and `NRS-002` have no dependencies and write disjoint files
  (`infra/gcp/neon-role-separation.sql` + `infra/gcp/README.md` versus
  `infra/gcp/bootstrap.sh` + `.github/workflows/gcp-production-deploy.yml`), so
  they share the `neon-split-foundations` parallel group.
- `NRS-003` depends on both and is the only task allowed to create/reset the
  roles, set the secrets, update the GitHub variables, and trigger the production
  redeploy; it requires the operator's private Neon and Secret Manager setup.
- No task changes product, schema, migration, API, or client code, so no other
  feature's artifacts are touched.

## Architecture guardrails carried into execution

- `algorithm_learning_app` is the runtime (DML-only) role; it is not a member of
  `table_owners`, keeps `USAGE` but not `CREATE` on `public`, and has no access to
  `flyway_schema_history`.
- `algorithm_learning_migrate` is the migration (DDL) role and the only member of
  `table_owners`; it runs Flyway only in `algorithm-learning-migrate` on the direct
  endpoint.
- Default privileges are installed for the object-creating role (and
  `table_owners` as a fallback) so future Flyway objects stay DML-usable by the
  runtime role.
- `NEON_DATABASE_USERNAME` / `algorithm-learning-db-password` name the runtime
  role; `NEON_MIGRATION_DATABASE_USERNAME` /
  `algorithm-learning-db-migration-password` name the migration role. No secret
  value is read, printed, committed, or stored in a GitHub variable.
- WIF, SHA-pinned actions, serialized deploy, the pooled/direct endpoint split,
  `prepareThreshold=0`, and forward-only migrations are unchanged.

## Completion definition

The feature is complete when `NRS-001` and `NRS-002` pass their task-limited
verification, the operator has created/reset both roles and set both secrets
without exposing a password, and a serialized release migrates once as
`algorithm_learning_migrate` and serves `/actuator/health/readiness` as
`algorithm_learning_app` with a proven DDL denial.
