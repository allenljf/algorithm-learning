# Neon Cloud Run Delivery Specification

## Document status

- Feature ID: `neon-cloud-run-delivery`
- Status: governed; ready for `$work-graph`
- Requirement baseline: [`../../requirement.md`](../../requirement.md), sections 2, 7, and 8
- Supersedes: the managed-database decision in `specs/gcp-cloud-run-delivery/spec.md`
  (Cloud SQL). That feature's OIDC/WIF, Artifact Registry, Secret Manager, and
  migration-Job design remain valid; only the database provider and network path change.
- Intake mode: `quick-analysis`
- Updated: 2026-09-19
- Governance mode: `update-docs + infer`
- Execution contract: not selected; chosen per ready task after planning

This feature keeps the existing GCP Cloud Run delivery but moves durable data
from Cloud SQL to Neon Serverless Postgres. It does not authorize product
behavior changes, schema changes, or Flutter/Compose UI changes.

## 1. Goal

Deploy the existing Spring Boot API to Cloud Run from GitHub Actions without
long-lived GCP credentials, running against a Neon Postgres database reachable
over TLS. Images go to Artifact Registry; runtime secrets stay in GCP Secret
Manager; the workflow authenticates through GitHub OIDC/WIF exactly as before.

Target project: `alert-study-508214-s5` (project number `730295148186`),
region `asia-east1`. Neon project `young-dream-38602077`, branch
`br-sweet-fog-b3g6o1mk`, database `neondb`, region `ap-southeast-1`.

## 2. Chosen architecture

```text
GitHub Actions (allenljf/algorithm-learning, main + protected production environment)
  └─ GitHub OIDC short-lived token → GCP WIF provider → deploy service account
       ├─ Artifact Registry: build/push API image
       └─ Cloud Run migration Job (algorithm-learning-migrate, direct Neon endpoint)
            └─ Cloud Run API service (Flyway disabled, pooled Neon endpoint)
                 ├─ GCP Secret Manager: APP_JWT_KEY, APP_REFRESH_HASH_KEY, DATABASE_PASSWORD
                 └─ Neon Serverless Postgres (public TLS; no VPC, no Cloud SQL)
```

- Cloud Run service `algorithm-learning-api`; migration Job
  `algorithm-learning-migrate`; Artifact Registry repository `algorithm-learning`.
- The API service uses the **pooled** Neon endpoint; Flyway runs only in the
  migration Job against the **direct** Neon endpoint (PgBouncer transaction mode
  does not support the session features migrations rely on).
- No custom VPC, subnet, private-service-access range, Cloud SQL instance, or
  Cloud SQL Java Socket Factory. Cloud Run reaches Neon over its default egress.
- The WIF condition, service accounts, IAM roles for Secret Manager/Artifact
  Registry/Cloud Run, and the serialized one-migration-then-deploy workflow shape
  are unchanged from `gcp-cloud-run-delivery`.

## 3. Scope

### In scope

- A Neon-compatible `infra/gcp/bootstrap.sh` that provisions the GCP foundation
  (APIs, Artifact Registry, Secret Manager containers, runtime/deploy service
  accounts, WIF) and no longer provisions Cloud SQL or private networking, plus a
  matching operator runbook.
- A production workflow that consumes Neon connection details from GitHub
  Environment variables and deploys migration Job + service accordingly.
- Explicit handling of the transaction-pooling constraint for the pooled
  endpoint.
- Least-privilege IAM: the runtime account no longer needs `roles/cloudsql.client`.

### Out of scope

- Cloud SQL/VPC teardown: the empty project has no Cloud SQL resources; if any
  existed, deleting them is a separate operator action.
- Neon role/password management, Neon project provisioning, or Neon branching.
- Long-lived GCP keys, secret values in GitHub, GKE, custom domains, Flutter
  hosting, product behavior, and schema changes.
- The Compose Multiplatform frontend migration (`compose-multiplatform-migration`).

## 4. Required configuration contract

### 4.1 GitHub Environment (production) non-secret variables

| Variable | Value |
|---|---|
| `GCP_PROJECT_ID` | `alert-study-508214-s5` |
| `GCP_REGION` | `asia-east1` |
| `GCP_WIF_PROVIDER` | `projects/730295148186/locations/global/workloadIdentityPools/github-actions/providers/github-provider` |
| `GCP_DEPLOY_SERVICE_ACCOUNT` | `algorithm-learning-deployer@alert-study-508214-s5.iam.gserviceaccount.com` |
| `GCP_ARTIFACT_REPOSITORY` | `algorithm-learning` |
| `GCP_CLOUD_RUN_SERVICE` | `algorithm-learning-api` |
| `NEON_MIGRATION_JDBC_URL` | direct endpoint, password-free JDBC URL |
| `NEON_SERVICE_JDBC_URL` | pooled endpoint, password-free JDBC URL |
| `NEON_DATABASE_USERNAME` | Neon application role name |
| `GCP_CORS_ALLOWED_ORIGINS` | intentionally **absent** (GitHub rejects an empty value; the workflow treats it as empty) |

