#!/usr/bin/env bash

set -euo pipefail

PROJECT_ID="alert-study-508214-s5"
PROJECT_NUMBER="730295148186"
REGION="asia-east1"
GITHUB_REPOSITORY="allenljf/algorithm-learning"
GITHUB_BRANCH="refs/heads/main"
GITHUB_ENVIRONMENT="production"

ARTIFACT_REPOSITORY="algorithm-learning"
RUNTIME_SERVICE_ACCOUNT="algorithm-learning-runtime"
DEPLOY_SERVICE_ACCOUNT="algorithm-learning-deployer"
WIF_POOL="github-actions"
WIF_PROVIDER="github-provider"

MODE="plan"

usage() {
  cat <<'EOF'
Usage: infra/gcp/bootstrap.sh [--plan|--apply]

Creates or verifies the non-secret GCP delivery foundation for Algorithm Learning.
The durable database is Neon Serverless Postgres, so this script creates no VPC,
no Cloud SQL instance, and no private-service-access range; Cloud Run reaches
Neon over TLS.

  --plan   Print the resources and IAM bindings that --apply will manage. This
           is the default and does not call gcloud.
  --apply  Create or verify resources in alert-study-508214-s5. It never reads,
           accepts, prints, or creates a Secret Manager secret value.
  --help   Show this message.

Before --apply, authenticate gcloud with an operator account authorized to
enable project APIs, manage IAM/WIF, and create Artifact Registry and Secret
Manager containers. See infra/gcp/README.md.
EOF
}

for argument in "$@"; do
  case "$argument" in
    --plan) MODE="plan" ;;
    --apply) MODE="apply" ;;
    --help|-h) usage; exit 0 ;;
    *) printf 'Unknown argument: %s\n' "$argument" >&2; usage >&2; exit 2 ;;
  esac
done

plan() {
  printf '%s\n' "$*"
}

require_gcloud() {
  command -v gcloud >/dev/null 2>&1 || {
    printf '%s\n' 'gcloud is required for --apply. Install the Google Cloud CLI first.' >&2
    exit 1
  }
}

ensure_project() {
  gcloud config set project "$PROJECT_ID" >/dev/null
}

ensure_service_account() {
  local account_id="$1"
  local display_name="$2"
  if ! gcloud iam service-accounts describe "${account_id}@${PROJECT_ID}.iam.gserviceaccount.com" >/dev/null 2>&1; then
    gcloud iam service-accounts create "$account_id" --display-name="$display_name"
  fi
}

ensure_secret_container() {
  local secret_id="$1"
  if ! gcloud secrets describe "$secret_id" >/dev/null 2>&1; then
    gcloud secrets create "$secret_id" --replication-policy=automatic
  fi
}

ensure_project_role() {
  local member="$1"
  local role="$2"
  gcloud projects add-iam-policy-binding "$PROJECT_ID" --member="$member" --role="$role" --quiet >/dev/null
}

ensure_secret_role() {
  local secret_id="$1"
  local member="$2"
  local role="$3"
  gcloud secrets add-iam-policy-binding "$secret_id" --member="$member" --role="$role" --quiet >/dev/null
}

