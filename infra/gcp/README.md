# GCP Cloud Run bootstrap (Neon database)

`bootstrap.sh` provisions the non-secret foundation for the production API in
GCP project `alert-study-508214-s5`. The durable database is Neon Serverless
Postgres, reached over public TLS, so this script creates no VPC, subnet,
private-service-access range, or Cloud SQL instance. It is intentionally
separate from a deployment: it does not build an image, deploy Cloud Run, read a
secret, or create a Secret Manager secret version.

## Safety boundary

Run the default plan first:

```sh
bash infra/gcp/bootstrap.sh --plan
```

`--apply` creates or verifies the following GCP resources:

- the Artifact Registry Docker repository `algorithm-learning` and empty Secret
  Manager containers;
- `algorithm-learning-runtime` and `algorithm-learning-deployer`
  service accounts with the narrowly documented runtime/deployment roles; and
- a GitHub Actions OIDC Workload Identity Federation pool/provider restricted
  to `allenljf/algorithm-learning`, `refs/heads/main`, and the `production`
  GitHub Environment.

It never reads, prints, takes as an argument, or creates a value for any
secret. Do not add a service-account JSON key, `GCP_SA_KEY`, database password,
JWT key, refresh hash key, access token, or Neon connection string with a
password to this repository or GitHub.

## Operator prerequisites

The operator running `--apply` needs an authenticated `gcloud` session with
permission to enable project services, manage IAM and Workload Identity Pools,
and create Artifact Registry/Secret Manager containers. Use the correct account
locally; do not send its token to an agent.

```sh
gcloud auth login
gcloud config set project alert-study-508214-s5
bash infra/gcp/bootstrap.sh --apply
```

## Required manual Secret Manager values

After the script creates the empty containers, add one current version to each
in the GCP Console or Cloud Shell without putting the value in a shell history,
workflow, or chat message.

| Secret Manager ID | Cloud Run variable | Requirement |
|---|---|---|
| `algorithm-learning-jwt-key` | `APP_JWT_KEY` | Random value, at least 32 characters |
| `algorithm-learning-refresh-hash-key` | `APP_REFRESH_HASH_KEY` | A distinct random value, at least 32 characters |
| `algorithm-learning-db-password` | `DATABASE_PASSWORD` | Password of the Neon application role |

The Cloud Run runtime account can access only these three secrets. The GitHub
deployer cannot read them directly.

## Required GitHub Environment variables

Create a protected GitHub Environment named `production`, restrict deployment
to `main`, and record these non-secret values. `scripts/neon-cloud-run-setup-wizard.sh`
writes them to the ignored `infra/env/.env.neon` and can set them through `gh`.

| Variable | Value |
|---|---|
| `GCP_PROJECT_ID` | `alert-study-508214-s5` |
| `GCP_REGION` | `asia-east1` |
| `GCP_WIF_PROVIDER` | `projects/730295148186/locations/global/workloadIdentityPools/github-actions/providers/github-provider` |
| `GCP_DEPLOY_SERVICE_ACCOUNT` | `algorithm-learning-deployer@alert-study-508214-s5.iam.gserviceaccount.com` |
| `GCP_ARTIFACT_REPOSITORY` | `algorithm-learning` |
| `GCP_CLOUD_RUN_SERVICE` | `algorithm-learning-api` |
| `NEON_DATABASE_USERNAME` | Neon application role name |
| `NEON_MIGRATION_JDBC_URL` | `jdbc:postgresql://<direct-host>/<database>?sslmode=require&channelBinding=require` |
| `NEON_SERVICE_JDBC_URL` | `jdbc:postgresql://<pooled-host>/<database>?sslmode=require&channelBinding=require` |

Do not create `GCP_CORS_ALLOWED_ORIGINS` with an empty value: GitHub rejects an
empty variable. Leave it absent until a controlled Flutter/Compose Web origin
exists; the workflow treats it as empty.

GitHub automatically issues the OIDC token at deployment time. Do not create a
GitHub Actions secret for GCP credentials, and never store a Neon JDBC URL that
contains a password.

## IAM and WIF review checklist

- The WIF provider condition names only `allenljf/algorithm-learning`,
  `refs/heads/main`, and `production`.
- Only the WIF principal set can impersonate
  `algorithm-learning-deployer`.
- `algorithm-learning-runtime` has Secret Accessor only for the three named
  secrets. It has no Cloud SQL role because the database is Neon.
- `algorithm-learning-deployer` can publish to the one Artifact Registry
  repository, administer Cloud Run, and act as the runtime account. It is not a
  Secret Manager accessor and neither account has Owner or Editor.

