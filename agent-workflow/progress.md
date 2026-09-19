# Progress

The Flutter application root and backend composition root are complete.

## CMP-007 closeout — 2026-09-19

- Added the cross-platform acceptance suite in `composeApp`. The test-only
  `ControlledApiAdapter` (`commonTest/.../acceptance`) implements the existing
  `AuthRepository`, `ProblemRepository`, `TagRepository`, `SolutionRepository`,
  `ReviewRepository`, and `DashboardRepository` contracts over in-memory state,
  returning defensive copies so callers cannot observe or mutate its internals.
  It never ships and adds no production behavior.
- `ComposeAcceptanceTest` (`commonTest`) runs one shared-session journey on
  Android and Web: register, start from an empty library, create a tagged
  problem, add a solution, search/filter, work the ordered Review Mode and
  submit a confidence, then read the dashboard aggregate the journey produced
  (total 1, hard 1, due 0) and sign out.
- `AcceptanceUiTest` (`wasmJsTest`) renders the real stateless screens bound to
  the real view models and the same adapter, driving each experience through the
  UI: register through the form, create a problem and solution, reveal in order
  and submit a review, and read the dashboard totals/distribution/due count.
- `update-docs`: added `COMPOSE_GUIDE.md` section 14 (cross-platform acceptance
  rules and the `commonTest`/`wasmJsTest` split) and extended
  `infra/acceptance/README.md` with the Compose client acceptance boundary and
  evidence mapping. Owner isolation, authorization, and status mapping remain
  with the API and data-layer suites.
- Task-limited verification passed exactly as contracted:
  `cd apps/multiplatform && ./gradlew :composeApp:assembleDebug` (BUILD
  SUCCESSFUL); `cd apps/multiplatform && ./gradlew :composeApp:allTests`
  (Android `testDebugUnitTest` 29 tests, Wasm `wasmJsBrowserTest` 48 tests, 0
  failures); `git diff --check` clean.
- Evaluator conclusions: the adapter is test-only and no composable reads a
  repository; the four experiences share one coherent session through the
  injected contracts; every user-visible string resolves through `AppStrings`;
  the closeout commit is local only.
- `CMP-007` is completed. `CMP-008` (retire the Flutter client) is now ready in
  the same Phase 4 — Release group.

## CMP-007 execution strategy — 2026-09-19

- Dependency reconciliation confirmed `CMP-004`, `CMP-005`, and `CMP-006` are
  completed; `CMP-007` advanced from `ready` to `in_progress`.
- Confirmed the contract recorded by `$work-graph`: `three-perspectives` /
  `test-candidates` / `update-docs` / `infer`.
- Planner boundary: one cross-platform acceptance suite that exercises the four
  experiences (auth, problem management, browse/staged Review Mode, dashboard)
  against a controlled, stateful API adapter over the shared repository
  contracts, running on Android and Web. Implementer boundary:
  `apps/multiplatform/composeApp` test source sets and the acceptance/guide
  documentation. Evaluator boundary: the adapter is test-only, no production
  composable reads a repository, every user-visible string resolves through
  `AppStrings`, and the exact task verification passes.
- `update-docs`: record the acceptance suite boundary in `COMPOSE_GUIDE.md` and
  `infra/acceptance/README.md`.

## CMP-006 closeout — 2026-09-19

- Implemented the dashboard/home experience in `composeApp` `commonMain` under
  `com.algorithmlearning.app.dashboard`: `DashboardViewModel` (sealed
  `DashboardState`: loading/content/failed) and the stateless `DashboardScreen`
  plus a `DashboardActions` callback object.
- Matches ALG-015 and the delivered counts-only `GET /dashboard` shape: loading
  spinner, failed state with retry, no-data guidance with an add-problem action
  when `totalProblems == 0`, and the populated totals, difficulty distribution
  (proportional bars for easy/medium/hard), and due-review count.
- `App` reloads the aggregate whenever the Dashboard destination becomes
  current (`LaunchedEffect(current)`), so the due count reflects a review just
  submitted in the Review tab; the empty-state action opens a new problem draft
  on the Problems destination.
- `update-docs`: added `COMPOSE_GUIDE.md` section 13 covering the dashboard
  presentation, state shape, refresh behavior, and testing seams; extended
  `AppStrings` with the dashboard strings in both languages.
- Tests: `DashboardViewModelTest` (commonTest) proves the content counts,
  failure/retry, and reload behavior; `DashboardScreenUiTest` (wasmJsTest)
  renders the loading, failed, empty, and populated states and asserts the
  totals, distribution, and due count.
- Task-limited verification passed exactly as contracted:
  `cd apps/multiplatform && ./gradlew :composeApp:allTests` (Android
  `testDebugUnitTest` 28 tests; Wasm `wasmJsBrowserTest` 43 tests) and
  `git diff --check` clean.
- Evaluator conclusions: no composable reads a repository; the counts-only
  aggregate is rendered as loading/failed/empty/content; every user-visible
  string resolves through `AppStrings`; the closeout commit is local only.
- `CMP-006` is completed. All Phase 3 experiences are done, so `CMP-007`
  advanced from `pending` to `ready`; it opens Phase 4 — Release and requires a
  new conversation per the workflow continuation contract.

## CMP-006 execution strategy — 2026-09-19

- Dependency reconciliation confirmed `CMP-002` and `CMP-003` are completed;
  `CMP-006` advanced from `ready` to `in_progress`.
- Confirmed the contract recorded by `$work-graph`: `three-perspectives` /
  `tdd` / `update-docs` / `infer`.
- Planner boundary: one Dashboard/home experience over the counts-only
  `DashboardRepository`, with loading, retry, no-data guidance, totals,
  difficulty distribution, and due-review count, matching ALG-015 and the
  delivered `GET /dashboard` shape. Implementer boundary:
  `apps/multiplatform/composeApp` and the Compose guide. Evaluator boundary: no
  composable reads a repository, every user-visible string resolves through
  `AppStrings`, and the exact task verification passes.
- `update-docs`: extend `COMPOSE_GUIDE.md` with the dashboard presentation layer
  and its testing seams.

## CMP-005 closeout — 2026-09-19

- Implemented the review experience in `composeApp` `commonMain` under
  `com.algorithmlearning.app.review`: `ReviewViewModel` (monotonic
  `ReviewStage`, `DueReviewState`, `ReviewSessionState`, `ReviewUiState`) and
  the stateless `ReviewScreen` plus a `ReviewActions` callback object.
- Adaptive browse and staged Review Mode match ALG-014: the Review tab lists
  due problems (`ReviewRepository.due`) with loading/failed/empty/content
  states and retry; selecting one loads its detail and starts at the Problem
  stage. Reveal advances only Problem → Think → Hint → My Approach → Solution →
  Mark confidence, so confidence is unreachable before the solution is shown.
  The Solution stage switches among solutions; the confidence stage accepts
  `0...4` and an optional trimmed note, and submission is gated on a chosen
  confidence.
- Adaptive layout: `App` measures width with `BoxWithConstraints` and passes
  `twoPane = maxWidth >= 840.dp`; the wide layout shows the due list beside the
  session (or a select prompt) and the narrow layout swaps between them, both
  rendering the same state. The problem detail now exposes a "Review problem"
  action that opens the session and switches to the Review destination.
- `update-docs`: added `COMPOSE_GUIDE.md` section 12 covering the review
  presentation, disclosure gating, adaptive layout, entry points, and testing
  seams; extended `AppStrings` with the review strings in both languages.
- Tests: `ReviewViewModelTest` (commonTest) proves failed/empty/content due
  states, ordered disclosure, confidence bounds and gating, note trimming,
  solution switching, exit, and failure handling. `ReviewScreenUiTest`
  (wasmJsTest) drives the real view model through the UI to prove the ordered
  reveal, gated submit, and the two-pane/narrow layouts.
- Task-limited verification passed exactly as contracted:
  `cd apps/multiplatform && ./gradlew :composeApp:allTests` (Android
  `testDebugUnitTest` 25 tests; Wasm `wasmJsBrowserTest` 36 tests) and
  `git diff --check` clean.
- Evaluator conclusions: no composable reads a repository; the disclosure order
  and `0...4` confidence bound hold in presentation state; every user-visible
  string resolves through `AppStrings`; the closeout commit is local only.
- `CMP-005` is completed. `CMP-006` (dashboard and home UI) remains ready in the
  same Phase 3 `cmp-experiences` group.

## CMP-005 execution strategy — 2026-09-19

- Dependency reconciliation confirmed `CMP-002` and `CMP-003` are completed;
  `CMP-005` advanced from `ready` to `in_progress`.
- Confirmed the contract recorded by `$work-graph`: `three-perspectives` /
  `tdd` / `update-docs` / `infer`.
