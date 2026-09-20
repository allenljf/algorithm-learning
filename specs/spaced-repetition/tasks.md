# Spaced Repetition — Task List

All task IDs, dependencies, statuses, and exact verification commands are
authoritative in `agent-workflow/WORK_GRAPH.yaml`. This document is the
human-readable execution contract.

## Phase 1 — Server scheduling

### SR-001 — Implement the adaptive-v1 policy, V2 migration, and latest-event due derivation

- Deliverable: an `adaptive-v1` review policy (section 3.2) with injected
  `Clock`, persisted snapshot fields, and the `V2` Flyway migration that adds
  `previous_interval_days`, `previous_ease_factor`, `interval_days`,
  `ease_factor`, `repetitions` and replaces the `V1`
  `reviews_policy_version_check`; `ReviewService` selects `adaptive-v1` for new
  events and returns the metadata; `GET /reviews/today` returns owned problem
  summaries from each problem's latest event (AC-SR-04/08); `POST /reviews`
  keeps returning the created review event with nullable schedule metadata.
- Depends on: none
- Parallel group: `sr-server`
- Spec refs: 2, 3.1, 3.2, 4 AC-SR-01..05/07/08, 5.1, 5.2, 5.4
- Execution contract: `three-perspectives` analysis; `tdd` test approach;
  `update-docs` documentation; `infer` ambiguity handling.
- Verification: `cd services/api && ./mvnw -q test -Dtest='*ReviewPolicyTest,*ReviewTest,*ReviewControllerTest,*SchemaMigrationTest'`;
  `cd services/api && ./mvnw -q test`; `git diff --check`
- Status: ready

## Phase 2 — Compose client

### SR-002 — Add Compose schedule context and align the due contract

- Deliverable: Ktor review remote/repository/DTO changes for the schedule
  metadata, `ReviewRepository.due` consuming the reconciled problem-summary
  contract, immutable domain schedule fields, and localized schedule context on
  the problem detail and Review completion via `AppStrings`; state-holder and
  Compose structure tests.
- Depends on: SR-001
- Parallel group: `sr-client`
- Spec refs: 2, 3.3, 4 AC-SR-04/06, 5.2, 5.3
- Execution contract: `three-perspectives` analysis; `tdd` test approach;
  `update-docs` documentation; `infer` ambiguity handling.
- Verification: `cd apps/multiplatform && ./gradlew :shared:allTests`;
  `cd apps/multiplatform && ./gradlew :composeApp:allTests`; `git diff --check`
- Status: pending

## Phase 3 — Acceptance and release

### SR-003 — Run cross-platform acceptance for adaptive scheduling

- Deliverable: acceptance coverage that proves the adaptive schedule end to end
  (server policy/migration plus the Compose schedule context), including
  historical `fixed-v1` compatibility, latest-event due derivation, and
  localized schedule text; update the reviewer docs.
- Depends on: SR-001, SR-002
- Parallel group: `sr-release`
- Spec refs: 4 AC-SR-01..08, 5.4
- Execution contract: `three-perspectives` analysis; `test-candidates` test
  approach; `update-docs` documentation; `infer` ambiguity handling.
- Verification: `cd services/api && ./mvnw -q verify`;
  `cd apps/multiplatform && ./gradlew :composeApp:allTests`; `git diff --check`
- Status: pending

### SR-004 — Apply the production migration and verify readiness

- Deliverable: the operator-gated production migration/release. Push runs the
  serialized workflow, which migrates once and redeploys the API; verify
  readiness and that `fixed-v1` history is intact.
- Depends on: SR-003
- Parallel group: `sr-release`
- Spec refs: 1, 5.1, 5.4
- Execution contract: `three-perspectives` analysis; `test-candidates` test
  approach; `update-docs` documentation; `infer` ambiguity handling.
- Verification: `gcloud run jobs execute algorithm-learning-migrate --region=asia-east1 --wait`;
  `gcloud run services describe algorithm-learning-api --region=asia-east1 --format='value(status.url)'`;
  `curl --fail --retry 12 --retry-delay 5 "$(gcloud run services describe algorithm-learning-api --region=asia-east1 --format='value(status.url)')/actuator/health/readiness"`;
  `git diff --check`
- Status: pending
