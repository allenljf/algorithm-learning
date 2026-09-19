# Neon Least-Privilege Role Specification

## Document status

- Feature ID: `neon-least-privilege-role`
- Status: governed; ready for `$work-graph`
- Requirement baseline: [`../../requirement.md`](../../requirement.md), sections 7
  and 8
- Relates to: completes the follow-up recorded in the `NEO-003` closeout and
  `specs/neon-cloud-run-delivery/spec.md` section 7 (the runtime currently
  authenticates as the Neon owner `neondb_owner`)
- Intake mode: `quick-analysis`
- Updated: 2026-09-19
- Governance mode: `update-docs + infer`
- Execution contract: not selected; chosen per ready task after planning

This feature removes the Neon owner credential from the production delivery path.
The API and its migration Job must authenticate as a dedicated, least-privilege
Neon role instead of `neondb_owner`. It changes no product behavior, schema, API
contract, or client code.

## 1. Goal

Rotate the production database credential from the Neon owner `neondb_owner` to a
dedicated application role that holds only the privileges the API and Flyway
need, update the GCP Secret Manager password and the GitHub `production`
variable, redeploy, and prove the served API runs as the new role and stays
ready. The agent never handles the role password; the operator creates the role
and enters the secret value privately.

## 2. Chosen architecture

```text
Operator (Neon owner, out of band)
  ├─ CREATE ROLE <app role> LOGIN PASSWORD <secret>
  ├─ grant app-database/schema privileges + object ownership
  └─ set GCP Secret Manager algorithm-learning-db-password = <secret>

GitHub Actions (main + protected production environment)
  └─ Cloud Run migration Job (Flyway, direct Neon endpoint)
       └─ Cloud Run API service (Flyway disabled, pooled Neon endpoint)
            └─ Neon as <app role>  (not neondb_owner, not a superuser)
```

- `NEON_DATABASE_USERNAME` names the dedicated application role.
- `algorithm-learning-db-password` holds the rotated role password.
- Existing pooled/direct endpoint split, `prepareThreshold=0`,
  `sslmode=require&channelBinding=require`, and the migration-before-deploy
  serialization are unchanged.

Governed topology decision: **one dedicated application role** that owns the
application schema so Flyway can run migrations, replacing the Neon owner. The
role is confined to the `neondb` database and its application schema and must not
be a superuser or hold `CREATEDB`, `CREATEROLE`, `REPLICATION`, or `BYPASSRLS`.
Splitting into separate DDL migration and DML-only runtime roles would require
two usernames, two secrets, a bootstrap/workflow change, and default-privilege
wiring; it is a larger config-contract change, is out of scope here, and is
recorded as the future hardening option that the residual serving DDL capability
would justify.

## 3. Scope

### In scope

- A password-free, committed operator artifact (SQL ownership/rotation script
  and/or wizard step) that creates or rotates the dedicated Neon role, transfers
  ownership of the application objects to it, and grants only the required
  privileges, without accepting, printing, storing, or embedding the password.
- Runbook updates in `infra/gcp/README.md` for the rotation procedure: create or
  rotate the role, transfer object ownership and grant privileges, update the
  Secret Manager value, update the GitHub `production` variable, redeploy,
  verify, and roll back.
- Workflow-config reconciliation if the role rename requires it (the workflow
  already reads `NEON_DATABASE_USERNAME`, so likely no change).
- An operator-gated release task that performs the rotation and proves readiness
  with the new credential.

### Out of scope

- Product, schema, migration, API-contract, client, or Compose changes.
- Splitting into separate migration and runtime roles (recorded as a future
  hardening option).
- Deleting the Neon project, branch, database, or the Neon-managed
  `neondb_owner` role.
- Any change to the WIF, Artifact Registry, Cloud Run, or Secret Manager
  topology beyond the password value and role name.
- Reading, printing, or storing any secret value in source, GitHub, or chat.

## 4. Required configuration contract

### 4.1 GitHub Environment (`production`) variables

| Variable | Change |
|---|---|
| `NEON_DATABASE_USERNAME` | New value: the dedicated application role name (was `neondb_owner`) |
| `NEON_MIGRATION_JDBC_URL` | Unchanged (password-free direct endpoint) |
| `NEON_SERVICE_JDBC_URL` | Unchanged (password-free pooled endpoint) |
| other `GCP_*` variables | Unchanged |

### 4.2 GCP Secret Manager secrets

| Secret Manager ID | Change |
|---|---|
| `algorithm-learning-db-password` | New version: the dedicated role's password (rotated from the owner password) |
| `algorithm-learning-jwt-key`, `algorithm-learning-refresh-hash-key` | Unchanged |

The runtime service account keeps Secret Accessor only for these named secrets.
The role that owns the Neon objects must be the connection role, because Flyway
runs DDL in the migration Job.

## 5. Acceptance criteria