- Planner boundary: one Review experience built on the shared
  `ReviewRepository` and `ProblemRepository` — an adaptive Today's Review browse
  list plus a staged Review Mode session (Problem → Think → Hint → My Approach →
  Solution → Mark confidence) with confidence gating, populated by the frozen
  REST contract. Implementer boundary: `apps/multiplatform/composeApp` and the
  Compose guide. Evaluator boundary: no composable reads a repository, the
  disclosure order and confidence gating hold in presentation state, every
  user-visible string resolves through `AppStrings`, and the exact task
  verification passes.
- `update-docs`: extend `COMPOSE_GUIDE.md` with the review presentation layer
  and its testing seams.

## CMP-004 closeout — 2026-09-19

- Implemented the auth and problem-management Compose experience in
  `composeApp` `commonMain`: `App.kt` restores the session behind
  `AuthSessionHolder`, shows a localized `AuthScreen` (sign-in/register) until a
  session exists, and renders the signed-in scaffold with the problem screen
  plus Settings session identity and sign-out.
- Presentation layer: `AuthViewModel` (`AuthFormState`, `AuthErrorKind`) and
  `ProblemsViewModel` (`ProblemsUiState`, sealed `ProblemListState`,
  `ProblemEditorState`, `ProblemDetailState`, `ProblemsMessage`). Screens are
  stateless: `AuthScreen` takes state plus callbacks, and `ProblemsScreen` takes
  state plus a `ProblemsActions` callback object. No composable reads a
  repository, `HttpClient`, or `AppContainer` other than the `App` root.
- Problem management mirrors ALG-013: search, difficulty/platform/review/tag
  filters, pagination, distinct loading/failed/empty/content states with retry,
  create/edit with a locally required title, retained drafts and server
  `fieldErrors`, confirmed delete, ordered detail sections, and independent
  solution create/delete. Tags load and can be created and assigned.
- Localization (`update-docs`): extended `AppStrings`/`StringCatalog` with the
  auth and problem strings in English and Traditional Chinese, and added
  `Labels.kt` mapping domain enums and failure kinds to catalog fields.
- `update-docs`: documented the presentation layer, DI flow, and testing seams
  as `COMPOSE_GUIDE.md` section 11, plus the experience test target in section 6.
- Added state-holder tests under `composeApp/src/commonTest` with fakes and
  Compose structure tests under `composeApp/src/wasmJsTest`. `composeApp` now
  depends on `kotlinx-coroutines-core`, `kotlinx-coroutines-test`, and, for the
  Wasm test target only, `org.jetbrains.compose.ui:ui-test`.
- Task-limited verification passed exactly as contracted:
  `cd apps/multiplatform && ./gradlew :composeApp:allTests` (Android
  `testDebugUnitTest` 16 tests; Wasm `wasmJsBrowserTest` 23 tests, including 7
  Compose structure tests) and `git diff --check` clean.
- Evaluator conclusions: composables render state and emit events only; every
  user-visible string resolves through `AppStrings`; no DTO, Ktor, or domain
  data-layer type reaches a screen; the closeout commit is local only.
- `CMP-004` is completed. `CMP-005` and `CMP-006` remain ready in the same
  Phase 3 `cmp-experiences` group.

## CMP-004 execution strategy — 2026-09-19

- Dependency reconciliation confirmed `CMP-002` and `CMP-003` are completed;
  `CMP-004` advanced from `ready` to `in_progress`.
- Confirmed the contract recorded by `$work-graph`: `three-perspectives` /
  `tdd` / `update-docs` / `infer`.
- Planner boundary: one Compose experience for auth (login/register/session)
  plus problem list/search/filter/create/edit/delete with solutions and tags,
  rendering shared `AuthSessionHolder` and library repositories through a thin
  presentation layer; the REST contract and data layers are unchanged.
  Implementer boundary: `apps/multiplatform/composeApp` and the Compose guide.
  Evaluator boundary: composables receive state and callbacks only (no
  repository access), all user-visible strings resolve through `AppStrings`,
  and the exact task verification passes.
- `update-docs`: extend `COMPOSE_GUIDE.md` with the presentation layer and its
  testing seams.

## CMP-003 closeout — 2026-09-19

- Pushed the deferred NEO-003 closeout and the CMP-002 closeout to `main`
  (`323544a..96fdea0`); production deploy run `35411580086` completed
  successfully.
- Implemented the shared library data flow in `shared` `commonMain` under
  `com.algorithmlearning.shared.library`: immutable models and wire enums
  (`Tag`, `ProblemSummary`/`ProblemDetail`, `ProblemWrite`, `Solution`,
  `SolutionWrite`, `Review`, `ReviewSummary`, `ProblemQuery`, `Dashboard`,
  `Page`, plus `ProblemPlatform`/`Difficulty`/`Sort`/`SolutionLanguage`/
  `ReviewStatus`), `ApiFailure`/`ApiFailureKind`, five typed remote contracts,
  five repository contracts, and delegating `Remote*` adapters.
- Added the Ktor data layer in `library/data`: DTOs and mappers, `Ktor*Remote`
  implementations for problems/solutions/tags/reviews/dashboard, and `ApiClient`
  which attaches the bearer token from `AuthRepository`, retries exactly once
  after a `401` (invalidating the token), and maps statuses/problem bodies to
  `ApiFailure` including `fieldErrors`. DTOs stay `internal`; enums convert via
  `fromWire`.
- Reconciled the frozen API: platform wire value is `hacker_rank`; `GET
  /dashboard` is the counts-only shape; `/reviews/today` and `/reviews/history`
  return bare lists. Wired all five repositories through `AppContainer`.
- `update-docs`: added `COMPOSE_GUIDE.md` section 10 covering routes, wire
  values, `ApiClient` auth/error behavior, DI, and testing seams.
- Task-limited verification passed exactly as contracted:
  `cd apps/multiplatform && ./gradlew :shared:allTests` (Android
  `testDebugUnitTest` and `wasmJsBrowserTest` both green: 71 tests per target,
  34 new for the library); `git diff --check` clean. No lockfile change was
  needed.
- Evaluator conclusions: no DTO, Ktor, or serialization type reaches a
  repository caller or `composeApp`; unknown enum values and malformed bodies
  become `ApiFailure`; concurrent `401` handling refreshes at most once; the
  closeout commit is local only.
- `CMP-003` is completed. `CMP-004`, `CMP-005`, and `CMP-006` (Phase 3 —
  Experiences) are now ready; they share the `cmp-experiences` parallel group.

## CMP-003 execution strategy — 2026-09-19

- Dependency reconciliation confirmed `CMP-001`, `CMP-002`, and `ALG-007..010`
  are completed; `CMP-003` advanced from `ready` to `in_progress`.
- Confirmed the contract recorded by `$work-graph`: `three-perspectives` /
  `tdd` / `update-docs` / `infer`.
- Reconciled the frozen REST contract with the delivered API. Two spec/API
  deltas are resolved in favor of the deployed controller: platform wire value
  is `hacker_rank` (not `hackerRank`), and `GET /dashboard` returns the
  counts-only `DashboardCounts` shape, not the richer response sketched in the
  spec. `/reviews/today` and `/reviews/history` return bare lists, not paged
  envelopes.
- Planner boundary: one commonMain library data layer (immutable models, typed
  remotes, Ktor implementations with bearer auth plus one 401 retry, repository
  seams, fakes) adapting to the frozen API. Implementer boundary:
  `apps/multiplatform/shared` and the Compose guide. Evaluator boundary: DTOs
  stay in the data layer, no `android.*`/browser types in commonMain, unknown
  enums never leak, and the exact task verification passes.
- `update-docs`: extend `COMPOSE_GUIDE.md` with the library data-flow map.

## CMP-002 closeout — 2026-09-19

- Implemented the shared auth/session data flow in `shared` `commonMain` under
  `com.algorithmlearning.shared.auth`: immutable `AuthUser`/`AuthSession`/
  `AccessToken`/`AuthState`, the `AuthRemote` transport contract, the
  `AuthRepository` application contract, `RemoteAuthRepository` with an
  in-memory access token and mutex-serialized single-flight refresh, and
  `AuthSessionHolder` exposing a coalesced-restore `StateFlow<AuthState>`.
- Added the Ktor data layer: `auth/data` DTOs and mappers, `KtorAuthRemote` for
  the frozen `/api/v1/auth` routes (`register`, `login`, `refresh`, `me`,
  `logout`) with status/transport-to-`AuthFailure` mapping, and an
  `expect fun createAuthHttpClient()` with an OkHttp (`HttpCookies`) Android
  actual and a `Js` Wasm actual. DTOs stay internal to the data layer.
- Wired the stack through `AppContainer`, whose `baseUrl`, `httpClient`, and
  `clock` are constructor seams; no service locator is used. Added tests
  authored before implementation: `FakeAuthRemote`, model tests, repository
  single-flight/expiry/restore/logout tests, session-holder coalescing tests,
  and `MockEngine` tests for routes, bearer auth, DTO mapping, and error kinds.
