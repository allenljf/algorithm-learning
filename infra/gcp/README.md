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

## Next phase

GCP-003 adds the GitHub deployment workflow. That workflow will create/update
the `algorithm-learning-migrate` Cloud Run Job, wait for it to complete, then
deploy the Flyway-disabled `algorithm-learning-api` service. The Job sets
`APP_MIGRATION_ONLY=true` and leaves Flyway enabled; the serving service sets
`SPRING_FLYWAY_ENABLED=false`. GCP-004 is the only task authorized to run the
bootstrap and first-release commands.
