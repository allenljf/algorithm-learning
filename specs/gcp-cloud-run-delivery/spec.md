# GCP Cloud Run Delivery Specification

## Document status

- Feature ID: `gcp-cloud-run-delivery`
- Status: governed; ready for `$work-graph`
- Requirement baseline: [`../../requirement.md`](../../requirement.md), sections 2, 7, and 8
- Intake mode: `brainstorm`
- Updated: 2026-09-10
- Governance mode: `update-docs + infer`
- Execution contract: not selected; it is chosen per ready task after planning

This feature turns the completed local Compose and provider-neutral Kubernetes
target into an actual, managed GCP delivery path. It is separate from the
unplanned `spaced-repetition` feature and does not authorize changes to product
behavior.

## 1. Goal

Deploy the existing Spring Boot API to Cloud Run from GitHub Actions without
placing long-lived GCP credentials in the repository or GitHub Secrets. Run the
API against a managed Cloud SQL for PostgreSQL instance, store runtime secrets
in Secret Manager, publish images to Artifact Registry, and authenticate the
deployment workflow through GitHub OIDC Workload Identity Federation (WIF).

The target project is `alert-study-508214-s5` (project number `730295148186`).

## 2. Chosen architecture

```text
GitHub Actions (allenljf/algorithm-learning, main + protected production environment)
  └─ GitHub OIDC short-lived token
       └─ GCP WIF provider
            └─ deploy service account
                 ├─ Artifact Registry: build/push API image
                 └─ Cloud Run migration Job: apply Flyway once
                      └─ Cloud Run API service: deploy Flyway-disabled revision
                       ├─ Secret Manager: runtime signing/hash keys and DB password
                       └─ Cloud SQL for PostgreSQL: durable data
```

- Region: `asia-east1`.
- Artifact Registry repository ID: `algorithm-learning` (Docker format).
- Cloud Run service name: `algorithm-learning-api`.
- GitHub Actions deploys only from `allenljf/algorithm-learning`'s protected `main` branch
  through a `production` GitHub Environment.
- The WIF provider maps GitHub OIDC `sub`, `repository`, `ref`, and
  `environment` claims. Its condition is exactly
  `assertion.repository == 'allenljf/algorithm-learning' &&
  assertion.ref == 'refs/heads/main' && assertion.environment == 'production'`.
  It must not accept arbitrary repositories, pull requests, or branches.
- A single Cloud Run Job named `algorithm-learning-migrate` runs the API image
  with `SPRING_MAIN_WEB_APPLICATION_TYPE=none` and Flyway enabled. GitHub
  Actions waits for that execution to succeed before deploying the API service
  revision with `SPRING_FLYWAY_ENABLED=false`. A workflow concurrency group
  permits one production release at a time. The service never runs migrations.

## 3. Scope

### In scope

- GCP resource bootstrap instructions or an idempotent infrastructure script
  for required APIs, Artifact Registry, Cloud Run, Cloud SQL, Secret Manager,
  the runtime service account, and the deploy service account.
- GitHub OIDC WIF setup scoped to the repository and production environment.
- A GitHub Actions workflow that tests, builds, publishes, and deploys the API
  image using short-lived WIF credentials.
- A Cloud Run migration Job configured from the same image and runtime identity
  as the API service, with deterministic completion and failure reporting.
- Cloud Run configuration for the existing health endpoints, Cloud SQL network
  connectivity, non-secret environment values, and Secret Manager references.
- A documented operator checklist for creating secret *values* outside the
  repository and for first deployment/rollback verification.
- Least-privilege IAM roles that are documented and reviewable.

### Out of scope

- Long-lived GCP service-account JSON keys, manually supplied access tokens,
  or secret values in GitHub Actions, source files, workflow logs, or commits.
- GKE, Kubernetes manifests, Terraform adoption, multi-region deployment,
  custom-domain/DNS configuration, CDN/WAF, or autoscaling/cost tuning beyond
  safe Cloud Run defaults.