- `update-docs`: extended `COMPOSE_GUIDE.md` with a section 9 describing the
  auth/session data flow, DI seams, and testing strategy.
- Task-limited verification passed exactly as contracted:
  `cd apps/multiplatform && ./gradlew :shared:allTests` (Android
  `testDebugUnitTest` and `wasmJsBrowserTest` both green: 7 existing plus 30 new
  auth tests per target); `git diff --check` clean. `kotlinWasmUpgradeYarnLock`
  ran once as required build maintenance because the Ktor JS engine changed
  `kotlin-js-store/wasm/yarn.lock`.
- Evaluator conclusions: no DTO, HTTP error, or Ktor type reaches `composeApp`;
  `commonMain` contains no `android.*` or browser API; concurrent expired-token
  callers collapse to one refresh; the closeout commit is local only.
- `CMP-002` is completed; `CMP-003` (library/review/dashboard data flows) is now
  the only `ready` task and shares Phase 2 with `CMP-002`.

## CMP-002 execution strategy — 2026-09-19

- Dependency reconciliation confirmed `CMP-001` and `ALG-005` are completed;
  `CMP-002` advanced from `ready` to `in_progress`.
- Confirmed the contract recorded by `$work-graph`: `three-perspectives` /
  `tdd` / `update-docs` / `infer`.
- Planner boundary: one commonMain auth/session data flow (Ktor remote contract,
  in-memory access token, single-flight refresh, session state holder) adapting
  to the frozen `/api/v1/auth` REST contract, with platform HTTP engines as the
  only actuals. Implementer boundary: `apps/multiplatform/shared` and the
  Compose guide. Evaluator boundary: DTOs stay in the data layer, no
  `android.*`/browser types in commonMain, concurrent refresh collapses to one
  rotation, and the exact task verification passes.
- `update-docs`: extend `COMPOSE_GUIDE.md` with the auth/session data-flow
  contract and testing seams.

## NEO-003 closeout — 2026-09-19

- Provisioned the GCP foundation in `alert-study-508214-s5`: enabled the
  Artifact Registry, IAM, IAM Credentials, Cloud Run, Secret Manager, and STS
  APIs; created the `algorithm-learning` Artifact Registry repository, the
  `algorithm-learning-runtime` and `algorithm-learning-deployer` service
  accounts, the `github-actions` WIF pool with a `main`/`production`-restricted
  provider, and the least-scope IAM bindings. No VPC, subnet,
  private-service-access range, or Cloud SQL resource exists (AC-NEO-01,
  AC-NEO-05).
- Pushed `main` to `allenljf/algorithm-learning`; workflow run `35410591526`
  passed: the OIDC deploy job published
  `asia-east1-docker.pkg.dev/alert-study-508214-s5/algorithm-learning/api:323544a2ecc771bbe3ed806c525553879c27d8aa`,
  deployed `algorithm-learning-migrate`, ran exactly one Flyway migration
  against the Neon direct endpoint, then deployed the Flyway-disabled
  `algorithm-learning-api` on the pooled endpoint, and verified readiness
  (AC-NEO-02, AC-NEO-03, AC-NEO-04, AC-NEO-06).
- Task-limited verification passed exactly as contracted:
  `bash infra/gcp/bootstrap.sh --apply`; `gcloud run jobs execute
  algorithm-learning-migrate --region=asia-east1 --wait` (execution
  `algorithm-learning-migrate-wsn48` completed); `gcloud run services describe
  algorithm-learning-api --region=asia-east1 --format='value(status.url)'`
  returned `https://algorithm-learning-api-qvepavg7qa-de.a.run.app`; and
  `curl --fail .../actuator/health/readiness` returned `{"status":"UP"}`.
- Release evidence for rollback (AC-NEO-07): image
  `api:323544a2ecc771bbe3ed806c525553879c27d8aa`; service revision
  `algorithm-learning-api-00001-c6z` (the first revision, so there is no prior
  known-good revision to return to yet). Future rollbacks select a recorded
  revision with `gcloud run services update-traffic` and never reverse Flyway
  migrations.
- Recorded deviation: `NEON_DATABASE_USERNAME` remains the Neon owner
  `neondb_owner`. The governed spec (section 7) permits this for the deployment
  path, but the runbook checklist prefers a least-privilege role; a follow-up
  should create a dedicated Neon role, update
  `algorithm-learning-db-password` and the GitHub `production` variable, and
  redeploy.
- `NEO-003` is completed and the `neon-cloud-run-delivery` feature is complete.
  The closeout commit is local only: pushing documentation-only commits to
  `main` would trigger another production deploy, so the push is deferred.

## NEO-003 recovery evidence — 2026-09-19

- Round 1 (bootstrap): `bash infra/gcp/bootstrap.sh --apply` failed creating the
  deploy service account because `algorithm-learning-github-deployer` is 35
  characters (GCP limit 30). Renamed it to `algorithm-learning-deployer` in
  `infra/gcp/bootstrap.sh`, `infra/gcp/README.md`, and
  `specs/neon-cloud-run-delivery/spec.md`, and updated the GitHub `production`
  variable `GCP_DEPLOY_SERVICE_ACCOUNT`. Re-ran `--apply`.
- Round 2 (bootstrap): `--apply` then failed creating the WIF provider because
  the display name `Algorithm Learning GitHub Actions` is 33 characters (limit
  32). Shortened it to `Algorithm Learning GitHub`; `--apply` then completed and
  created the runtime/deploy accounts, the WIF pool and provider, the Artifact
  Registry repository, and the IAM bindings.
- Round 3 (first release): the pushed `main` workflow passed Verify API and the
  image build, deployed the migration Job, and Flyway migrated successfully
  against the Neon direct endpoint, but the Job then exited 1: with
  `APP_MIGRATION_ONLY=true` the non-web context still created
  `apiSecurityFilterChain`, which requires an `HttpSecurity` bean that does not
  exist outside a servlet context. Made `apiSecurityFilterChain` and
  `corsConfigurationSource` `@ConditionalOnWebApplication(type = SERVLET)` so
  migration-only startup keeps the service beans but no servlet security chain,
  and added `SecurityConfigurationMigrationModeTest`
  (`./mvnw -o test -Dtest='SecurityConfigurationMigrationModeTest'` passes).

## NEO-003 execution strategy — 2026-09-19

- Dependency reconciliation confirmed `NEO-002` is completed; `NEO-003` advanced
  from `ready` to `in_progress`.
- The user authorized the real first release (recorded in conversation).
- Selected contract: `three-perspectives` / `test-candidates` / `update-docs` /
  `ask-with-options`.
- Planner boundary: apply the Neon-compatible GCP bootstrap, then let the pushed
  `main` workflow run exactly one migration before the service deploy.
  Implementer boundary: bootstrap execution, the release commit/push, and the
  workflow run. Evaluator boundary: record the deployed Git SHA, the new and
  prior known-good revisions, readiness evidence, and a revision-based rollback;
  never read, print, or store a secret value.
- Material decision open: `NEON_DATABASE_USERNAME` is the Neon owner
  `neondb_owner`. The runbook first-deploy checklist expects a least-privilege
  role, but no Neon CLI or `psql` is installed locally and the agent must not
  handle the role password, so creating a dedicated role is an operator-only
  step.

## CMP-001 closeout — 2026-09-19

- Established `apps/multiplatform` as a Gradle/Kotlin-DSL project with modules
  `:shared` and `:composeApp` and a single pinned version catalog: Kotlin
  2.4.10, Compose Multiplatform 1.11.1, AGP 9.1.1, Gradle wrapper 9.7.1 (SHA-256
  pinned). `:shared` targets Android and Wasm and holds the domain placeholder,
  `AppLanguage`/`StringCatalog` localization, the `Navigator` state holder, and
  the manual DI root `AppContainer`; `:composeApp` holds the shared Compose UI
  (`App`, `AlgorithmLearningTheme`, a Material 3 navigation bar) with an Android
  `MainActivity`/manifest and a Wasm `ComposeViewport` entry plus `index.html`.
- Added `apps/multiplatform/COMPOSE_GUIDE.md` covering architecture, layer
  responsibilities, DI, state/data flow, localization, testing, versions, and
  guardrails. Recorded the AGP 9 compatibility decision: `com.android.application`
  cannot be combined with the Kotlin Multiplatform plugin under AGP 9 defaults,
  so `gradle.properties` opts into the documented `android.builtInKotlin=false`
  and `android.newDsl=false` flags; splitting `composeApp` is a future change.
- Ignored `local.properties`, `.kotlin/`, and heap dumps in the root `.gitignore`.
- Task-limited verification passed exactly as contracted:
  `cd apps/multiplatform && ./gradlew :shared:allTests` (includes the
  `wasmJsBrowserTest` run); `cd apps/multiplatform && ./gradlew
  :composeApp:assembleDebug` (produced `composeApp-debug.apk`);
  `test -f apps/multiplatform/COMPOSE_GUIDE.md`; `git diff --check`.