- **AC-NLP-01:** A committed, password-free operator artifact creates or rotates
  a dedicated Neon role confined to the `neondb` database and its application
  schema, with no `SUPERUSER`/`CREATEDB`/`CREATEROLE`/`REPLICATION`/`BYPASSRLS`
  and no ownership of unrelated objects, and transfers ownership of the existing
  application objects to it so Flyway can migrate; it never accepts, prints,
  stores, or embeds the password.
- **AC-NLP-02:** After rotation, both the Flyway migration Job and the serving
  API authenticate as the dedicated role; `NEON_DATABASE_USERNAME` is no longer
  `neondb_owner`.
- **AC-NLP-03:** `algorithm-learning-db-password` holds the rotated role's
  password, and no password, connection string with a password, or owner
  credential appears in source, a GitHub variable, or the workflow.
- **AC-NLP-04:** The migration Job completes exactly one successful migration and
  the served API reports ready at `/actuator/health/readiness` under the new
  credential; the elected `current_user` is the dedicated role.
- **AC-NLP-05:** The runbook documents the create/rotate procedure, the required
  ownership transfer and grants, the secret/variable update order, the readiness
  verification, and a rollback that restores the previous credential without
  reversing Flyway migrations.
- **AC-NLP-06:** No `GCP_SA_KEY`, service-account key, or long-lived credential is
  introduced; WIF, SHA-pinned actions, and serialized deploy remain unchanged.
- **AC-NLP-07:** The old owner password is not usable for serving after rotation
  (the `NEON_DATABASE_USERNAME`/secret pairing is replaced), and the change is
  recorded in `progress.md`.

## 6. Constraints and operational decisions

- The role password is a secret: the operator creates the role and enters the
  Secret Manager version; the agent must not receive, read, echo, or commit it.
- Neon role creation and grants run with the owner credential over the Neon SQL
  editor or `psql`; no Neon CLI is assumed to be installed locally.
- Because Flyway runs DDL, the application role must own the application schema
  and its existing objects. PostgreSQL `ALTER TABLE` requires ownership, so the
  runbook must transfer ownership of the existing `public` objects from
  `neondb_owner` to the application role (for example `ALTER SCHEMA public OWNER
  TO <app role>` plus `REASSIGN OWNED BY neondb_owner TO <app role>` or
  per-object `ALTER ... OWNER TO`); grants alone are insufficient.
- The role is confined to the `neondb` database and its application schema. It
  must not be a superuser or hold `CREATEDB`, `CREATEROLE`, `REPLICATION`, or
  `BYPASSRLS`, and must not own unrelated databases or objects.
- A serving credential without DDL is out of scope and requires the split-role
  design; `ALTER DEFAULT PRIVILEGES` wiring belongs to that future work.
- The pooled endpoint still uses PgBouncer transaction mode; only the serving
  API uses it, with JDBC prepared-statement caching disabled.
- Migration behavior stays forward-only; the routine rollback returns Cloud Run
  traffic and credentials, never reverses migrations.
- Deployment stays serialized: one migration Job, then the service deploy.

## 7. Assumptions to govern

- The operator has Neon owner access to create the role, grant privileges, and
  transfer object ownership, and can set a new Secret Manager version.
- The dedicated role name is operator-chosen and non-secret; the spec uses
  `<app role>` as a placeholder.
- The current `algorithm-learning-db-password` value is the `neondb_owner`
  password and will be replaced; the owner credential remains available to the
  operator but is no longer used by the deployed workloads.
- The existing application objects in `public` are owned by `neondb_owner`
  because prior Flyway runs used it; the operator transfers their ownership to
  the application role as part of the first rotation.
- First rotation creates the dedicated role; later rotations may simply reset its
  password and update the secret. The runbook covers both.
- No `plan.md`, `tasks.md`, or `WORK_GRAPH.yaml` node exists for this feature.

## 8. Governance record

- Intake mode selected: `quick-analysis` (bounded operational hardening; the
  request is already concrete).
- Documentation decision: `update-docs` (the operator artifact and runbook are
  the deliverable; no product or client code changes).
- Ambiguity decision: `infer`. The intake left the role topology open; governance
  rules for **one dedicated application role** and records the rationale below.
- Topology ruling: one dedicated application role that owns the application
  schema and existing objects, replacing the Neon owner. Rationale: it removes
  the actual risk recorded by `NEO-003` (a privileged Neon owner credential in
  the delivery path) with no config-contract expansion, and Flyway's DDL
  requirement is satisfied by ownership. The split into a DDL migration role and
  a DML-only runtime role is the stronger design but adds a second username,
  a second secret, bootstrap/workflow changes, and default-privilege wiring; it
  is deferred as the future hardening option that the residual serving DDL
  capability justifies.
- Non-goals preserved: no product/schema/API/client change, no pool/endpoint
  change, no secret handling by the agent.
- Operator boundary recorded: the operator performs the credential rotation and
  Secret Manager entry, matching `NEO-003`; the release task is operator-gated.
- Contradiction resolved: the intake's "grants only" language was tightened to
  "ownership transfer plus grants" because PostgreSQL `ALTER TABLE` requires
  object ownership for Flyway to evolve existing tables.
