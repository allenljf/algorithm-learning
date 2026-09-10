# GCP Cloud Run bootstrap

`bootstrap.sh` provisions the non-secret foundation for the production API in
GCP project `alert-study-508214-s5`. It is intentionally separate from a
deployment: it does not build an image, deploy Cloud Run, read a secret, or
create a Secret Manager secret version.

## Safety boundary

Run the default plan first:

```sh
bash infra/gcp/bootstrap.sh --plan
```

`--apply` creates or verifies the following GCP resources:

- a custom VPC, regional subnet, private-service-access range, and private-IP
  PostgreSQL 16 Cloud SQL instance (`algorithm-learning-postgres`);
- the `algorithm_learning` database, Artifact Registry Docker repository
  `algorithm-learning`, and empty Secret Manager containers;
- `algorithm-learning-runtime` and `algorithm-learning-github-deployer`
  service accounts with the narrowly documented runtime/deployment roles; and
- a GitHub Actions OIDC Workload Identity Federation pool/provider restricted
  to `allenljf/algorithm-learning`, `refs/heads/main`, and the `production`
  GitHub Environment.

It never reads, prints, takes as an argument, or creates a value for any
secret. Do not add a service-account JSON key, `GCP_SA_KEY`, database password,
JWT key, refresh hash key, or access token to this repository or GitHub.

## Operator prerequisites

The operator running `--apply` needs an authenticated `gcloud` session with
permission to enable project services, manage IAM and Workload Identity Pools,
create VPC/Cloud SQL resources, and create Artifact Registry/Secret Manager
containers. Use the correct account locally; do not send its token to an agent.

```sh
gcloud auth login
gcloud config set project alert-study-508214-s5
bash infra/gcp/bootstrap.sh --apply
```

The script creates the database but deliberately cannot create its application
user: doing so would require handling the database password. In Cloud SQL,
create the least-privilege PostgreSQL user `algorithm_learning_app`, grant it
access only to database `algorithm_learning`, and use its password only for the
matching Secret Manager value below.

## Required manual Secret Manager values

After the script creates the empty containers, add one current version to each
in the GCP Console or Cloud Shell without putting the value in a shell history,
workflow, or chat message.

| Secret Manager ID | Cloud Run variable | Requirement |
|---|---|---|
| `algorithm-learning-jwt-key` | `APP_JWT_KEY` | Random value, at least 32 characters |
| `algorithm-learning-refresh-hash-key` | `APP_REFRESH_HASH_KEY` | A distinct random value, at least 32 characters |
| `algorithm-learning-db-password` | `DATABASE_PASSWORD` | Password of `algorithm_learning_app` |

The Cloud Run runtime account can access only these three secrets. The GitHub
deployer cannot read them directly.

## Required GitHub Environment variables

Create a protected GitHub Environment named `production`, restrict deployment
to `main`, and add these non-secret variables there after `--apply`:

| Variable | Value |
|---|---|
| `GCP_PROJECT_ID` | `alert-study-508214-s5` |
| `GCP_REGION` | `asia-east1` |
| `GCP_WIF_PROVIDER` | `projects/730295148186/locations/global/workloadIdentityPools/github-actions/providers/github-provider` |
| `GCP_DEPLOY_SERVICE_ACCOUNT` | `algorithm-learning-github-deployer@alert-study-508214-s5.iam.gserviceaccount.com` |
| `GCP_ARTIFACT_REPOSITORY` | `algorithm-learning` |
| `GCP_CLOUD_RUN_SERVICE` | `algorithm-learning-api` |
| `GCP_CORS_ALLOWED_ORIGINS` | Empty until a controlled Flutter Web origin is deployed |

GitHub automatically issues the OIDC token at deployment time. Do not create a
GitHub Actions secret for GCP credentials.

## IAM and WIF review checklist

- The WIF provider condition names only `allenljf/algorithm-learning`,
  `refs/heads/main`, and `production`.