- `CMP-001` is completed; `CMP-002` (shared auth/session data flow) is now ready.
  The closeout commit was not created in this pass because the working tree
  still contains the uncommitted `NEO-001`/`NEO-002` artifacts, and mixing them
  into a CMP commit was avoided.

## CMP-001 execution strategy — 2026-09-19

- Dependency reconciliation confirmed `CMP-001` has no dependencies; it advanced
  from `ready` to `in_progress`.
- Selected contract (already recorded by `$work-graph`): `three-perspectives` /
  `test-candidates` / `update-docs` / `infer`.
- Planner boundary: one Compose Multiplatform foundation whose `commonMain` UI
  builds for Android and Web. Implementer boundary: `apps/multiplatform` and the
  Compose guide only. Evaluator boundary: no DTO or repository reaches a
  composable, versions are pinned, and the exact four verification commands pass.

## NEO-002 closeout — 2026-09-19

- Retargeted `.github/workflows/gcp-production-deploy.yml` from Cloud SQL to
  Neon: the migration Job now uses `NEON_MIGRATION_JDBC_URL` and the service
  uses `NEON_SERVICE_JDBC_URL` with `&prepareThreshold=0`, both with
  `NEON_DATABASE_USERNAME`. Removed `--add-cloudsql-instances`, `--network`,
  `--subnet`, `--vpc-egress`, and the Cloud SQL socket factory; all SHA-pinned
  OIDC/serialization/immutable-image behavior is unchanged.
- Removed `com.google.cloud.sql:postgres-socket-factory` from
  `services/api/pom.xml`; it was runtime-scoped and unused by any source.
- Task-limited verification passed: the workflow static assertion, the pom
  assertion, and `git diff --check`. `NEO-002` is completed; `NEO-003` (first
  Neon release) is now ready but remains blocked on private operator setup.

## NEO-001 closeout — 2026-09-19

- Rewrote `infra/gcp/bootstrap.sh` to drop every Cloud SQL, VPC, subnet,
  private-service-access, and `roles/cloudsql.client` step while keeping API
  enablement, Artifact Registry, Secret Manager containers, the runtime/deploy
  service accounts, WIF, and least-privilege IAM. Rewrote `infra/gcp/README.md`
  for Neon connection variables, the pooled/direct endpoint split, and the
  first-deploy/rollback checklist.
- Task-limited verification passed: `bash -n infra/gcp/bootstrap.sh`,
  `--help`, the no-CloudSQL static assertion, and `git diff --check`. `NEO-001`
  is completed.

## Neon delivery work graph — 2026-09-19

- `$work-graph` created `specs/neon-cloud-run-delivery/plan.md` and `tasks.md`
  and appended `NEO-001`..`NEO-003` to `agent-workflow/WORK_GRAPH.yaml` with a
  readable graph in `WORK_GRAPH.md`.
- Superseded the Cloud SQL `GCP-004` first-release task: it is marked `blocked`
  and must not run; `NEO-003` is the replacement production-release task.

## Neon delivery spec governance — 2026-09-19

- `$spec-governance` selected `update-docs + infer` and created
  `specs/neon-cloud-run-delivery/spec.md` at intake mode `quick-analysis`.
- Locked the split: the serving API uses the Neon pooled endpoint, the Flyway
  migration Job uses the direct endpoint, and Cloud Run reaches Neon over public
  TLS with no VPC or Cloud SQL. Acceptance criteria `AC-NEO-01..07` cover the
  bootstrap, workflow, pooling constraint, IAM, readiness, and rollback.

## Neon credential incident and wizard fix — 2026-09-19

- The first wizard run accepted full Neon connection strings in the hostname
  fields, producing doubled `jdbc:postgresql://postgresql://...` URLs that
  embedded the Neon role password in GitHub production Variables. The two
  variables were overwritten with password-free host-only URL values.
- The user rotated the Neon role password and re-created the Secret Manager
  value. The stray repo-root `.env` that held the leaked value was removed.
- Hardened `scripts/neon-cloud-run-setup-wizard.sh`: hostname/identifier
  validators reject any scheme, credential, port, or path; empty values delete
  the GitHub variable instead of hanging on `gh`'s hidden prompt; and the
  `ENV_FILE` default bug that wrote to repo-root `.env` was fixed so it writes
  `infra/env/.env.neon`. A re-run completed without hanging and produced the
  correct file.

## Compose Multiplatform migration intake and governance — 2026-09-19

- Created `specs/compose-multiplatform-migration/spec.md`, `plan.md`, and
  `tasks.md` to replace the Flutter client with a Kotlin Compose Multiplatform
  client on Android/Web (then iOS/Desktop), mirroring the ALG-011..015 behavior.
- Appended `CMP-001`..`CMP-008` to `agent-workflow/WORK_GRAPH.yaml` and
  `WORK_GRAPH.md`; `CMP-001` is ready. A Compose-specific engineering guide is
  an explicit prerequisite deliverable (`AC-CMP-02`). No Compose code was
  written in this pass.


## GCP-003 closeout — 2026-09-10

- Added `.github/workflows/gcp-production-deploy.yml`: a SHA-pinned,
  serialized `main` production workflow with minimal deployment-job OIDC
  permission. It verifies the API, publishes an immutable Git-SHA Artifact
  Registry image through WIF, deploys and waits for one Cloud Run migration
  Job, then deploys that exact image as a Flyway-disabled public API service
  and checks Actuator readiness.
- Added the PostgreSQL Cloud SQL Java Socket Factory runtime dependency, so the
  workflow's private-IP JDBC URL works with the runtime service account and
  Direct VPC egress without embedding credentials or a database endpoint in the
  image. Runtime secrets remain named Secret Manager references only.
- Expanded `infra/gcp/README.md` with protected Environment/WIF prerequisites,
  first-deploy checks, image/revision evidence expectations, readiness command,
  and a forward-only rollback procedure that returns traffic to a recorded
  known-good revision without reversing Flyway migrations.
- Task-limited verification passed: the required workflow static assertion and
  `git diff --check` both exited successfully. `GCP-003` is completed.
  `GCP-004` is the next phase and remains blocked on private operator setup of
  the GitHub Environment and Secret Manager values; no cloud-mutating command
  was run for GCP-003.

## GCP-003 execution strategy — 2026-09-10

- Dependency reconciliation confirms that `GCP-001` and `GCP-002` are
  completed, so `GCP-003` advanced from `ready` to `in_progress`.
- Selected contract: `three-perspectives` / `test-candidates` /
  `update-docs` / `infer`.
- Planner boundary: preserve the existing constrained WIF and private Cloud SQL
  design while serializing releases around one migration Job. Implementer
  boundary: the production workflow, Cloud SQL JDBC connector dependency, and
  rollout/rollback documentation only. Evaluator boundary: all actions are
  commit-SHA pinned, no long-lived credential or secret value is introduced,
  the Job completes before the Flyway-disabled service deploys, and rollback
  never reverses Flyway migrations.

## GCP-002 closeout — 2026-09-10

- Added the explicit `APP_MIGRATION_ONLY=true` startup mode. It switches the
  Spring Boot process to `WebApplicationType.NONE`, lets Flyway initialize the
  configured datasource, then exits through Spring's exit-code path; normal API
  startup remains Servlet-based.
- Added TDD coverage for both migration and normal startup mode selection, and
  documented the Cloud Run Job/service environment split in the API and GCP
  operator documentation. The serving service will set
  `SPRING_FLYWAY_ENABLED=false`; only the Job enables Flyway.
- Task-limited verification passed: `cd services/api && ./mvnw -q test
  -Dtest='*MigrationModeTest'`; `cd services/api && ./mvnw -q test`. The full
  suite exited successfully while emitting the expected Docker-unavailable
  Testcontainers diagnostic for its Docker-optional fixture.
- `GCP-002` is completed. Both foundations are now complete, so `GCP-003` is
  ready for the continuous-delivery phase.

## GCP-002 execution strategy — 2026-09-10

- `GCP-002` advanced from `ready` to `in_progress`; it has no dependencies.
- Selected contract: `three-perspectives` / `tdd` / `update-docs` / `infer`.
- Planner boundary: the Cloud Run Job uses an explicit application mode rather
  than relying on incidental non-web JVM termination. Implementer boundary:
  test-first bootstrap behavior and backend operational documentation only.
  Evaluator boundary: migration mode must preserve normal API startup, avoid an
  HTTP listener, and use Spring's exit path only after Flyway has initialized.

## GCP-001 closeout — 2026-09-10