- Flutter Web hosting; this feature deploys the API only.
- Application product changes, the `spaced-repetition` feature, database schema
  changes unrelated to deployment, or an automated reverse Flyway migration.

## 4. Required configuration contract

### 4.1 GitHub Actions non-secret variables

The workflow consumes these GitHub Environment variables. Their values are
resource identifiers, not credentials:

| Variable | Required value |
|---|---|
| `GCP_PROJECT_ID` | `alert-study-508214-s5` |
| `GCP_REGION` | `asia-east1` |
| `GCP_WIF_PROVIDER` | `projects/730295148186/locations/global/workloadIdentityPools/<pool-id>/providers/<provider-id>` |
| `GCP_DEPLOY_SERVICE_ACCOUNT` | `<deploy-account>@alert-study-508214-s5.iam.gserviceaccount.com` |
| `GCP_ARTIFACT_REPOSITORY` | `algorithm-learning` |
| `GCP_CLOUD_RUN_SERVICE` | `algorithm-learning-api` |

The workflow requests `id-token: write` only in the deployment job. GitHub's
automatically issued OIDC token is exchanged for short-lived GCP credentials;
no `GCP_SA_KEY` secret is allowed.

`GCP_CORS_ALLOWED_ORIGINS` is an additional GitHub Environment variable. It is
an explicit comma-separated allowlist. Until Flutter Web has a deployed,
controlled origin, it is the empty string, which means browser-origin
refresh/logout requests are rejected while non-browser health checks remain
available. The deployment must not silently fall back to `localhost`.

### 4.2 GCP Secret Manager runtime secrets

The following Secret Manager secret IDs map to the API's existing environment
variables. Operators set their values directly in GCP; the values are never
provided to an agent or stored in GitHub.

| Secret Manager ID | Cloud Run environment variable | Value constraint |
|---|---|---|
| `algorithm-learning-jwt-key` | `APP_JWT_KEY` | independently generated random value, at least 32 characters |
| `algorithm-learning-refresh-hash-key` | `APP_REFRESH_HASH_KEY` | distinct independently generated random value, at least 32 characters |
| `algorithm-learning-db-password` | `DATABASE_PASSWORD` | password for the least-privilege Cloud SQL application user |

Non-secret values remain Cloud Run environment configuration:
`DATABASE_URL`, `DATABASE_USERNAME`, `APP_CORS_ALLOWED_ORIGINS`,
`APP_JWT_ISSUER`, and `APP_JWT_AUDIENCE`.

## 5. Acceptance criteria

- **AC-GCP-01:** A production deployment workflow triggered from `main` obtains
  short-lived credentials through the configured WIF provider; no service
  account key or other long-lived GCP credential exists in GitHub Secrets.
- **AC-GCP-02:** WIF trust and IAM bindings restrict deployment access to this
  repository's protected production workflow; an untrusted repository, pull
  request, or branch cannot impersonate the deploy service account.
- **AC-GCP-03:** The workflow runs the API verification required by its task
  contract, builds the existing `services/api/Dockerfile`, pushes an immutable
  image tag to Artifact Registry, and deploys that exact image to Cloud Run.
- **AC-GCP-04:** Cloud Run receives database credentials, JWT signing material,
  and refresh-token HMAC material only from Secret Manager. They do not appear
  in source control, build images, command output, or GitHub configuration.
- **AC-GCP-05:** Cloud Run reaches its existing readiness endpoint while using
  Cloud SQL PostgreSQL; exactly one migration Job applies forward migrations
  before the service deployment, and Hibernate does not generate production
  DDL.
- **AC-GCP-06:** The runtime service account has only the permissions required
  to access its secrets and Cloud SQL. The deployment service account has only
  the permissions required to publish the image and deploy/configure this
  service.
