# Neon Migration/Runtime Role Separation Specification

## Document status

- Feature ID: `neon-role-separation`
- Status: governed; revision 2026-09-21 requires a SQL-created runtime role
  (section 10)
- Requirement baseline: [`../../requirement.md`](../../requirement.md), sections 7
  and 8
- Relates to: the future hardening option recorded in
  [`../neon-least-privilege-role/spec.md`](../neon-least-privilege-role/spec.md)
  section 2 and section 8 ("Splitting into separate DDL migration and DML-only
  runtime roles"), and the `NLP-002` closeout in
  [`../../agent-workflow/progress.md`](../../agent-workflow/progress.md)
- Intake mode: `brainstorm`
- Updated: 2026-09-21
- Governance mode: `update-docs + ask-with-options` (revision, section 10)
- Execution contract: not selected; chosen per ready task after planning

This feature splits the single dedicated Neon application role into two roles so
the serving API loses DDL capability: one **migration (DDL) role** that owns the
application schema and runs Flyway, and one **runtime (DML-only) role** used by
the Cloud Run API service. It changes no product behavior, schema, API contract,
or client code.

## 1. Goal

Remove database DDL capability from the production serving credential. Today the
same role (`algorithm_learning_app`) owns the application schema and serves API
traffic, so a compromised or buggy serving process could alter or drop schema. The
feature creates a separate migration role for Flyway and confines the serving
role to `SELECT`/`INSERT`/`UPDATE`/`DELETE` on application objects, then rotates
the production delivery so the migration Job and the serving API authenticate as
different roles.

The feature is successful when a serialized production release migrates once as
the migration role, the API serves `/actuator/health/readiness` as the DML-only
runtime role, and the runtime role provably holds no object ownership and no DDL
privilege, with no secret value handled by the agent.

## 2. Current state (the gap being closed)

- `NLP-002` rotated production to one dedicated role, `algorithm_learning_app`,
  which is a member of the shared `table_owners` group and therefore inherits
  ownership of `public` and its objects so Flyway can run DDL.
- The migration Job and the serving API both set `DATABASE_USERNAME` to
  `NEON_DATABASE_USERNAME` and read one secret,
  `algorithm-learning-db-password`. The serving credential can therefore run
  `ALTER`/`DROP`/`CREATE`.
- The delivery already separates the direct endpoint (migration) from the pooled
  endpoint (service) and serializes migration-before-deploy, so the two workloads
  are already distinct execution paths; only their database identity is shared.

## 3. Proposed architecture (for governance)

```text
Neon (operator, out of band)
  ├─ group role table_owners (NOLOGIN) owns public + application objects
  ├─ algorithm_learning_migrate LOGIN, member of table_owners -> DDL for Flyway
  └─ algorithm_learning_app     LOGIN, NOT a member, DML only  -> serving API

GitHub Actions (main + protected production environment)
  └─ Cloud Run migration Job (direct endpoint, Flyway, algorithm_learning_migrate)
       └─ Cloud Run API service (pooled endpoint, Flyway disabled, algorithm_learning_app)
```

Design decisions (governed; rulings in section 9):

1. **Two login roles.** `algorithm_learning_migrate` owns/uses `table_owners` for
   DDL; `algorithm_learning_app` is confined to DML and is not a member of
   `table_owners`. Neither is `neondb_owner` nor a superuser, and neither holds
   `CREATEDB`/`CREATEROLE`/`REPLICATION`/`BYPASSRLS`. **Revision (section 10):**
   both roles must be created with SQL, because a role created through the Neon
   Console/CLI/API keeps an un-removable `neon_superuser` membership that defeats
   the runtime confinement.
2. **Role reuse.** `algorithm_learning_app` stays the **runtime** role (the
   serving identity and `algorithm-learning-db-password` remain stable), and a new
   `algorithm_learning_migrate` role owns the migration workload with a new secret.
3. **Config contract.** Add a second username and a second secret for the
   migration workload while the runtime keeps the existing pair, so the serving
   revision diff stays minimal. Concrete names are in section 5.
4. **Default privileges.** Future Flyway-created objects must be automatically
   DML-usable by the runtime role; the artifact sets `ALTER DEFAULT PRIVILEGES`
   for the role that creates them, so schema evolution never silently locks out
   the runtime role. The mechanism and the connection identity that runs it are
   governed in section 9.
5. **One runtime service account.** Keep the single `algorithm-learning-runtime`
   account for both workloads and grant it Secret Accessor on the new migration
   secret; a dedicated migration service account is recorded as a further
   hardening option, not part of this feature.

## 4. Scope

### In scope

- A password-free, committed operator artifact (SQL) that:
  - creates or reconciles the two dedicated roles and their confinement;
  - keeps `table_owners` as the schema/object owner, with the migration role as
    its member for DDL and the runtime role not a member;
  - grants the runtime role only `SELECT`/`INSERT`/`UPDATE`/`DELETE` on
    application tables (excluding `flyway_schema_history`) and `USAGE`/`SELECT`
    on sequences, with `CREATE` on the schema revoked;
  - installs default privileges so Flyway-created objects remain DML-usable by
    the runtime role;
  - never accepts, prints, stores, or embeds a password.
- A committed update to `infra/gcp/bootstrap.sh` to create the new Secret Manager
  container and grant the runtime account Secret Accessor on it.
- Workflow-config changes in `.github/workflows/gcp-production-deploy.yml`: the
  migration Job reads the migration username and secret; the service keeps the
  runtime username and secret.
- Runbook updates in `infra/gcp/README.md` for the split, the ownership and
  grant/default-privilege wiring, the secret/variable update order, redeploy,
  verification, and rollback.
- An operator-gated release task that performs the split and proves readiness and
  DDL-denial under the runtime role.

### Out of scope

- Product, schema, migration, API-contract, client, or Compose changes.
- Row-level security, per-column grants, read-only replicas, or connection-pool
  changes.
- Changing the pooled/direct endpoint split, `prepareThreshold=0`, TLS settings,
  WIF, Artifact Registry, or Cloud Run topology beyond the two role identities and
  the secret/username wiring above.
- Deleting the Neon project, branch, database, `neondb_owner`, or `table_owners`,
  or removing `neondb_owner` as the operator/break-glass credential.
- A separate migration service account or a second WIF trust boundary (recorded
  as a future hardening option).
- Reading, printing, storing, or committing any secret value.

## 5. Required configuration contract

### 5.1 GitHub Environment (`production`) variables

| Variable | Change |
|---|---|
| `NEON_DATABASE_USERNAME` | Runtime (DML-only) role name |
| `NEON_MIGRATION_DATABASE_USERNAME` | **New**: migration (DDL) role name |
| `NEON_MIGRATION_JDBC_URL` | Unchanged (password-free direct endpoint) |
| `NEON_SERVICE_JDBC_URL` | Unchanged (password-free pooled endpoint) |
| other `GCP_*` variables | Unchanged; `GCP_CORS_ALLOWED_ORIGINS` still absent |

### 5.2 GCP Secret Manager secrets

| Secret Manager ID | Change |
|---|---|
| `algorithm-learning-db-password` | Runtime (DML-only) role password |
| `algorithm-learning-db-migration-password` | **New**: migration (DDL) role password |
| `algorithm-learning-jwt-key`, `algorithm-learning-refresh-hash-key` | Unchanged |

The runtime service account gains Secret Accessor on the new migration secret
(one shared runtime identity). No password, connection string with a password,
or owner credential appears in source, a GitHub variable, or the workflow.

## 6. Acceptance criteria

- **AC-NRS-01:** A committed, password-free operator artifact creates or
  reconciles two dedicated Neon login roles confined to the `neondb` database and
  its application schema, neither `neondb_owner` nor superuser and neither with
  `CREATEDB`/`CREATEROLE`/`REPLICATION`/`BYPASSRLS`; the migration role is the
  only one that inherits schema/object ownership through `table_owners`.
- **AC-NRS-02:** The runtime role holds no object ownership and no DDL privilege:
  `CREATE` on schema `public` is revoked (it keeps `USAGE`), and the artifact
  grants it only `SELECT`/`INSERT`/`UPDATE`/`DELETE` on application tables plus
  `USAGE`/`SELECT` on sequences; it has no access to `flyway_schema_history`.
- **AC-NRS-03:** `ALTER DEFAULT PRIVILEGES` is installed for the role that creates
  future objects (the migration role, and `table_owners` as a fallback) so a table
  or sequence created by a future Flyway migration is automatically DML-usable by
  the runtime role without a manual re-grant; the statements and the connection
  identity that runs them follow section 9, decision 2.
- **AC-NRS-04:** The migration Job authenticates as the migration role and still
  applies migrations (the applied `V2` remains); the serving API authenticates as
  the runtime role. Exactly one migration runs before the service deploy.
- **AC-NRS-05:** After the split deploy, `/actuator/health/readiness` reports
  ready; the served `DATABASE_USERNAME` is the runtime role; and a runtime-role
  probe proves DDL denial (for example `CREATE TABLE`/`ALTER TABLE` rejected)
  while normal reads and writes succeed.
- **AC-NRS-06:** The runbook documents the split creation, ownership and
  default-privilege wiring, the secret/variable update order, the redeploy, the
  verification (including the DDL-denial probe), and a rollback that restores the
  previous credential/revision and never reverses Flyway migrations.
- **AC-NRS-07:** `algorithm-learning-db-migration-password` holds the migration
  role's password and no secret value, password-bearing JDBC URL, or owner
  credential appears in source, a GitHub variable, or the workflow.
- **AC-NRS-08:** No `GCP_SA_KEY`, service-account key, or long-lived credential is
  introduced; WIF, SHA-pinned actions, serialized deploy, the pooled/direct
  endpoint split, `prepareThreshold=0`, and forward-only migrations are unchanged.

## 7. Constraints and operational decisions

- Neon roles created through the Console/CLI/API are not Postgres superusers, and
  `neondb_owner` has no admin option on them; ownership must flow through the
  shared `table_owners` group role established by `NLP-001`/`SR-004`. Grants
  alone are insufficient for Flyway DDL, and membership alone is insufficient for
  the runtime role because it must not hold ownership.
- PostgreSQL ownership is not grantable as a table privilege; the runtime role
  must never be a member of `table_owners`, and the migration role must be.
- `ALTER DEFAULT PRIVILEGES` applies to objects created by a specific role, so
  the artifact must target the role that actually creates future objects (the
  migration role, and/or `table_owners`); the exact statements and the connection
  identity that runs them are governed in section 9, decision 2.
- The role passwords are secrets: the operator creates/resets the roles and enters
  the Secret Manager versions; the agent must not receive, read, echo, or commit
  them.
- Migration behavior stays forward-only; the routine rollback returns Cloud Run
  traffic and credentials and never reverses migrations.
- Deployment stays serialized: one migration Job, then the service deploy.
- The change is credential/config-only; the deployed image content is unaffected,
  so a rollback can be revision- or secret-only.
- **Revision (section 10):** a role created through the Neon Console/CLI/API is
  permanently a member of `neon_superuser` (`CREATEDB`, `CREATEROLE`, `BYPASSRLS`,
  `REPLICATION`, and broad table/sequence grants), and this membership cannot be
  revoked, so the DML-only runtime role must be created with SQL.

## 8. Governed assumptions

- The operator has Neon owner access to create/reset roles, manage `table_owners`
  membership, grant DML, set default privileges, and set new Secret Manager
  versions.
- The current production role is `algorithm_learning_app` (member of
  `table_owners`); the split reuses it as the runtime (DML-only) role and adds
  `algorithm_learning_migrate` (section 9, decision 1).
- Existing application objects are owned by `table_owners`; the runtime role
  currently has effective ownership and DDL through membership, which the split
  removes by revoking membership and relying on explicit DML grants.
- The operator's `neondb_owner` break-glass credential remains available and is
  not used by the deployed workloads.
- A single runtime service account remains acceptable for this feature; a
  dedicated migration identity is deferred.
- No `plan.md`, `tasks.md`, or `WORK_GRAPH.yaml` node exists for this feature;
  `$work-graph` creates them after governance.

## 9. Governance record

- Documentation decision: `update-docs`. The deliverables are a committed SQL
  artifact, `infra/gcp/bootstrap.sh`, the production workflow, and
  `infra/gcp/README.md`; no product or client code changes.
- Ambiguity decision: `infer`. All five intake decisions resolve from the recorded
  constraints and prior `NLP`/`SR` evidence; none requires a product-owner choice.
- Decision 1 — role names. Keep `algorithm_learning_app` as the **runtime
  (DML-only)** role and create `algorithm_learning_migrate` as the **migration
  (DDL)** role. Rationale: the serving identity, `NEON_DATABASE_USERNAME`, and
  `algorithm-learning-db-password` stay stable, so the deployed service revision
  diff is minimal and the recorded revision/credential-only rollback stays valid;
  only the migration workload takes the new `NEON_MIGRATION_DATABASE_USERNAME`
  variable and `algorithm-learning-db-migration-password` secret.
- Decision 2 — default-privilege mechanism. Future objects are created by the
  migration role (a member's `current_user` is itself), so the artifact installs
  `ALTER DEFAULT PRIVILEGES FOR ROLE algorithm_learning_migrate IN SCHEMA public
  GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO algorithm_learning_app` and
  the matching `GRANT USAGE, SELECT ON SEQUENCES TO algorithm_learning_app`.
  PostgreSQL requires membership in the role the defaults are `FOR`, and Neon
  roles created from the Console give `neondb_owner` no admin option, so the
  runbook runs these statements while connected as the migration role, or as
  `neondb_owner` when it administers that role. `ALTER DEFAULT PRIVILEGES FOR ROLE
  table_owners` is also set as a fallback for objects that remain owned by the
  group. The artifact contains no password.
- Decision 3 — object owner. `table_owners` remains the owner of `public` and the
  existing objects, with the migration role as its member. Transferring ownership
  directly to the migration role is not possible under Neon's constraints:
  `REASSIGN OWNED` and `ALTER ... OWNER TO` require membership in the new owner,
  and `neondb_owner` has no admin option on Console-created roles.
- Decision 4 — service identity. Keep the single `algorithm-learning-runtime`
  account for both workloads and grant it Secret Accessor on
  `algorithm-learning-db-migration-password` in `infra/gcp/bootstrap.sh`; a
  dedicated migration service account is deferred as future hardening.
- Decision 5 — DDL-denial evidence. The artifact task's verification asserts
  statically that the SQL contains the confinement, `REVOKE CREATE`, the DML
  grants, and no password, and that the workflow binds the migration
  username/secret to the Job and the runtime username/secret to the service. The
  runbook adds an operator probe that connects as the runtime role and proves
  `CREATE`/`ALTER`/`DROP` are denied while `SELECT`/`INSERT`/`UPDATE`/`DELETE`
  succeed; the release task records the operator's attestation in `progress.md`,
  matching the `NLP-002` agent boundary.
- Refinements: the runtime role keeps `USAGE` on schema `public` without `CREATE`,
  and the artifact excludes `flyway_schema_history` from the runtime grants.
- Non-goals are preserved and `AC-NRS-01..08` are the executable contract for
  `$work-graph`. `plan.md`, `tasks.md`, and the `WORK_GRAPH.yaml` node do not exist
  yet; `$work-graph` creates them next.

## 10. Revision 2026-09-21 — runtime role must be SQL-created

### Trigger

The `NRS-003` split released the two roles and moved the serving API to
`algorithm_learning_app`, but the runtime-role probe showed the role still had
broad capability. Production diagnostics established:

- `algorithm_learning_app` is a member of `neon_superuser` (`inherit=t`,
  `set=t`) because it was created through the Neon Console in `NLP-001`.
- Neon automatically grants `neon_superuser` to every Console/CLI/API role and
  cannot revoke it: `neondb_owner` has no admin option on it, and Neon documents
  the membership as not user-modifiable.
- `neon_superuser` carries `CREATEDB`, `CREATEROLE`, `REPLICATION`, `BYPASSRLS`,
  and broad table/sequence grants in `public`. The runtime role could therefore
  `SET ROLE neon_superuser` and held table privileges beyond
  `SELECT`/`INSERT`/`UPDATE`/`DELETE`, including `flyway_schema_history`.

This invalidates the assumption in section 3 decision 1 that the existing Console
role can be confined by revoking `table_owners` and `CREATE`. The delivered
workflow and secret contract are unaffected; only the runtime role's Neon
identity changes.

### Ruling

The runtime (DML-only) role **must be created with SQL** (`CREATE ROLE`), never
through the Neon Console/CLI/API, so it never receives `neon_superuser`. The
migration role already satisfies this because `NRS-001` creates it with SQL.

### New acceptance criterion

- **AC-NRS-09:** The runtime role is a SQL-created login role that is not a
  member of `neon_superuser` (or any other Neon control-plane role), directly
  holds none of `CREATEDB`/`CREATEROLE`/`REPLICATION`/`BYPASSRLS` and does not
  inherit them, and a live probe as that role denies `CREATE TABLE`/`ALTER TABLE`
  while normal reads and writes succeed.

### Material decision (ask-with-options)

How to replace the Console-created runtime role:

1. **Reuse the name `algorithm_learning_app`.** Delete it in the Neon Console,
   create it again with SQL with the same confinement and DML grants, rotate the
   `algorithm-learning-db-password` value, and redeploy. `NEON_DATABASE_USERNAME`
   and the secret/artifact wiring stay unchanged; there is a brief serving outage
   between the delete and the redeploy (the running revision loses its login).
2. **New SQL-created name (for example `algorithm_learning_runtime`).** Create it
   with SQL, grant DML, add a new `algorithm-learning-db-password` version, set
   `NEON_DATABASE_USERNAME` to the new name, redeploy, then delete or invalidate
   the Console role. No serving outage; the runtime username changes.

Option 1 keeps the governed names and is recommended for a low-traffic service;
option 2 avoids downtime at the cost of a config/name change. Keeping the
Console role as the runtime is not viable (AC-NRS-09 cannot be met).

**Decision (2026-09-21):** option 1 — reuse `algorithm_learning_app`. The operator
deletes the Console-created role, `NRS-001`'s artifact recreates it with SQL, the
runtime secret is rotated, and the release is redeployed.