- Added `infra/gcp/bootstrap.sh` with a safe default `--plan` and an explicit
  `--apply`. The script defines idempotent GCP API, custom VPC/private-service
  access, private-IP PostgreSQL 16 Cloud SQL, Artifact Registry, empty Secret
  Manager containers, dedicated runtime/deployer accounts, least-privilege IAM,
  and GitHub repository/branch/environment-constrained WIF resources.
- Added `infra/gcp/README.md`, which separates operator-only secret-value and
  GitHub Environment setup from the script. Neither artifact reads, prints,
  accepts, or commits a credential or secret value.
- Task-limited verification passed exactly as contracted: `bash -n
  infra/gcp/bootstrap.sh`; `bash infra/gcp/bootstrap.sh --help`; and `git diff
  --check`.
- `GCP-001` is completed. `GCP-002` remains ready in the same Delivery
  foundations phase; `GCP-003` still waits for both foundations.

## GCP-001 execution strategy — 2026-09-10

- `GCP-001` advanced from `ready` to `in_progress` after dependency
  reconciliation confirmed it has none.
- Selected contract: `three-perspectives` / `test-candidates` / `update-docs`
  / `infer`.
- Planner boundary: a transparent, idempotent bootstrap plan/apply interface
  that never accepts or prints secret values. Implementer boundary: only
  `infra/gcp` bootstrap/runbook artifacts. Evaluator boundary: exact IAM/WIF
  scope, private Cloud SQL topology, documented manual secret entry, and no
  cloud-mutating command during this task.

## GCP Cloud Run delivery work graph — 2026-09-10

- `$work-graph` selected `three-perspectives` because GCP delivery crosses
  GitHub OIDC, IAM, Cloud SQL private networking, Spring Boot migration
  startup, and a production release boundary.
- Created `specs/gcp-cloud-run-delivery/plan.md`,
  `specs/gcp-cloud-run-delivery/tasks.md`, and the GCP dependency view in
  `agent-workflow/WORK_GRAPH.md`; appended `GCP-001` through `GCP-004` to the
  YAML source-of-truth graph.
- `GCP-001` (idempotent bootstrap/runbook) and `GCP-002` (migration-only API
  mode) are independently `ready`. `GCP-003` waits for both; `GCP-004` is the
  only cloud-mutating first-release task and waits for the completed workflow
  plus private user setup of GitHub Environment variables and Secret Manager
  values.
- No execution contract has been selected. Next action: choose one ready task
  with `$execution-strategy`; the recommended first task is `GCP-001`.

## GCP Cloud Run delivery spec governance — 2026-09-10

- `$spec-governance` selected `update-docs + infer` and advanced
  `gcp-cloud-run-delivery` to governed, ready for `$work-graph`.
- Locked the WIF condition to `allenljf/algorithm-learning`, `refs/heads/main`,
  and the `production` GitHub Environment. The WIF provider may impersonate
  only the dedicated GitHub deploy service account; no long-lived GCP key is
  allowed.
- Replaced service-startup migration ambiguity with an explicit single Cloud
  Run migration Job. It runs the API image non-web with Flyway enabled; the
  deployed API service disables Flyway, and production releases are serialized.
- Selected Cloud SQL PostgreSQL 16 with private IP, Direct VPC egress, seven-day
  backup/PITR retention, a dedicated runtime account, least-privilege secret/
  Cloud SQL access, and no Flutter browser CORS origin until a future hosting
  feature supplies one.
- `plan.md`, `tasks.md`, and a graph node remain absent. `$work-graph` is the
  next phase and must create them before any deployment implementation.

## GCP Cloud Run delivery workflow intake — 2026-09-10

- The user selected actual GCP delivery for project `alert-study-508214-s5`
  (project number `730295148186`) and approved the recommended Cloud Run +
  Cloud SQL + Artifact Registry + Secret Manager + GitHub OIDC/WIF design.
- Created `specs/gcp-cloud-run-delivery/spec.md` in `brainstorm` intake mode.
  It explicitly forbids long-lived service-account keys and agent access to
  secret values. The agreed identifiers are `asia-east1`, Artifact Registry
  repository `algorithm-learning`, and Cloud Run service
  `algorithm-learning-api`.
- `gcp-cloud-run-delivery` has no `plan.md`, `tasks.md`, or graph node yet.
  `$spec-governance` must confirm the WIF trust boundary, migration safety,
  IAM roles, and first-deploy assumptions before `$work-graph` creates
  implementation tasks.

## Spaced-repetition workflow intake — 2026-09-10

- The completed `algorithm-learning-platform` MVP has no ready task. Based on
  the explicit roadmap in `requirement.md`, `$workflow-intake` selected the
  next requirement as the post-MVP `spaced-repetition` feature in `brainstorm`
  mode.
- Created `specs/spaced-repetition/spec.md`. It proposes a deterministic,
  server-owned `adaptive-v1` policy; forward-only immutable-event snapshots;
  fixed-v1 history compatibility; and Flutter schedule context. Notifications,
  AI, user-configurable policies, offline scheduling, and historical rewrites
  are deliberately out of scope.
- `spaced-repetition` has no `plan.md`, `tasks.md`, or `WORK_GRAPH.yaml` node
  yet. Next workflow phase: `$spec-governance` must govern the policy,
  migration, API, and UX assumptions before `$work-graph` is allowed to create
  those artifacts.

## ALG-018 closeout — 2026-09-10

- Added Flutter's `integration_test` harness and a stateful, test-only
  controlled API adapter. The Android integration journey proves problem
  creation, edit and browse behavior, ordered Review Mode disclosure, and
  confidence submission without adding test behavior to production widgets.
- Added `infra/acceptance/README.md`, which records the release-evidence
  boundary for the API, Flutter, Compose, and Flutter-guide checks. It also
  explicitly scopes owner isolation to the API/repository suite rather than
  falsely emulating authorization in Flutter.
- Task-limited verification passed exactly as contracted: `cd services/api &&
  ./mvnw -q verify`; `cd apps/learning_app && flutter analyze`; `cd
  apps/learning_app && flutter test`; `cd apps/learning_app && flutter test
  integration_test`; `python3 flutter-dev-guide/tools/check-rules.py --staged`;
  `docker compose -f infra/compose.yaml up -d --build`; `docker compose -f
  infra/compose.yaml ps` (API and PostgreSQL healthy); and `docker compose -f
  infra/compose.yaml down`.
- `ALG-018` is completed. All planned MVP tasks are complete; there is no next
  ready task.

## ALG-018 execution strategy — 2026-09-10

- Dependency reconciliation confirmed that ALG-010, ALG-013, ALG-014, ALG-015,
  ALG-016, and ALG-017 are completed. ALG-018 advanced from the stale
  `pending` graph state to `in_progress`.
- Selected contract: `three-perspectives` / `tdd` / `update-docs` / `infer`.
  Planner scope: a controlled integration boundary plus evidence for the MVP
  acceptance journeys. Implementer scope: test-first key Flutter journeys and
  release evidence only. Evaluator boundary: keep provider overrides/fakes at
  the integration harness seam, verify owner isolation and operations via the
  existing API and Compose suites, and run only ALG-018's listed checks.

## ALG-017 execution strategy — 2026-09-10

- `$work-graph` synchronization advanced ALG-017 from `pending` to `ready`
  because ALG-016 is completed.
- Selected contract: `single-agent` / `rapid` / `update-docs` / `infer`; status
  is now `in_progress`. The target architecture is already locked by the spec,
  and this task creates documentation only—no Kubernetes manifests, provider
  choice, or production database deployment are authorized.

## ALG-017 closeout — 2026-09-10

- Added the provider-neutral Kubernetes target document: stateless API
  Deployment, ClusterIP Service, TLS Ingress, ConfigMap/Secret boundaries,
  Actuator startup/liveness/readiness probes, a single Flyway migration Job,
  managed PostgreSQL backups and point-in-time recovery, plus rollout and
  rollback boundaries.
- No Helm chart, Kustomize overlay, production manifest, cloud-provider choice,
  or in-cluster production PostgreSQL configuration was added.
- Task-limited verification passed: `test -f infra/kubernetes/README.md` and
  `git diff --check`.
- `ALG-017` is completed. All dependencies for `ALG-018` are now complete, so
  it is ready for the release-acceptance phase.

## ALG-016 work-graph and execution strategy — 2026-09-10

- `$work-graph` synchronization confirmed that all four dependencies
  (`ALG-002`, `ALG-004`, `ALG-005`, and `ALG-010`) are completed, so ALG-016
  advanced from `pending` to `ready`.
- Selected contract: `three-perspectives` / `test-candidates` /
  `update-docs` / `infer`; status is now `in_progress`.
- Planner: Compose remains a two-service local topology with PostgreSQL as the
  sole durable store and Flyway running once at API startup. Implementer: add a
  reproducible API image, named DB volume, startup/readiness health checks, and
  an ignored local environment file workflow. Evaluator: no secrets enter git,
  API logs retain their existing redaction policy, and the documented lifecycle
  must satisfy every task-limited Compose command.