- **AC-GCP-07:** The deployment guide gives exact resource-name formats,
  secret names, a first-deploy checklist, a health-check command, and a
  rollback method that redeploys a prior known-good Cloud Run revision without
  reversing database migrations.

## 6. Constraints and operational decisions

- Use Cloud Run and Cloud SQL rather than GKE to keep the single-user MVP
  operationally simple. The existing `infra/kubernetes/README.md` remains a
  future target, not an alternate deployment path in this feature.
- Use official Google GitHub Actions authentication tooling pinned to an
  immutable commit SHA. Deployment actions must not be referenced by a mutable
  tag alone.
- Use Cloud SQL's connector/socket path with encrypted managed connectivity;
  the instance uses PostgreSQL 16, private IP only, and Cloud Run Direct VPC
  egress through a dedicated subnet. The database must not expose a public
  application endpoint.
- Provision one regional Cloud SQL instance named `algorithm-learning-postgres`,
  database `algorithm_learning`, and least-privilege application user
  `algorithm_learning_app`. Enable automated backups and point-in-time recovery
  with seven-day retention. The database password is the Secret Manager value;
  the Cloud SQL instance connection name is non-secret configuration.
- Create distinct user-managed service accounts:
  `algorithm-learning-runtime` for the Cloud Run Job and API service, and
  `algorithm-learning-github-deployer` for GitHub Actions. Do not use the
  Compute Engine default service account.
- Grant the runtime account `roles/cloudsql.client` on this project and
  `roles/secretmanager.secretAccessor` only on the three named application
  secrets. Grant the deploy account `roles/artifactregistry.writer` on the
  `algorithm-learning` repository, `roles/run.admin` on the Cloud Run service
  and migration Job, and `roles/iam.serviceAccountUser` on the runtime account.
  WIF may impersonate only the deploy account. No role grants either account
  project Owner, Editor, or Secret Manager Administrator.
- The API remains publicly invokable so its register/login routes are reachable;
  grant `roles/run.invoker` to `allUsers` only on `algorithm-learning-api`.
  The migration Job is never public. Cloud SQL remains private.
- Use Cloud Run's service-level health checks or deployment verification against
  `/actuator/health/readiness`; do not expose a health endpoint that returns
  secrets or private data.
- The initial deployment uses the Cloud Run generated HTTPS URL. A custom
  domain and Flutter Web hosting are subsequent explicit changes. Until then,
  `APP_CORS_ALLOWED_ORIGINS` is empty and browser-origin state-changing auth
  requests are deliberately rejected.

## 7. Assumptions to govern

- The user authorizes the specified project, Cloud Run architecture, region,
  repository ID, service name, and `allenljf/algorithm-learning` as the only
  GitHub repository allowed to deploy. The default branch is confirmed as
  `main`.
- The user will personally create or enter every secret value in GCP Secret
  Manager and GitHub Environment configuration. The agent only receives key
  names and non-sensitive resource identifiers.
- The GCP account used for bootstrap has authority to enable APIs, create IAM
  resources, configure WIF, and create Cloud SQL/Cloud Run resources. An agent
  must never request or receive that account's token.
- No `plan.md`, `tasks.md`, or `WORK_GRAPH.yaml` node exists for this feature.
  `$work-graph` may create them only after `$spec-governance` accepts this
  specification.

## 8. Governance record

- Documentation decision: `update-docs`.
- Ambiguity decision: `infer`.
- WIF trust is restricted to the confirmed repository, protected branch, and
  GitHub Environment rather than to a broad owner or organization claim.
- The migration Job is selected over service-startup Flyway so that a serverless
  scale-out or repeat deployment cannot cause concurrent migrations.
- Cloud SQL private IP plus Direct VPC egress is selected over a public IP so
  database connectivity is never a publicly reachable application surface.
- The initial API is public because authentication begins unauthenticated; API
  authorization still protects all product data routes. Flutter Web hosting and
  its CORS origin are deferred, with an explicit empty CORS allowlist until a
  controlled browser origin exists.