ensure_bootstrap_resources() {
  local runtime_email="${RUNTIME_SERVICE_ACCOUNT}@${PROJECT_ID}.iam.gserviceaccount.com"
  local deploy_email="${DEPLOY_SERVICE_ACCOUNT}@${PROJECT_ID}.iam.gserviceaccount.com"
  local runtime_member="serviceAccount:${runtime_email}"
  local deploy_member="serviceAccount:${deploy_email}"
  local pool_resource="projects/${PROJECT_NUMBER}/locations/global/workloadIdentityPools/${WIF_POOL}"

  gcloud services enable \
    artifactregistry.googleapis.com \
    iam.googleapis.com \
    iamcredentials.googleapis.com \
    run.googleapis.com \
    secretmanager.googleapis.com \
    sts.googleapis.com

  if ! gcloud artifacts repositories describe "$ARTIFACT_REPOSITORY" --location="$REGION" >/dev/null 2>&1; then
    gcloud artifacts repositories create "$ARTIFACT_REPOSITORY" --repository-format=docker --location="$REGION" --description='Algorithm Learning API images'
  fi

  ensure_secret_container algorithm-learning-jwt-key
  ensure_secret_container algorithm-learning-refresh-hash-key
  ensure_secret_container algorithm-learning-db-password
  ensure_secret_container algorithm-learning-db-migration-password

  ensure_service_account "$RUNTIME_SERVICE_ACCOUNT" 'Algorithm Learning Cloud Run runtime'
  ensure_service_account "$DEPLOY_SERVICE_ACCOUNT" 'Algorithm Learning GitHub deployer'

  if ! gcloud iam workload-identity-pools describe "$WIF_POOL" --location=global >/dev/null 2>&1; then
    gcloud iam workload-identity-pools create "$WIF_POOL" --location=global --display-name='GitHub Actions'
  fi
  if ! gcloud iam workload-identity-pools providers describe "$WIF_PROVIDER" --location=global --workload-identity-pool="$WIF_POOL" >/dev/null 2>&1; then
    gcloud iam workload-identity-pools providers create-oidc "$WIF_PROVIDER" \
      --location=global \
      --workload-identity-pool="$WIF_POOL" \
      --display-name='Algorithm Learning GitHub' \
      --issuer-uri='https://token.actions.githubusercontent.com' \
      --attribute-mapping='google.subject=assertion.sub,attribute.repository=assertion.repository,attribute.ref=assertion.ref,attribute.environment=assertion.environment' \
      --attribute-condition="assertion.repository == '${GITHUB_REPOSITORY}' && assertion.ref == '${GITHUB_BRANCH}' && assertion.environment == '${GITHUB_ENVIRONMENT}'"
  fi

  ensure_secret_role algorithm-learning-jwt-key "$runtime_member" roles/secretmanager.secretAccessor
  ensure_secret_role algorithm-learning-refresh-hash-key "$runtime_member" roles/secretmanager.secretAccessor
  ensure_secret_role algorithm-learning-db-password "$runtime_member" roles/secretmanager.secretAccessor
  ensure_secret_role algorithm-learning-db-migration-password "$runtime_member" roles/secretmanager.secretAccessor

  gcloud artifacts repositories add-iam-policy-binding "$ARTIFACT_REPOSITORY" --location="$REGION" --member="$deploy_member" --role=roles/artifactregistry.writer --quiet >/dev/null
  ensure_project_role "$deploy_member" roles/run.admin
  gcloud iam service-accounts add-iam-policy-binding "$runtime_email" --member="$deploy_member" --role=roles/iam.serviceAccountUser --quiet >/dev/null
  gcloud iam service-accounts add-iam-policy-binding "$deploy_email" --member="principalSet://iam.googleapis.com/${pool_resource}/attribute.repository/${GITHUB_REPOSITORY}" --role=roles/iam.workloadIdentityUser --quiet >/dev/null
}

if [[ "$MODE" == "plan" ]]; then
  plan "GCP project: ${PROJECT_ID} (${PROJECT_NUMBER})"
  plan "Region: ${REGION}; Artifact Registry: ${ARTIFACT_REPOSITORY}"
  plan "Database: Neon Serverless Postgres reached over public TLS; no VPC, subnet, private-service range, or Cloud SQL resource is created."
  plan "Create empty Secret Manager containers only: algorithm-learning-jwt-key, algorithm-learning-refresh-hash-key, algorithm-learning-db-password, algorithm-learning-db-migration-password."
  plan "Create runtime and GitHub deploy service accounts; bind runtime secret access, Artifact Registry writer, Cloud Run admin, and service-account-user permissions."
  plan "Create WIF pool ${WIF_POOL} and provider ${WIF_PROVIDER}, restricted to ${GITHUB_REPOSITORY} ${GITHUB_BRANCH} environment ${GITHUB_ENVIRONMENT}."
  plan "No secret value is requested, read, printed, or created by this script."
  exit 0
fi

require_gcloud
ensure_project
ensure_bootstrap_resources

printf '%s\n' 'Bootstrap resources are ready. Follow infra/gcp/README.md for manual Secret Manager values and GitHub Environment variables.'