## ALG-016 recovery evidence — 2026-09-10

- The first required verification command,
  `docker compose -f infra/compose.yaml config`, could not start because this
  host's Docker CLI has no Compose plugin: it returned `unknown shorthand flag:
  'f' in -f`. `docker-compose` is also absent, while the Docker daemon itself
  is healthy (`29.5.2`, `overlayfs`).
- This is an unavailable local verification dependency, not a Compose-file or
  application failure. ALG-016 remains `in_progress`; no task closeout or
  commit was made. Once Docker Compose v2 is installed, rerun the four exact
  task commands in order, beginning with `docker compose -f infra/compose.yaml
  config`.

## ALG-016 recovery attempt 1 — 2026-09-10

- Docker Compose v2.5.5.1 was installed with Homebrew and configured as a Docker
  CLI plugin. `docker compose -f infra/compose.yaml config` then passed, and
  PostgreSQL became healthy during `up -d --build`.
- Diagnosis: the API stopped before its readiness check because Hibernate schema
  validation expected `varchar` for `users.email`, while the Flyway-owned schema
  correctly uses PostgreSQL `citext`.
- Change: declare the JPA email column as PostgreSQL `citext`, matching the
  existing migration and preserving database-managed case-insensitive email
  uniqueness. The next attempt will rebuild the API and rerun the exact Compose
  commands.

## ALG-016 recovery attempt 2 — 2026-09-10

- The rebuilt API passed Flyway and schema validation but then failed during
  transaction-proxy creation: Spring cannot subclass a `final` application
  service containing `@Transactional` methods.
- Change: make the transaction-owning Auth, Tag, Problem, Solution, and Review
  application services proxyable. This preserves their public contracts and
  allows Spring's class-based transaction proxies to apply at the declared
  application-service boundary.

## ALG-016 closeout — 2026-09-10

- Delivered `infra/compose.yaml`, a multi-stage Java 21 API image, an ignored
  local environment-file workflow with a tracked non-secret example, and local
  operational documentation. Compose uses an isolated application network, a
  named PostgreSQL volume, PostgreSQL/API health checks, and a dependency gate
  so Flyway runs once at API startup after PostgreSQL is healthy.
- Recovery exposed and corrected two production-startup seams: Hibernate now
  declares `users.email` as PostgreSQL `citext`, and transaction-owning
  application services are proxyable by Spring.
- Task-limited verification passed exactly as contracted:
  `docker compose -f infra/compose.yaml config`; `docker compose -f
  infra/compose.yaml up -d --build`; `docker compose -f infra/compose.yaml ps`
  (both services healthy); and `docker compose -f infra/compose.yaml down`.
- The user supplied a repository-local Git identity and the required commit was
  created. `ALG-016` is completed; `ALG-017` is now ready in the same
  operations phase.

## ALG-015 closeout — 2026-09-02

- Added provider-injected dashboard/home states for loading, retry, no-data and
  populated totals, difficulty distribution, and due-review count.
- Task-limited verification passed: `flutter analyze`; `flutter test
  test/features/dashboard/presentation`.
- `ALG-015` is completed.

## ALG-015 execution strategy — 2026-09-02

- Selected contract: `three-perspectives` / `tdd` / `update-docs` / `infer`.

## ALG-014 closeout — 2026-09-02

- Added a `problemId` review route and provider-injected review screen. Its
  disclosure state permits only the ordered Problem → Think → Hint → My
  Approach → Solution → confidence progression; review submission is disabled
  until a confidence is selected after Solution is revealed.
- Task-limited verification passed: `flutter analyze`; `flutter test
  test/features/reviews test/features/problems/presentation`.
- `ALG-014` is completed. `ALG-015` remains ready.

## ALG-014 execution strategy — 2026-09-02

- Selected contract: `three-perspectives` / `tdd` / `update-docs` / `infer`.
- Planner: route `problemId` into a family/provider input and preserve already
  revealed stages locally. Implementer: establish stage and submit behavior with
  focused red-green widget tests. Evaluator: confidence must remain unavailable
  until Solution is revealed and no solution/review transport reaches widgets.

## ALG-013 closeout — 2026-09-02

- Implemented the Web problem-management experience through an injectable
  repository/provider boundary: remote list/search/filter/pagination state,
  create/edit/delete with confirmation and retained validation drafts, owned tag
  creation/assignment, ordered problem detail, and independent solution
  create/delete actions.
- Widget coverage proves loading/error/empty/data states, a recoverable server
  validation error retaining the draft, independent solution save, and delete
  confirmation. Task-limited verification passed: `flutter analyze`; `flutter
  test test/features/problems/presentation`.
- `ALG-013` is completed. `ALG-014` and `ALG-015` remain independently ready.

## ALG-012 closeout — 2026-09-02

- Added typed, transport-agnostic remote/repository contracts for problem list,
  tags, solutions, review history, and dashboard, using immutable domain models
  and no durable problem-data cache.
- Task-limited verification passed: `flutter analyze`; `flutter test
  test/features/problems test/features/reviews test/features/dashboard`.
- `ALG-012` is completed. ALG-013, ALG-014, and ALG-015 are now ready.

## ALG-012 execution strategy — 2026-09-02

- Selected contract: `three-perspectives` / `tdd` / `update-docs` / `infer`.
- Scope: typed remote/repository boundaries for the library, immutable domain
  models, explicit failure/cancellation seams, fakes/provider seams, and no cache.

## ALG-011 closeout — 2026-09-02

- Added transport-agnostic auth session/repository contracts and an auth state
  notifier with a coalesced refresh seam, keeping the access session in memory.
- Task-limited verification passed: `flutter analyze`; `flutter test test/features/auth`.
- `ALG-011` is completed; ALG-012 is now ready for its own execution contract.

## ALG-011 execution strategy — 2026-09-02

- Selected contract: `three-perspectives` / `tdd` / `update-docs` / `infer`.
- Scope: transport-agnostic auth repository, memory-only access session,
  single-flight refresh seam, auth notifier, and route state; status is in progress.

## ALG-010 closeout — 2026-09-02

- Added owner-scoped dashboard total/difficulty/due aggregates and a
  `/api/v1/dashboard` endpoint. Empty libraries return explicit zero counts.
- Task-limited verification passed:
  `cd services/api && ./mvnw -q test -Dtest='*DashboardTest,*DashboardControllerTest'`.
- `ALG-010` is completed; ALG-016 is now dependency-ready, while ALG-011
  remains independently ready.

## ALG-010 execution strategy — 2026-09-02

- Selected contract: `three-perspectives` / `tdd` / `update-docs` / `infer`.
- Scope: owner-scoped read-only PostgreSQL aggregation for dashboard counts,
  latest-review state, deterministic weak topics, recent/random rows, and no-data.

## ALG-009 closeout — 2026-09-02

- Added fixed-v1 immutable review events with 1/2/4/7/14-day confidence
  intervals, owner-scoped submission, due retrieval, and newest-first history.
- The API exposes review creation plus today/history routes; foreign problems
  are non-disclosing and no review mutation/deletion endpoint is present.
- Task-limited verification passed:
  `cd services/api && ./mvnw -q test -Dtest='*ReviewPolicyTest,*ReviewTest,*ReviewControllerTest'`.
- `ALG-009` is `completed`; ALG-010 is now ready. ALG-011 remains independently ready.

## ALG-009 execution strategy — 2026-09-02

- Selected contract: `three-perspectives` / `tdd` / `update-docs` / `infer`.
- Planner: review state is derived from immutable events, never stored on a
  mutable problem record. Implementer: start with fixed-v1 policy tests using
  injected Clock/UUID, then owner-scoped submission/due/history tests.
- Evaluator: foreign problem IDs return `404`; no update/delete route is added;
  dashboard aggregation remains ALG-010 work.
- Status is `in_progress`; task verification is
  `cd services/api && ./mvnw -q test -Dtest='*ReviewPolicyTest,*ReviewTest,*ReviewControllerTest'`.

## ALG-008 closeout — 2026-09-02

- Added independent owner-scoped Solution CRUD beneath owned problems and at
  direct solution routes. Solution lookups join through the owning problem, so
  foreign identifiers remain non-disclosing.
- Validation permits only Kotlin, Java, Python, or Dart; preserves code text,
  trims optional explanations, limits content sizes, and requires non-blank
  code or explanation. Lists use stable `created_at, id` ordering.
- Task-limited verification passed:
  `cd services/api && ./mvnw -q test -Dtest='*SolutionTest,*SolutionControllerTest'`.
- `ALG-008` is `completed`. `ALG-009` and independent `ALG-011` remain ready;
  select one through `$execution-strategy` before implementation.

## ALG-008 execution strategy — 2026-09-02

- Selected contract: `three-perspectives` / `tdd` / `update-docs` / `infer`.
- Planner perspective: keep solution CRUD independent from Problem replacement
  and expose it only through the specified nested/list and direct solution
  routes.
- Implementer perspective: use focused red-green tests for owner-scoped parent
  and solution lookups, language/content validation, stable creation ordering,
  and delete behavior.
- Evaluator perspective: confirm no Problem write schema is expanded with
  solution fields and that unowned identifiers remain non-disclosing `404`s.
- Status is now `in_progress`; implementation begins with failing solution
  domain/application tests. The task-limited verification remains
  `cd services/api && ./mvnw -q test -Dtest='*SolutionTest,*SolutionControllerTest'`.

## ALG-007 closeout — 2026-09-02

- Completed owner-scoped Problem create/read/replace/delete infrastructure with
  normalized write fields, HTTPS platform URL policy, and non-disclosing absent
  resources.
- Problem writes now replace supplied owner-owned tag links within the problem
  transaction; foreign, duplicate, or oversized tag selections are rejected.
- `GET /api/v1/problems` exposes the specified page envelope, deterministic
  sorting, combined text/difficulty/platform/tag/review-status filters, and
  PostgreSQL full-text search with `LIMIT/OFFSET`. Problem response schemas now
  include tag summaries, a never-reviewed summary, and an explicit empty
  solutions list pending ALG-008/ALG-009 implementations.
- Added focused TDD coverage for tag replacement/owner isolation plus real
  PostgreSQL Testcontainers coverage for owner-scoped full-text/tag-AND query
  behavior.
- Task-limited verification passed:
  `cd services/api && DOCKER_HOST=unix:///Users/allen/.colima/default/docker.sock TESTCONTAINERS_RYUK_DISABLED=true ./mvnw -q test -Dtest='*ProblemTest,*ProblemControllerTest,*ProblemRepositoryIntegrationTest'`.