Cloud Run Admin is project-scoped during bootstrap because the API service and
migration Job do not exist yet. After the first controlled deployment, narrow
that binding to the named service and Job if the organization policy permits
resource-level Cloud Run IAM bindings.

## Continuous-delivery workflow

`.github/workflows/gcp-production-deploy.yml` runs only for `main` (or a manual
dispatch whose ref still satisfies the WIF condition) and uses the protected
`production` Environment. Every referenced action is pinned to its full commit
SHA. The deploy job is the only job that requests `id-token: write`; it exchanges
GitHub's ephemeral OIDC token for credentials to
`algorithm-learning-deployer`. It never consumes a service-account key
or GitHub credential secret.

Each production run is serialized (`gcp-production-api`, with cancellation
disabled). After the API test suite, it builds and pushes exactly one immutable
Artifact Registry image:

```text
asia-east1-docker.pkg.dev/alert-study-508214-s5/algorithm-learning/api:<git-sha>
```

The workflow deploys that exact image to `algorithm-learning-migrate`, waits for
its one-shot execution to succeed, and only then deploys the same image to
`algorithm-learning-api`. Both workload types use
`algorithm-learning-runtime@alert-study-508214-s5.iam.gserviceaccount.com` and
Secret Manager references by name. They differ in the database endpoint:

- The migration Job sets `DATABASE_URL` to `NEON_MIGRATION_JDBC_URL` (the direct
  endpoint), `APP_MIGRATION_ONLY=true`, and `SPRING_FLYWAY_ENABLED=true`, with
  zero retries so a failed migration is a visible failed release.
- The serving revision sets `DATABASE_URL` to `NEON_SERVICE_JDBC_URL` (the
  pooled endpoint) plus `prepareThreshold=0`, and `SPRING_FLYWAY_ENABLED=false`;
  Flyway never runs on Cloud Run service startup.

Both set `DATABASE_USERNAME` to `NEON_DATABASE_USERNAME`. The migration uses the
direct endpoint because PgBouncer transaction pooling does not support the
session features migrations rely on; the service uses the pooled endpoint
because Cloud Run scales to many short-lived instances. The service is made
publicly invokable for the authentication routes, while the Job is never public.

## First-deploy checklist

NEO-003 is the only task authorized to run a real first release. Before it is
started, the operator verifies all of the following without entering any secret
value into GitHub, a shell history, source control, or this conversation:

1. `bash infra/gcp/bootstrap.sh --apply` has completed in the intended project.
2. The Neon project/database and a least-privilege application role exist; the
   role's password is the current value of `algorithm-learning-db-password`.
3. The `algorithm-learning-jwt-key` and
   `algorithm-learning-refresh-hash-key` containers each have a distinct,
   random value of at least 32 characters.
4. GitHub Environment `production` is protected to `main` and holds the
   non-secret variables above, with `GCP_CORS_ALLOWED_ORIGINS` absent.
5. `NEON_MIGRATION_JDBC_URL` and `NEON_SERVICE_JDBC_URL` are password-free and
   point at the direct and pooled endpoints respectively.
6. The WIF provider name has the form
   `projects/730295148186/locations/global/workloadIdentityPools/github-actions/providers/github-provider`,
   and its condition remains restricted to this repository, branch, and
   environment.
7. A reviewer has confirmed the generated workflow contains only SHA-pinned
   actions and no `GCP_SA_KEY`, credential JSON, or secret value.

## Rollout, verification, and rollback

For every release, preserve the deployed Git SHA, the new Cloud Run revision
name, and the immediately preceding known-good revision name in the release
record. The workflow itself fails before API deployment if the migration Job
does not complete successfully. After deployment, it verifies readiness with:

```sh
SERVICE_URL="$(gcloud run services describe algorithm-learning-api --region=asia-east1 --format='value(status.url)')"
curl --fail --retry 12 --retry-delay 5 "${SERVICE_URL}/actuator/health/readiness"
```

If the new service revision is unhealthy after a successful migration, do not
rerun or reverse Flyway. First list revisions and select the previously recorded
known-good serving revision:

```sh
gcloud run revisions list --service=algorithm-learning-api --region=asia-east1
gcloud run services update-traffic algorithm-learning-api \
  --region=asia-east1 \
  --to-revisions=KNOWN_GOOD_REVISION=100
```

Then repeat the readiness command. This rolls serving traffic back to the
known-good Cloud Run revision while retaining forward database migrations. It
is safe only when migrations are forward-compatible with the prior service;
otherwise pause the rollout and use a Neon branch/restore or incident procedure.
Never treat a reverse Flyway migration as the routine rollback mechanism.
