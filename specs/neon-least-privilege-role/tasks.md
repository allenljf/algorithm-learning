# Neon Least-Privilege Role — Task List

All task IDs, dependencies, statuses, and exact verification commands are
authoritative in `agent-workflow/WORK_GRAPH.yaml`. This document is the
human-readable execution contract.

## Phase 1 — Role tooling

### NLP-001 — Add the password-free Neon role ownership/rotation artifact and runbook

- Deliverable: a committed, password-free `infra/gcp/neon-least-privilege-role.sql`
  template that creates/rotates the dedicated application role, transfers
  ownership of the application schema and its existing objects from
  `neondb_owner`, and confines the role (no
  `SUPERUSER`/`CREATEDB`/`CREATEROLE`/`REPLICATION`/`BYPASSRLS`); plus an
  `infra/gcp/README.md` rotation runbook covering create/rotate, ownership and
  grants, secret/variable update order, redeploy, verification, and rollback.
  No workflow or application code change is expected because the delivery reads
  `NEON_DATABASE_USERNAME` and the secret by name.
- Depends on: none
- Parallel group: `neon-role-tooling`
- Spec refs: 2, 3, 4, 5 AC-NLP-01/03/05/06, 6, 7
- Execution contract: `three-perspectives` analysis; `test-candidates` test
  approach; `update-docs` documentation; `infer` ambiguity handling.
- Verification: `test -f infra/gcp/neon-least-privilege-role.sql`;
  `python3 -c "from pathlib import Path; t = Path('infra/gcp/neon-least-privilege-role.sql').read_text().lower(); assert 'reassign owned' in t and 'nosuperuser' in t and 'nocreatedb' in t and 'nocreaterole' in t and 'noreplication' in t and ('password' + ' ') not in t and ('password' + '=') not in t"`;
  `git diff --check`
- Status: completed

## Phase 2 — Credential rotation

### NLP-002 — Rotate the production Neon credential and verify readiness

- Deliverable: the operator-gated rotation. The operator creates the dedicated
  role and transfers ownership per `NLP-001`, sets the new
  `algorithm-learning-db-password` Secret Manager version, and updates the
  GitHub `production` `NEON_DATABASE_USERNAME`; the serialized workflow then
  migrates once and redeploys the API, which must report ready under the new
  credential (`DATABASE_USERNAME` is no longer `neondb_owner`).
- Depends on: NLP-001
- Parallel group: `neon-role-release`
- Spec refs: 1, 3, 4, 5 AC-NLP-02/04/07, 6
- Execution contract: `three-perspectives` analysis; `test-candidates` test
  approach; `update-docs` documentation; `infer` ambiguity handling.
- Verification: `gcloud run jobs execute algorithm-learning-migrate --region=asia-east1 --wait`;
  `gcloud run services describe algorithm-learning-api --region=asia-east1 --format='value(status.url)'`;
  `curl --fail --retry 12 --retry-delay 5 "$(gcloud run services describe algorithm-learning-api --region=asia-east1 --format='value(status.url)')/actuator/health/readiness"`;
  `python3 -c "import subprocess; out = subprocess.run(['gcloud','run','services','describe','algorithm-learning-api','--region=asia-east1','--format=value(spec.template.spec.containers[0].env)'], capture_output=True, text=True).stdout; assert 'neondb_owner' not in out"`;
  `git diff --check`
- Status: ready