- `ALG-007` is `completed`; `ALG-008` and `ALG-009` now have all dependencies
  complete and are ready for separate execution-strategy selection. `ALG-011`
  remains ready and independent.

## ALG-007 execution strategy — 2026-09-02

- Selected contract: `three-perspectives` / `tdd` / `update-docs` / `infer`.
- Scope: owner-scoped Problem CRUD, write normalization and validation, tag-link
  replacement, PostgreSQL search/filter/pagination, deterministic sorting, and
  URL platform policy. Solutions and review summaries remain later task work.
- Status is now `in_progress`; tests will first establish URL policy, owner
  isolation/non-disclosing absence, and write/list behavior.

## ALG-007 implementation checkpoint — 2026-09-02

- Completed red-green cycles for HTTPS/platform-host URL policy, owner-scoped
  replacement/non-disclosing lookup, and the versioned controller path.
- Added uncommitted Problem domain/application/JPA/controller foundations:
  write-field trimming and size checks, owner-scoped create/read/replace/delete,
  basic title/description/difficulty/platform list filtering, and stable
  `updated_at desc, id desc` repository order.
- Focused command currently passes:
  `cd services/api && ./mvnw -q test -Dtest='*ProblemTest,*ProblemControllerTest,*ProblemRepositoryIntegrationTest'`.
- Remaining before closeout: implement owned tag-link validation/replacement;
  move combined filters/search/pagination/sort behavior into PostgreSQL queries;
  add real PostgreSQL repository integration coverage; make response schemas
  include required tag/review summary fields without taking solution/review
  behavior from later tasks. Do not commit the partial task.

## ALG-006 closeout — 2026-09-02

- Added an owner-scoped tag aggregate, repository port and JPA adapter, plus
  authenticated `GET`/`POST /api/v1/tags` endpoints.
- Tag names use Unicode NFKC, trim, collapsed internal whitespace, and
  lowercase comparison keys. The `(user_id, normalized_name)` persistence
  boundary makes the normalized uniqueness owner-local; duplicate creates return
  the existing tag as `200`, while a new tag returns `201` and `Location`.
- TDD coverage proves normalization, owner isolation, duplicate idempotency, and
  the controller's HTTP result boundary.
- Task-limited verification passed: `cd services/api && ./mvnw -q test -Dtest='*TagTest,*TagControllerTest'`.
- `ALG-006` is `completed`; `ALG-007` is now `ready`. ALG-011 remains ready and
  is independent at the workflow level.

## ALG-006 execution strategy — 2026-09-02

- Selected contract: `three-perspectives` / `tdd` / `update-docs` / `infer`.
- Scope: user-owned tag normalization, idempotent creation, owned listing/search,
  and its security/API boundaries only. This task defers problem-tag assignment
  and all problem behavior to ALG-007.
- Status is now `in_progress`; tests will establish NFKC whitespace/case-fold
  normalization, owner isolation, and duplicate-create behavior before the
  application, JPA, and HTTP layers are added.

## ALG-005 closeout — 2026-09-01

- Completed the authentication and ownership foundation: Argon2id password
  hashing at OWASP's 19 MiB / two-iteration / one-lane minimum profile; signed
  15-minute HS256 JWTs; opaque 30-day refresh values persisted only as keyed
  hashes; one-time refresh rotation and logout revocation.
- Added JPA adapters for users and refresh sessions, a bearer authentication
  filter plus `CurrentUser` owner identity seam, and all required
  `/api/v1/auth` register/login/refresh/logout/me endpoints. Refresh cookies are
  `Secure`, `HttpOnly`, `SameSite=Lax`, and origin checks protect cookie-backed
  refresh/logout. Registration, login, refresh, and password verification use a
  replaceable IP/account-key rate-limit seam.
- Added focused red-green tests for Argon2id behavior and refresh rotation; the
  pre-existing JWT, password-policy, and keyed refresh-token tests remain green.
- Task-limited verification passed: `cd services/api && ./mvnw -q test -Dtest='*AuthTest,*SecurityTest'` and `cd services/api && ./mvnw -q verify`.
  The latter emitted the expected Testcontainers no-Docker diagnostic, while its
  Docker-optional migration fixture was skipped and Maven exited successfully.
- `ALG-005` is `completed`. `ALG-006` and `ALG-011` now have all dependencies
  complete and are ready for separate execution-strategy selection.

## ALG-004 closeout — 2026-09-01

- Added Flyway V1 for the `citext` extension and all initial product tables:
  users, auth sessions, problems, solutions, tags, problem tags, and reviews.
  It includes the required checks, indexes, ownership cascades, UTC timestamps,
  mutable-table update triggers, and a GIN full-text index over problem text
  only.
- Added a real PostgreSQL Testcontainers migration test. It proves empty-schema
  migration, key database checks, and user-owned record cascades.
- Task-limited verification passed: `cd services/api && DOCKER_HOST=unix:///Users/allen/.colima/default/docker.sock TESTCONTAINERS_RYUK_DISABLED=true ./mvnw -q test -Dtest='*MigrationTest,*RepositoryIntegrationTest'`.
- `ALG-004` is `completed`; `ALG-005` is now `ready`.

## ALG-005 execution strategy — 2026-09-01

- Selected contract: `three-perspectives` / `tdd` / `update-docs` / `infer`.
- Scope: test-first authentication and ownership infrastructure only—Argon2id,
  15-minute JWT, opaque 30-day rotating refresh sessions, cookie/origin and
  rate-limit seams, and user-scoped persistence/security wiring. Product library
  behavior remains assigned to later tasks.
- Status is now `in_progress`; tests will first cover authentication outcomes and
  ownership boundaries. Argon2id will use OWASP's current 19 MiB / 2 iteration /
  1 parallelism minimum profile.

## ALG-005 implementation checkpoint — 2026-09-01

- Began the required TDD cycle with focused `*AuthTest` / `*SecurityTest`
  coverage. The initial tests correctly failed because `PasswordPolicy`,
  `RefreshTokenService`, and `JwtTokenService` did not exist.
- Added only the corresponding domain primitives so far: 12–128-character
  password boundary validation, 256-bit opaque refresh values with HMAC-SHA-256
  keyed persistence hashes and constant-time comparison, plus HS256 access JWT
  issuance/verification containing issuer, audience, subject, issued-at,
  expiry, and token ID claims. JWT lifetime is exactly 15 minutes.
- Evidence: `cd services/api && ./mvnw -q test -Dtest='*AuthTest,*SecurityTest'`
  passes. ALG-005 remains `in_progress`; Argon2id configuration, JPA/JDBC
  persistence, session rotation, endpoints, cookie/origin validation, rate-limit
  seam, bearer filter, ownership repository foundation, task verification, and
  closeout have not yet been implemented.

## ALG-004 recovery attempt 1 — 2026-09-01

- Diagnosis: the initial Testcontainers migration test could not be compiled
  because Spring Boot 4.1.1 does not manage Testcontainers dependency versions.
