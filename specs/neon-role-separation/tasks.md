# Neon Migration/Runtime Role Separation — Task List

All task IDs, dependencies, statuses, and exact verification commands are
authoritative in `agent-workflow/WORK_GRAPH.yaml`. This document is the
human-readable execution contract.

## Phase 1 — Split tooling

### NRS-001 — Add the password-free split-role SQL artifact and runbook

- Deliverable: a committed, password-free `infra/gcp/neon-role-separation.sql`
  template that reconciles the two dedicated roles — `algorithm_learning_app` as
  the DML-only runtime role (not a member of `table_owners`, `USAGE` but not
  `CREATE` on `public`, `SELECT`/`INSERT`/`UPDATE`/`DELETE` on application tables
  excluding `flyway_schema_history`, `USAGE`/`SELECT` on sequences, confined with
  no `SUPERUSER`/`CREATEDB`/`CREATEROLE`/`REPLICATION`/`BYPASSRLS`) and
  `algorithm_learning_migrate` as the DDL migration role (member of
  `table_owners`); plus `ALTER DEFAULT PRIVILEGES` for the object-creating role
  (and `table_owners` as a fallback); plus an `infra/gcp/README.md` split runbook
  covering create/rotate, membership, grants/default privileges, the connection
  identity that runs the default-privilege statements, the secret/variable update
  order, redeploy, the DDL-denial probe, and rollback.
- Depends on: none
- Parallel group: `neon-split-foundations`
- Spec refs: 3, 4, 5, 6 AC-NRS-01/02/03/06/07, 7, 8, 9 decisions 1-3/5
- Execution contract: `three-perspectives` analysis; `test-candidates` test
  approach; `update-docs` documentation; `infer` ambiguity handling.
- Verification: `test -f infra/gcp/neon-role-separation.sql`;
  `python3 -c "from pathlib import Path; t = Path('infra/gcp/neon-role-separation.sql').read_text().lower(); assert 'algorithm_learning_app' in t and 'algorithm_learning_migrate' in t and 'table_owners' in t and 'revoke' in t and 'create' in t and 'grant select' in t and 'alter default privileges' in t and 'nosuperuser' in t and ('password' + ' ') not in t and ('password' + '=') not in t"`;
  `git diff --check`
- Status: completed

### NRS-002 — Wire the split identity into bootstrap and the production workflow

- Deliverable: `infra/gcp/bootstrap.sh` creates the new
  `algorithm-learning-db-migration-password` Secret Manager container and grants
  the runtime service account Secret Accessor on it, and
  `.github/workflows/gcp-production-deploy.yml` binds the migration Job to
  `NEON_MIGRATION_DATABASE_USERNAME` +
  `algorithm-learning-db-migration-password` while the service keeps
  `NEON_DATABASE_USERNAME` + `algorithm-learning-db-password`; SHA-pinned
  actions, OIDC, serialized concurrency, the pooled/direct endpoint split, and
  `prepareThreshold=0` are unchanged.
- Depends on: none
- Parallel group: `neon-split-foundations`
- Spec refs: 3, 4, 5, 6 AC-NRS-04/07/08, 7, 9 decisions 1/4
- Execution contract: `three-perspectives` analysis; `test-candidates` test
  approach; `update-docs` documentation; `infer` ambiguity handling.
- Verification: `bash -n infra/gcp/bootstrap.sh`;
  `bash infra/gcp/bootstrap.sh --help`;
  `python3 -c "from pathlib import Path; t = Path('infra/gcp/bootstrap.sh').read_text(); assert 'algorithm-learning-db-migration-password' in t"`;
  `python3 -c "from pathlib import Path; t = Path('.github/workflows/gcp-production-deploy.yml').read_text(); assert 'id-token: write' in t and 'GCP_SA_KEY' not in t and 'concurrency:' in t and 'NEON_MIGRATION_DATABASE_USERNAME' in t and 'algorithm-learning-db-migration-password' in t and 'NEON_DATABASE_USERNAME' in t and 'algorithm-learning-db-password' in t"`;
  `git diff --check`
- Status: ready

## Phase 2 — Split release

### NRS-003 — Apply the split roles and verify readiness with DDL denial

- Deliverable: the operator-gated split release. The operator creates/resets
  `algorithm_learning_migrate` and reconciles `algorithm_learning_app` per
  `NRS-001`, transfers/verifies membership and default privileges, sets the
  `algorithm-learning-db-migration-password` and `algorithm-learning-db-password`
  Secret Manager versions, and updates the GitHub `production`
  `NEON_MIGRATION_DATABASE_USERNAME` and `NEON_DATABASE_USERNAME`; the serialized
  workflow then migrates once as the migration role and redeploys the API, which
  must report ready as the runtime role with the operator attesting that DDL is
  denied and DML succeeds for the runtime role. The agent never handles a
  password.
- Depends on: NRS-001, NRS-002
- Parallel group: `neon-split-release`
- Spec refs: 1, 6 AC-NRS-04/05/06/07, 7, 9 decisions 1/4/5
- Execution contract: `three-perspectives` analysis; `test-candidates` test
  approach; `update-docs` documentation; `infer` ambiguity handling.
- Verification: `gcloud run jobs describe algorithm-learning-migrate --region=asia-east1 --format='value(spec.template.spec.containers[0].env)'`;
  `gcloud run jobs execute algorithm-learning-migrate --region=asia-east1 --wait`;
  `gcloud run services describe algorithm-learning-api --region=asia-east1 --format='value(status.url)'`;
  `curl --fail --retry 12 --retry-delay 5 "$(gcloud run services describe algorithm-learning-api --region=asia-east1 --format='value(status.url)')/actuator/health/readiness"`;
  `python3 -c "import subprocess; svc = subprocess.run(['gcloud','run','services','describe','algorithm-learning-api','--region=asia-east1','--format=value(spec.template.spec.containers[0].env)'], capture_output=True, text=True).stdout; job = subprocess.run(['gcloud','run','jobs','describe','algorithm-learning-migrate','--region=asia-east1','--format=value(spec.template.spec.template.spec.containers[0].env)'], capture_output=True, text=True).stdout; assert 'algorithm_learning_app' in svc and 'algorithm_learning_migrate' not in svc and 'algorithm_learning_migrate' in job"`;
  `git diff --check`
- Status: pending
