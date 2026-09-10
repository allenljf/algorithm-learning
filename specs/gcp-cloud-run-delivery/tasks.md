# GCP Cloud Run Delivery — Task List

All task IDs, dependencies, statuses, and exact verification commands are
authoritative in `agent-workflow/WORK_GRAPH.yaml`. This document is the
human-readable execution contract.

## Phase 1 — Delivery foundations

### GCP-001 — Add idempotent GCP bootstrap tooling and secret-safe runbook

- Deliverable: `infra/gcp/bootstrap.sh` with explicit `--plan`/`--apply`
  modes; resource and IAM definitions for APIs, private networking, Cloud SQL,
  Artifact Registry, Secret Manager containers, service accounts, and GitHub
  WIF; plus an operator document that lists manual secret/environment setup.
- Depends on: none
- Parallel group: `delivery-foundations`
- Spec refs: 2, 3, 4.1-4.2, 5 AC-GCP-01..02/04/06..07, 6-8
- Execution contract: `three-perspectives` analysis; `test-candidates` test
  approach; `update-docs` documentation; `infer` ambiguity handling.
- Verification: `bash -n infra/gcp/bootstrap.sh`; `bash infra/gcp/bootstrap.sh --help`; `git diff --check`
- Status: completed

### GCP-002 — Make the API image complete in migration-only Cloud Run Job mode

- Deliverable: a tested Spring Boot migration-only startup mode that runs
  Flyway against the configured datasource, exits successfully without opening
  an HTTP listener, and leaves normal service startup unchanged. Document the
  mode's Cloud Run environment contract.
- Depends on: none
- Parallel group: `delivery-foundations`
- Spec refs: 2, 3, 5 AC-GCP-03/05/07, 6-8
- Execution contract: `three-perspectives` analysis; `tdd` test approach;
  `update-docs` documentation; `infer` ambiguity handling.
- Verification: `cd services/api && ./mvnw -q test -Dtest='*MigrationModeTest'`; `cd services/api && ./mvnw -q test`
- Status: completed

## Phase 2 — Continuous delivery

### GCP-003 — Add the OIDC Cloud Run migration and deployment workflow

- Deliverable: a SHA-pinned GitHub Actions production workflow with minimal
  permissions, concurrency protection, WIF authentication, API verification,
  immutable Artifact Registry image tagging, migration Job execution/wait,
  Flyway-disabled Cloud Run deployment, readiness verification, and a rollout/
  rollback operator runbook.
- Depends on: GCP-001, GCP-002
- Parallel group: `continuous-delivery`
- Spec refs: 2, 3, 4.1-4.2, 5 AC-GCP-01..07, 6-8
- Execution contract: `three-perspectives` analysis; `test-candidates` test
  approach; `update-docs` documentation; `infer` ambiguity handling.
- Verification: `python3 -c "from pathlib import Path; text = Path('.github/workflows/gcp-production-deploy.yml').read_text(); assert 'id-token: write' in text and 'GCP_SA_KEY' not in text and 'concurrency:' in text and 'SPRING_FLYWAY_ENABLED=false' in text"`; `git diff --check`
- Status: completed

## Phase 3 — First controlled release

### GCP-004 — Provision and verify the first GCP production release

- Deliverable: GCP resources created in `alert-study-508214-s5`, GitHub
  `production` Environment variables configured, Secret Manager values entered
  by the user outside source control, a successful serialized migration Job,
  a deployed API service, and recorded health/rollback evidence.
- Depends on: GCP-003
- Parallel group: `production-release`
- Spec refs: 2, 4, 5 AC-GCP-01..07, 6-8
- Verification: `bash infra/gcp/bootstrap.sh --apply`; `gcloud run jobs execute algorithm-learning-migrate --region=asia-east1 --wait`; `gcloud run services describe algorithm-learning-api --region=asia-east1 --format='value(status.url)'`; `curl --fail --retry 12 --retry-delay 5 "$(gcloud run services describe algorithm-learning-api --region=asia-east1 --format='value(status.url)')/actuator/health/readiness"`
- Status: pending