- Change: import the Testcontainers 2.0.5 BOM and use its v2 artifact IDs
  (`testcontainers-junit-jupiter`, `testcontainers-postgresql`) for test-scoped
  dependencies.
- Command: `cd services/api && ./mvnw -q test -Dtest='*MigrationTest,*RepositoryIntegrationTest'`.
- Result: after dependency correction, the test reached the intended missing-schema red state.

## ALG-004 migration implementation adjustment — 2026-09-01

- Diagnosis: PostgreSQL rejected the initial full-text GIN expression because
  `concat_ws` is not immutable.
- Change: replace it with an explicitly immutable `problem_search_vector`
  function that indexes only the specified problem text fields.
- Command: `cd services/api && DOCKER_HOST=unix:///Users/allen/.colima/default/docker.sock TESTCONTAINERS_RYUK_DISABLED=true ./mvnw -q test -Dtest='*MigrationTest,*RepositoryIntegrationTest'`.
- Result: reran successfully after the immutable-vector correction.

## ALG-004 recovery attempt 2 — 2026-09-01

- Diagnosis: Testcontainers 2.0.5's PostgreSQL container is managed by the
  JUnit 5 `@Container` annotation, not `@RegisterExtension`.
- Change: use the Testcontainers JUnit 5 container annotation on the real
  PostgreSQL test fixture.
- Command: `cd services/api && ./mvnw -q test -Dtest='*MigrationTest,*RepositoryIntegrationTest'`.
- Result: the fixture then reached Docker discovery; the initial default socket
  was unavailable.

## ALG-004 recovery attempt 3 — 2026-09-01

- Diagnosis: the default Docker socket is unavailable, but the local Colima
  context is healthy at `/Users/allen/.colima/default/docker.sock`; Colima
  cannot mount that macOS socket into Testcontainers' Ryuk cleanup container.
- Change: no repository change; run the task's exact Maven verification against
  the available Docker endpoint with Ryuk disabled for this local test process.
- Command: `cd services/api && DOCKER_HOST=unix:///Users/allen/.colima/default/docker.sock TESTCONTAINERS_RYUK_DISABLED=true ./mvnw -q test -Dtest='*MigrationTest,*RepositoryIntegrationTest'`.
- Result: with the available Colima endpoint and Ryuk disabled, the test reached
  the intended missing-schema red state before V1 was added.

## ALG-003 closeout — 2026-09-01

- Created Flutter 3.47.0 Web/Android/iOS app root with Riverpod, GoRouter,
  Material 3 light/dark themes, English and Traditional Chinese locale support,
  and a widget smoke test.
- Task-limited verification passed: `flutter analyze`; `flutter test`.
- `ALG-003` is `completed`; `ALG-004` remains `ready`.

## ALG-002 closeout — 2026-09-01

- Established the Java 21 Spring Boot 4.1.1 modular-monolith composition root
  with Maven Wrapper 3.9.16, feature package boundaries, Web/JPA/Security/
  Validation/Flyway/Actuator dependencies, PostgreSQL runtime support,
  non-secret configuration binding, request-ID propagation, Problem Details
  error seam, and candidate configuration/filter tests.
- Recovery attempt 1 fixed only a candidate-test compile error caused by the
  generic `ServletResponse` type exposed by `FilterChain`; the request-ID
  assertion now uses the concrete mock response after the chain returns.
- Task-limited verification passed: `cd services/api && ./mvnw -q test` and
  `cd services/api && ./mvnw -q package -DskipTests`.
- `ALG-002` is `completed`. `ALG-003` and `ALG-004` are both `ready`; neither
  initially had an execution contract.
- `ALG-003` 已由 `$execution-strategy` 選定 contract：
  `three-perspectives` / `test-candidates` / `update-docs` / `infer`。實作
  限於 Flutter application composition root、Material 3 light/dark theme、
  英文與繁體中文 localization、單一 GoRouter、ProviderScope、feature-first
  directories 與 app-root widget tests；auth、API、repository 與產品畫面仍由
  後續 task 負責。
- 下一步：使用 `$task-execute` 依上述 contract 執行 `ALG-003`。

## ALG-002 recovery attempt 1 — 2026-09-01

- Diagnosis: `./mvnw -q test` failed during test compilation because the
  `FilterChain` lambda exposes its second parameter as `ServletResponse`, which
  has no `getHeader` method. The failure occurred before application startup or
  test execution.
- Change: moved the request-ID equality assertion outside the lambda, where the
  concrete `MockHttpServletResponse` is available; the test still verifies the
  same request-to-response propagation behavior.
- Result: reran `./mvnw -q test`; it passed. Next command is the remaining
  task-limited verification: `./mvnw -q package -DskipTests`.

## ALG-001 closeout — 2026-09-01

- 已完成 monorepo/toolchain skeleton：建立 `apps/learning_app`、`services/api`
  與 `infra` 的 tracked entry-point READMEs、非機密
  `infra/env/.env.example`、root `.gitignore`，並鎖定 Java 21 和 Flutter
  3.47.0。
- 僅執行 task 限定驗證，且全部通過：`git diff --check`、
  `test -d apps/learning_app`、`test -d services/api`、
  `test -f infra/env/.env.example`。
- `ALG-001` 已標記 `completed`。`ALG-002` 與 `ALG-003` 的所有依賴均已完成，
  因此已標記為 `ready`。
- `ALG-002` 已由 `$execution-strategy` 選定 contract：
  `three-perspectives` / `test-candidates` / `update-docs` / `infer`。實作
  限於 Spring Boot modular-monolith composition root、非機密 configuration
  binding、request-ID/error seams 與測試 harness；migrations、auth、REST
  resources 和 Compose 仍由後續 task 負責。目前 status 為 `in_progress`。

## Work-graph planning — 2026-09-01

- 已使用 `$work-graph`（`three-perspectives` 規劃深度）建立
  `specs/algorithm-learning-platform/plan.md`、
  `specs/algorithm-learning-platform/tasks.md` 與可讀 dependency 視圖
  `agent-workflow/WORK_GRAPH.md`。
- `agent-workflow/WORK_GRAPH.yaml` 現為產品 task 相依與狀態來源真相，包含
  `ALG-001` 至 `ALG-018`；每個節點已記錄 deliverable、`depends_on`、
  `parallel_group`、`spec_refs` 與限定 verification。
- 只有 `ALG-001`（monorepo/toolchain skeleton）為 `ready`；所有後續 task
  維持 `pending`，直到圖上的依賴完成。尚未開始產品程式碼或產品驗證。
- `ALG-001` 已由 `$execution-strategy` 選定 contract：`single-agent` /
  `rapid` / `update-docs` / `infer`。實作僅限根目錄骨架、非機密設定範例、
  toolchain version metadata 與 entry-point README；不得提前建立任一產品
  composition root。目前 status 為 `in_progress`；將在實作後僅跑該 task
  已宣告的四項驗證。

## Product requirement baseline

- 已建立根目錄 `requirement.md`，整理 Algorithm Learning & Review Platform 的需求基線。
- 已於 2026-09-01 使用 `$workflow-intake` 的 `brainstorm` 模式建立正式規格：
  `specs/algorithm-learning-platform/spec.md`。
- 規格已涵蓋 MVP scope/non-goals、驗收條件、整體 architecture、monorepo 與
  Flutter/Backend 結構、ERD/schema、REST API、authentication、安全、Docker、
  Kubernetes target、測試策略、決策與假設。
- 已於 2026-09-01 使用 `$spec-governance` 的 `update-docs + infer` 路徑完成
  規格治理；正式規格狀態為 governed，已可交由 `$work-graph` 拆分。
- Backend 為已鎖定決策：Java + Spring Boot、PostgreSQL、Spring Web、Spring
  Data JPA、Spring Security、Bean Validation、Flyway 與 Actuator；不得改用
  其他後端語言或框架，除非使用者明確同意並更新規格。
- 尚未建立 feature plan、task list 或產品 task graph 節點；這三項須在規格治理
  通過後由 `$work-graph` 建立。
- Execution contract 尚未選定；應在有 ready task 後由 `$execution-strategy` 選定。

## Workflow setup

- 已建立 project-local harness、8 個 workflow skills 與 `AI_DEVELOPMENT_GUIDE.md`。
- 已通過每個 skill 的結構驗證，以及 `flutter-dev-guide` 的 rules self-check。
- 情境驗證確認：在「快速實作、不寫測試」下，workflow 仍要求完整 spec、plan、task graph、明確 execution contract 與 task 限定驗證；無 graph 不可直接實作。
- 已加入 `WORKFLOW_CONTINUATION.md` 共用契約；8 個 skills 都明確引用它，且結尾必須提供同對話指令與新對話 prompt。重測確認 prompt 不會要求讀取尚未建立的工件。

下一個建議動作是以 `$work-graph` 為 `algorithm-learning-platform` 建立 feature
plan、task list 與產品 task graph；不要直接開始產品程式碼實作。