- Only the WIF principal set can impersonate
  `algorithm-learning-github-deployer`.
- `algorithm-learning-runtime` has Cloud SQL Client and Secret Accessor only
  for the three named secrets.
- `algorithm-learning-github-deployer` can publish to the one Artifact Registry
  repository, administer Cloud Run, and act as the runtime account. It is not a
  Secret Manager accessor and neither account has Owner or Editor.

Cloud Run Admin is project-scoped during bootstrap because the API service and
migration Job do not exist yet. After the first controlled deployment, narrow
that binding to the named service and Job if the organization policy permits
resource-level Cloud Run IAM bindings.

## Continuous-delivery workflow

`.github/workflows/gcp-production-deploy.yml` runs only for `main` (or a
manual dispatch whose ref still satisfies the WIF condition) and uses the
protected `production` Environment. Every referenced action is pinned to its
full commit SHA. The deploy job is the only job that requests `id-token: write`;
it exchanges GitHub's ephemeral OIDC token for credentials to
`algorithm-learning-github-deployer`. It never consumes a service-account key
or GitHub credential secret.

Each production run is serialized (`gcp-production-api`, with cancellation
disabled). After the API test suite, it builds and pushes exactly one immutable
Artifact Registry image:

```text
asia-east1-docker.pkg.dev/alert-study-508214-s5/algorithm-learning/api:<git-sha>
```

The workflow deploys that exact image to `algorithm-learning-migrate`, waits
for its one-shot execution to succeed, and only then deploys the same image to
`algorithm-learning-api`. Both workload types use
`algorithm-learning-runtime@alert-study-508214-s5.iam.gserviceaccount.com`,
the dedicated Direct VPC network/subnet, and this non-secret Cloud SQL JDBC
format:

```text
jdbc:postgresql:///algorithm_learning?cloudSqlInstance=alert-study-508214-s5:asia-east1:algorithm-learning-postgres&ipTypes=PRIVATE&socketFactory=com.google.cloud.sql.postgres.SocketFactory
```

`services/api/pom.xml` includes the Cloud SQL PostgreSQL Socket Factory needed
by that URL. The connector uses the runtime service account and the private
network path; it does not put a database address, credentials, or certificate
in the image.

The migration Job sets `APP_MIGRATION_ONLY=true` and
`SPRING_FLYWAY_ENABLED=true`, with zero retries so a failed migration is a
visible failed release. The serving revision sets
`APP_MIGRATION_ONLY=false` and `SPRING_FLYWAY_ENABLED=false`; Flyway never runs
on Cloud Run service startup. Both reference the three Secret Manager values by
secret ID and version selector only. The service is made publicly invokable for
the authentication routes, while the Job is never public.

## First-deploy checklist

GCP-004 is the only task authorized to run a real first release. Before it is
started, the operator verifies all of the following without entering any secret
value into GitHub, a shell history, source control, or this conversation:

1. `bash infra/gcp/bootstrap.sh --apply` has completed in the intended project.
2. Cloud SQL has database `algorithm_learning` and least-privilege user
   `algorithm_learning_app`; the user's password is the current value of
   `algorithm-learning-db-password`.
3. The `algorithm-learning-jwt-key` and
   `algorithm-learning-refresh-hash-key` containers each have a distinct,
   random value of at least 32 characters.
4. GitHub Environment `production` is protected to `main` and holds every
   non-secret variable in the table above, including an intentionally empty
   `GCP_CORS_ALLOWED_ORIGINS` until a controlled Flutter Web origin exists.
5. The WIF provider name has the form
   `projects/730295148186/locations/global/workloadIdentityPools/github-actions/providers/github-provider`,
   and its condition remains restricted to this repository, branch, and
   environment.
6. A reviewer has confirmed the generated workflow contains only SHA-pinned
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
otherwise pause the rollout and use a separately authorized Cloud SQL restore
or incident procedure. Never treat a reverse Flyway migration as the routine
rollback mechanism.