The non-secret origin of these values is the ignored `infra/env/.env.neon` file
written by `scripts/neon-cloud-run-setup-wizard.sh`. JDBC URLs use
`sslmode=require&channelBinding=require` and never contain a password.

### 4.2 GCP Secret Manager runtime secrets

| Secret Manager ID | Runtime variable | Value |
|---|---|---|
| `algorithm-learning-jwt-key` | `APP_JWT_KEY` | random ≥ 32 chars |
| `algorithm-learning-refresh-hash-key` | `APP_REFRESH_HASH_KEY` | distinct random ≥ 32 chars |
| `algorithm-learning-db-password` | `DATABASE_PASSWORD` | Neon application role password |

`DATABASE_URL` is the migration or service JDBC URL per workload;
`DATABASE_USERNAME`, `APP_CORS_ALLOWED_ORIGINS`, `APP_JWT_ISSUER`, and
`APP_JWT_AUDIENCE` remain non-secret environment configuration.

## 5. Acceptance criteria

- **AC-NEO-01:** The GCP bootstrap no longer creates or references Cloud SQL,
  VPC, subnet, private-service access, or the Cloud SQL socket factory, and its
  `--plan`/`--apply` safety boundary is unchanged (never reads or prints a secret).
- **AC-NEO-02:** The workflow obtains short-lived WIF credentials and contains no
  `GCP_SA_KEY`, service-account key, or secret value.
- **AC-NEO-03:** The migration Job uses `NEON_MIGRATION_JDBC_URL` (direct) with
  `APP_MIGRATION_ONLY=true` and `SPRING_FLYWAY_ENABLED=true`, and Cloud Run no
  longer receives `--add-cloudsql-instances`, `--network`, `--subnet`, or
  `--vpc-egress`.
- **AC-NEO-04:** The API service uses `NEON_SERVICE_JDBC_URL` (pooled) with
  `SPRING_FLYWAY_ENABLED=false` and `prepareThreshold=0` applied for PgBouncer
  transaction mode.
- **AC-NEO-05:** The runtime service account is granted only Secret Manager
  access to the three named secrets; no Cloud SQL role remains.
- **AC-NEO-06:** Cloud Run starts the API against Neon and
  `/actuator/health/readiness` reports ready; exactly one migration Job runs
  before the service deploy.
- **AC-NEO-07:** The operator runbook gives exact Neon variable/JDBC formats, the
  Secret Manager names, a first-deploy checklist, readiness command, and a
  revision-based rollback that never reverses Flyway migrations.

## 6. Constraints and operational decisions

- Neon pooled endpoint uses PgBouncer transaction mode; only the serving API
  uses it, and JDBC prepared-statement caching is disabled for that datasource.
- Neon requires TLS with SNI; JDBC uses `sslmode=require` and
  `channelBinding=require` (pgjdbc `channelBinding` is camelCase).
- Cloud Run egress to Neon is public internet; the database is never exposed as
  a private-network resource.
- No secret value is ever read, printed, accepted, stored in a GitHub Variable,
  or committed. GitHub rejects an empty variable value, so an intentionally
  empty `GCP_CORS_ALLOWED_ORIGINS` is represented by the variable's absence.
- Deployment remains serialized; the migration Job has zero retries so a failed
  migration is a visible failed release.

## 7. Assumptions to govern

- The Neon project, branch, database, and least-privilege role already exist, and
  the role password is the current `algorithm-learning-db-password` value.
- The user authorizes the same GCP project, WIF trust boundary, and GitHub
  repository/branch/environment as `gcp-cloud-run-delivery`.
- `NEON_DATABASE_USERNAME` is currently the Neon owner role `neondb_owner`; a
  dedicated least-privilege role is recommended but not required to satisfy this
  feature's deployment path.
- No `plan.md`, `tasks.md`, or `WORK_GRAPH.yaml` node exists for this feature.

## 8. Governance record

- Documentation decision: `update-docs`.
- Ambiguity decision: `infer`.
- Chose Neon pooled endpoint for serving and direct endpoint for migrations over
  a single endpoint, because PgBouncer transaction mode breaks schema migrations.
- Chose to keep WIF, Artifact Registry, Cloud Run, and Secret Manager unchanged
  and change only the database/network path, minimizing the review surface.
- Recorded that GitHub Environment variables cannot hold an empty string; the
  empty CORS allowlist is represented by omitting the variable.
