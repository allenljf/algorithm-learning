# Neon Cloud Run Delivery — Task List

All task IDs, dependencies, statuses, and exact verification commands are
authoritative in `agent-workflow/WORK_GRAPH.yaml`. This document is the
human-readable execution contract.

## Phase 1 — Neon delivery foundations

### NEO-001 — Replace Cloud SQL bootstrap with a Neon-compatible GCP bootstrap and runbook

- Deliverable: `infra/gcp/bootstrap.sh` provisions only APIs, Artifact Registry,
  Secret Manager containers, the runtime/deploy service accounts, WIF, and IAM;
  it no longer creates or plans a VPC, subnet, private-service-access range, or
  Cloud SQL instance, and no longer grants `roles/cloudsql.client`. Update
  `infra/gcp/README.md` to describe Neon connection variables, Secret Manager
  values, the first-deploy checklist, readiness, and revision rollback without
  any Cloud SQL/VPC instruction.
- Depends on: none
- Parallel group: `neon-delivery-foundations`
- Spec refs: 2, 3, 4.1-4.2, 5 AC-NEO-01/02/05/07, 6-8
- Execution contract: `three-perspectives` analysis; `test-candidates` test
  approach; `update-docs` documentation; `infer` ambiguity handling.
- Verification: `bash -n infra/gcp/bootstrap.sh`; `bash infra/gcp/bootstrap.sh --help`; `python3 -c "from pathlib import Path; t = Path('infra/gcp/bootstrap.sh').read_text().lower(); assert 'cloudsql' not in t and 'sqladmin' not in t and 'private-services' not in t"`; `git diff --check`
- Status: completed

### NEO-002 — Retarget the production workflow from Cloud SQL to Neon

- Deliverable: `.github/workflows/gcp-production-deploy.yml` keeps SHA-pinned
  actions, serialized OIDC deployment, and the immutable image, but builds the
  migration Job from `vars.NEON_MIGRATION_JDBC_URL` + `vars.NEON_DATABASE_USERNAME`
  and the service from `vars.NEON_SERVICE_JDBC_URL` + `vars.NEON_DATABASE_USERNAME`,
  removes `--add-cloudsql-instances`/`--network`/`--subnet`/`--vpc-egress`, and
  applies `prepareThreshold=0` to the pooled serving URL. Remove the now-unused
  `com.google.cloud.sql:postgres-socket-factory` dependency from
  `services/api/pom.xml`.
- Depends on: NEO-001, GCP-002
- Parallel group: `neon-delivery-foundations`
- Spec refs: 2, 3, 4.1-4.2, 5 AC-NEO-02/03/04/06, 6-8
- Execution contract: `three-perspectives` analysis; `test-candidates` test
  approach; `update-docs` documentation; `infer` ambiguity handling.
- Verification: `python3 -c "from pathlib import Path; t = Path('.github/workflows/gcp-production-deploy.yml').read_text(); assert 'id-token: write' in t and 'GCP_SA_KEY' not in t and 'concurrency:' in t and 'SPRING_FLYWAY_ENABLED=false' in t and 'NEON_MIGRATION_JDBC_URL' in t and 'NEON_SERVICE_JDBC_URL' in t and 'cloudSqlInstance' not in t and 'add-cloudsql-instances' not in t and 'prepareThreshold=0' in t"`; `python3 -c "from pathlib import Path; t = Path('services/api/pom.xml').read_text(); assert 'postgres-socket-factory' not in t"`; `git diff --check`
- Status: completed

## Phase 2 — First Neon release

### NEO-003 — Provision and verify the first Neon-backed GCP production release

- Deliverable: GCP resources created in `alert-study-508214-s5`, GitHub
  `production` Environment variables already set by the wizard, Secret Manager
  values entered by the user outside source control, a successful serialized
  migration Job against the Neon direct endpoint, a deployed API service on the
  pooled endpoint, and recorded health/rollback evidence.
- Depends on: NEO-002
- Parallel group: `neon-production-release`
- Spec refs: 2, 4, 5 AC-NEO-01..07, 6-8
- Verification: `bash infra/gcp/bootstrap.sh --apply`; `gcloud run jobs execute algorithm-learning-migrate --region=asia-east1 --wait`; `gcloud run services describe algorithm-learning-api --region=asia-east1 --format='value(status.url)'`; `curl --fail --retry 12 --retry-delay 5 "$(gcloud run services describe algorithm-learning-api --region=asia-east1 --format='value(status.url)')/actuator/health/readiness"`
- Status: completed
