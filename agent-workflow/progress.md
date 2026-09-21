# Progress

## PHR-001 closeout — bilingual project handoff README — 2026-09-22

- Replaced the minimal root README with an English project handoff and added a
  Traditional-Chinese counterpart. Both cover local setup, Docker, Java/Spring
  architecture, Flyway tables and fields, REST routes, Compose modules/features,
  API-to-UI flow, user operations, AI workflow skills, deployment boundaries,
  and security guidance.
- Added GitHub Mermaid system, backend, Compose, request-data, and agent-workflow
  diagrams. The documents use only checked-in source/runbook facts and do not
  direct readers to removed Flutter tooling.
- Verification passed: both files exist; all local Markdown links resolve; both
  documents have the required heading depth, Mermaid diagrams, and workflow
  skill content; `git diff --check` passed.
- PHR-001 is completed. The user explicitly authorized committing the prior
  iOS/Firebase working-tree changes first; that separate commit was `cd9f584`.

## FRR-001 closeout — Flutter assets fully removed — 2026-09-22

- Deleted the retired Flutter client `apps/learning_app` and the entire
  Flutter-only `flutter-dev-guide`, including tracked sources and local ignored
  build artifacts. `apps/multiplatform` remains the sole client.
- Updated active entry points, workflow preflight guidance, the requirement
  baseline, Compose guide, migration record, and acceptance-evidence document
  to remove dead links and obsolete Flutter prerequisites. Historical completed
  task records remain as migration provenance only.
- Task-limited verification passed exactly as contracted: both removed paths are
  absent, `apps/multiplatform` exists, and `git diff --check` passed.
- FRR-001 is completed. No additional ready task was created.

## CDS-007 source-build governance revision — 2026-09-21

- `$spec-governance` selected `update-docs` + `infer` after the operator
  replaced Android APK publication with public source and local debug-build
  guidance. The Web showcase remains public at
  `https://allenljf-algorithm.web.app`; Android has no release channel,
  published APK, checksum, unknown-source download flow, or signing requirement.
  Existing optional signing wiring remains untouched and operator-only.
- `$work-graph` revised CDS-007 to document prerequisites, source checkout,
  debug assembly, local installation, endpoint confirmation, and source rebuild
  rollback. The previous missing-signing blocker is superseded; CDS-007 is now
  `ready` and requires a new execution contract. The protected iOS Xcode changes
  and Firebase local cache remain out of scope.

## CDS-007 execution strategy — source-build path — 2026-09-21

- Selected contract: `single-agent` / `rapid` / `update-docs` / `infer`.
  Scope: replace APK artifact guidance with an actionable Android source-build
  runbook only. Completion checklist: public source checkout, JDK/Android SDK
  prerequisites, debug assembly path, local installation and endpoint check,
  source rebuild rollback guidance, explicit no-artifact boundary, task-limited
  verification, and closeout without touching signing material, iOS Xcode
  changes, or the Firebase local cache.

## CDS-007 closeout — Android source-build showcase path — 2026-09-21

- Replaced the former APK-publication runbook with a public source-build path:
  JDK/Android SDK prerequisites, clone, debug assembly, `adb` installation,
  endpoint confirmation, and source revision rebuild rollback. It explicitly
  excludes any published APK, checksum, release channel, or unknown-source
  download flow. Optional signing wiring remains operator-only and untouched.
- Updated the client-distribution specification, plan, tasks, and work graph to
  make Android a source-built demonstration rather than a signed artifact.
  This supersedes the prior signing-configuration blocker; no signing material,
  APK artifact, GitHub Release, or external publish action is required.
- Task-limited verification passed exactly as contracted:
  `cd apps/multiplatform && ./gradlew :composeApp:assembleDebug` and `git diff
  --check`. Gradle emitted its existing KMP/AGP compatibility and deprecation
  warnings but exited successfully.
- CDS-007 is `completed`; client-distribution has no remaining task. The
  pre-existing local iOS Xcode changes and `.firebase/` cache were neither
  inspected, modified, staged, nor committed.

## CDS-007 execution strategy — 2026-09-21

- Selected contract: `three-perspectives` / `rapid` / `update-docs` / `infer`.
  Planner boundary: produce one versioned signed Android APK, its SHA-256, and
  a public non-Firebase download with a known-good rollback reference.
  Implementer boundary: use the existing Gradle signing seam and GitHub Release
  channel only; never read, print, stage, or modify the local keystore/signing
  configuration, iOS Xcode settings, or Firebase local cache. Evaluator
  boundary: the uploaded APK and checksum correspond to the same signed build,
  installation guidance is public, and only the task's exact verification runs.
- Completion checklist: a monotonic versioned signed APK is assembled; SHA-256
  is calculated from that file and uploaded beside it to a non-Firebase public
  release; the release notes identify version, checksum, install guidance, and
  rollback artifact; task-limited verification passes; closeout records public
  evidence and commits only task artifacts.

## CDS-007 terminal blocker — Android signing configuration unavailable — 2026-09-21

- The task's exact verification was run: `cd apps/multiplatform && ./gradlew
  :composeApp:assembleRelease` completed successfully, followed by `git diff
  --check` successfully. The only release APK output was
  `composeApp/build/outputs/apk/release/composeApp-release-unsigned.apk`.
- Cause: the ignored local `apps/multiplatform/android-release-signing.properties`
  file is absent; GitHub has no Android signing secrets or release-publishing
  workflow. The existing signing seam correctly leaves a release unsigned when
  no operator material is supplied. Publishing it would violate AC-CDS-04,
  AC-CDS-07, and AC-CDS-10, so no checksum, GitHub Release, tag, or artifact
  was created.
- CDS-007 is `blocked` only at the operator-controlled signing boundary. To
  resume, the operator must locally create the ignored signing-properties file
  from `apps/multiplatform/android-release-signing.properties.example` with the
  established release keystore references (or securely configure the four
  equivalent CI Gradle properties). Do not provide any signing value in chat.
  The next execution must choose a new monotonically increasing version code
  and name, build the signed APK, compute its SHA-256 from that exact file, and
  publish both to a public GitHub Release. The existing local iOS Xcode changes
  and `.firebase/` cache were neither inspected, modified, staged, nor committed.

## CDS-006 closeout — public Firebase/Cloud Run showcase deployed — 2026-09-21

- Completed the isolated `allenljf-algorithm` Firebase/GCP deployment without
  changing the existing `alert-study-508214-s5` production project. The active
  project is number `1062311115113`; Firebase Hosting serves
  `https://allenljf-algorithm.web.app`, with its `/api/**` rewrite targeting the
  `asia-east1` Cloud Run service `algorithm-learning-api`.
- Cloud Build published the API image to the project-local `algorithm-learning`
  Artifact Registry repository. The direct-Neon migration Cloud Run Job
  `algorithm-learning-migrate` completed successfully before the pooled-Neon
  serving service was released under the dedicated
  `algorithm-learning-runtime` identity. Runtime access to Secret Manager stays
  limited to the four named CDS-006 secret containers; no secret value was read,
  printed, or committed.
- Recovery: the Hosting rewrite preserves the `/api` prefix, so the actuator
  endpoint was configured beneath `/api/actuator` and Spring Security now
  permits its readiness path. The public readiness probe returns
  `{"status":"UP"}`.
- Public-browser evidence passed with a disposable non-personal test account:
  account creation reached Dashboard; a full page reload retained the signed-in
  Dashboard session; Sign out returned the application to the Sign in form.
- Required verification passed: `gcloud projects describe allenljf-algorithm`;
  `firebase hosting:sites:list --project allenljf-algorithm` (via the
  repository's `npx firebase-tools` wrapper); both required public `curl --fail`
  probes; and `git diff --check`. The user’s pre-existing uncommitted iOS Xcode
  signing settings were not inspected, modified, staged, or committed.
- CDS-006 is `completed`. CDS-007 is now `ready` and still needs its own
  `$execution-strategy` contract before work begins.

## CDS-006 resumed — private configuration verified — 2026-09-21

- The operator completed the private configuration wizard. Without reading any
  values, the agent verified that all four required Secret Manager containers
  now have enabled version `1`, that the ignored
  `infra/env/.env.showcase` configuration file exists, and that the dedicated
  runtime service account exists. CDS-006 returned to `in_progress` for the
  Cloud Run and Firebase Hosting deployment phase.

## CDS-006 private configuration wizard prepared — 2026-09-21

- Created the one-run, executable
  `scripts/cds-006-showcase-private-config-wizard.sh` from the project wizard
  template. It confirms access to `allenljf-algorithm`, accepts only bare Neon
  hostnames/identifiers, builds password-free JDBC URLs locally, reads the four
  secret values with hidden terminal input, streams them directly into the new
  project's existing Secret Manager containers, then unsets them. It writes
  only non-secret deployment values to the ignored
  `infra/env/.env.showcase` file.
- Static verification passed: `bash -n` succeeds and Git confirms the local
  configuration file is ignored. `shellcheck` is not installed. The wizard was
  not run by the agent because it requires the operator's Neon and secret
  values. CDS-006 remains blocked until the operator completes it and confirms
  success without disclosing any value.

## CDS-006 terminal recovery — showcase foundation complete, private configuration pending — 2026-09-21

- The operator authorized the same billing account used by the existing project.
  `allenljf-algorithm` is now linked to `billingAccounts/01EB1E-475316-280641`
  and billing is enabled. The agent then enabled Cloud Run, Artifact Registry,
  Secret Manager, Cloud Build, IAM Credentials, and STS; created the
  `asia-east1` Docker repository `algorithm-learning`; created
  `algorithm-learning-runtime@allenljf-algorithm.iam.gserviceaccount.com`; and
  created the four empty required Secret Manager containers. The runtime
  identity has `roles/secretmanager.secretAccessor` only on those four named
  containers, not project-wide Secret Manager access.
- The remaining Cloud Run migration Job and serving service cannot be safely
  deployed without operator-provided private values: current JWT and refresh
  hash key versions; runtime and migration Neon role password versions; and the
  password-free direct and pooled Neon JDBC URLs plus their role names. The
  agent did not read or copy any values from the existing production project.
  Hosting is intentionally not deployed before its `/api/**` target can become
  healthy.
- CDS-006 is `blocked` at the required private configuration boundary; CDS-007
  remains pending. Existing production resources and the user's local iOS
  signing settings remain unchanged.

## CDS-006 terminal recovery — billing selection required — 2026-09-21

- The new `allenljf-algorithm` Firebase/GCP project was created successfully by
  the authenticated operator account. `gcloud projects describe` reports active
  project number `1062311115113`, and Firebase Hosting created the default site
  at `https://allenljf-algorithm.web.app`. This establishes the governed
  canonical origin without changing the existing production project.
- Cloud billing is not enabled (`billingAccountName` empty, `billingEnabled:
  false`), so Cloud Run, Artifact Registry, and Secret Manager cannot yet be
  provisioned. The account can access one open billing account,
  `01EB1E-475316-280641` (display name `我的帳單帳戶`), but binding it can incur
  charges and requires the operator's explicit confirmation. No billing account,
  service API, Cloud Run service, secret, Neon configuration, or iOS signing
  setting was changed.
- CDS-006 is `blocked` pending authorization to link that billing account and
  the later private runtime configuration. CDS-007 remains pending.

## CDS-006 canonical-origin governance revision — 2026-09-21

- `$spec-governance` selected `update-docs` + `infer` after the operator
  explicitly replaced the unavailable `algorithmlearning` ID with
  `allenljf-algorithm`. The canonical public origin is now
  `https://allenljf-algorithm.web.app`; it is the Wasm production API base URL
  and the exact sole browser origin allowed by the showcase API. The Firebase
  project mapping, plan, task verification, work graph, and Hosting runbook now
  use this new project identity. Historical attempts against the globally
  allocated `algorithmlearning` ID remain recorded below as evidence.
- CDS-006 returned to `in_progress`. The revised decision remains within the
  same isolated-project model: existing production resources and local iOS
  signing settings are out of scope, and the operator never provides a secret.

## CDS-006 terminal recovery — required project ID allocated — 2026-09-21

- Recovery mode: `force-once`. With the now-authenticated Firebase CLI, the
  authorized creation command `npx firebase-tools projects:create
  algorithmlearning --display-name "Algorithm Learning Showcase"` was attempted
  once. Firebase rejected it because a GCP project with ID `algorithmlearning`
  already exists. This reconciles the earlier gcloud permission-denied result:
  the required project is allocated, but the authenticated account cannot access
  it.
- No project, Firebase site, billing setting, API, Cloud Run resource, secret,
  or existing production resource changed. CDS-006 cannot infer a substitute
  project ID because the governed specification fixes the canonical public
  origin as `https://algorithmlearning.web.app`. The operator must either grant
  this account access to the existing `algorithmlearning` project, or explicitly
  approve a new project ID and matching canonical-origin/spec/work-graph change.
  CDS-006 is `blocked`; CDS-007 remains pending.

## CDS-006 resumed — 2026-09-21

- The operator completed Firebase CLI authentication. `npx firebase-tools
  login:list` now reports `allenliproject@gmail.com`, and `projects:list` is
  available; it lists only the unrelated `daily-news-93f7b` project, so the
  requested `algorithmlearning` project ID remains available for creation.
  CDS-006 returned to `in_progress`. The next authorized action is creating the
  isolated Firebase/GCP project; the existing production project remains out of
  scope.

## CDS-006 terminal recovery — 2026-09-21

- Recovery mode: `force-once`. The one non-mutating deployment-preflight attempt
  established that the active operator account, `allenliproject@gmail.com`, has
  no access to `algorithmlearning`: `gcloud projects describe algorithmlearning`
  returned permission denied (or the project does not exist). The active gcloud
  project remains the protected existing production project
  `alert-study-508214-s5`, which CDS-006 must not change. Firebase tooling is
  available through `npx firebase-tools`, but that cannot supply project access
  or the required private configuration.
- No Firebase or GCP resource was created or changed; no secret, Neon endpoint,
  signing material, or iOS Xcode setting was read, printed, staged, or modified.
  The project cannot be safely created or deployed until an operator either
  grants the active account the required access to the existing
  `algorithmlearning` project, or creates that project under its own billing/
  organization boundary and grants deployment access. The operator must also
  privately configure the independent showcase runtime/migration Neon
  credentials, JWT and refresh-hash secret versions, the password-free direct
  and pooled Neon URLs, and the exact
  `https://algorithmlearning.web.app` allowed-origin setting before a Cloud Run
  release can succeed.
- CDS-006 is `blocked`; CDS-007 remains pending on it. Resume only after the
  operator confirms project access and the private configuration is complete,
  without sharing any secret value.

### Firebase CLI authentication follow-up

- The operator reported Firebase login, but the deployment environment's
  `npx firebase-tools login:list` returned `No authorized accounts`; its
  `projects:list` therefore could not authenticate. Started the official
  device-login flow, which issued session ID `A307A` and requires the operator
  to complete the displayed Firebase authorization page, then run the returned
  `firebase login <authorizationCode>` command locally. The authorization code
  must not be pasted into this conversation or committed. No project or cloud
  resource was changed by this authentication attempt.

## CDS-006 execution started — 2026-09-21

- `$execution-strategy` selected `three-perspectives` / `test-candidates` /
  `update-docs` / `infer` for the operator-gated Firebase/GCP public showcase
  deployment. Planner boundary: create or verify only the isolated
  `algorithmlearning` project resources, then deploy the prepared production
  Wasm Hosting bundle and an `asia-east1` Cloud Run API replica. Implementer
  boundary: use existing non-secret deployment configuration and operator-owned
  secret references only; never read, print, copy, or commit Neon credentials.
  Evaluator boundary: verify the exact canonical Hosting origin, same-origin
  readiness rewrite, least-privilege runtime identity, and browser
  login/refresh/logout evidence while preserving the existing production GCP
  project and untouched local iOS signing settings.
- Completion checklist: the `algorithmlearning` project and Firebase Hosting
  site exist; the showcase API runs in `asia-east1` with an independent
  least-privilege runtime identity and operator-provided Neon configuration;
  only `https://algorithmlearning.web.app` is allowed; the prepared production
  Wasm bundle is deployed with the `/api/**` rewrite; public browser
  login/refresh/logout evidence is recorded; the five CDS-006 verification
  commands pass; closeout changes only deployment records and workflow
  artifacts, never the user's iOS signing changes.

## CDS-005 closeout — 2026-09-21

- Replaced the browser refresh-cookie name with Firebase Hosting's forwardable
  `__session`. `AuthControllerAuthTest` verifies that refresh and logout bind
  that name and that rotation/expiry retain `HttpOnly`, `Secure`, `SameSite=Lax`,
  `/api/v1/auth`, and the 30-day maximum age; the original `refresh_token` name
  is absent from the emitted expiry cookie.
- The Wasm production webpack task now stages the optimized artifacts and its
  processed `index.html` into `composeApp/build/firebaseHosting`, using
  `https://algorithmlearning.web.app` as its production default. Root
  `firebase.json` deploys that directory with the `/api/**`-first rewrite to
  `algorithm-learning-api` in `asia-east1`, followed by the SPA fallback; the
  non-secret `.firebaserc` selects `algorithmlearning`. `infra/firebase/README.md`
  records the operator-only CDS-006 deployment boundary.
- Recovery succeeded on attempt 1: moving the staging task after Kotlin target
  registration fixed the missing-task failure, and the exact production command
  produced `build/firebaseHosting/index.html` containing the canonical origin.
- Task-limited verification passed: `cd services/api && ./mvnw -q test
  -Dtest='*AuthTest,*SecurityTest'`; `cd apps/multiplatform && ./gradlew
  :shared:allTests`; `cd apps/multiplatform && ./gradlew
  :composeApp:wasmJsBrowserProductionWebpack
  -PapiBaseUrl=https://algorithmlearning.web.app`; and `git diff --check`.
  The Maven run emitted only the existing Mockito dynamic-agent warning; Gradle
  emitted existing AGP/KMP compatibility and deprecation warnings.
- CDS-005 is completed. CDS-006 is now the sole ready task and remains
  operator-gated for Firebase/GCP project resources and private Neon/secret
  configuration; the iOS Xcode signing changes already present in the worktree
  were not inspected, staged, or modified.

## CDS-005 execution started — 2026-09-21

- `$execution-strategy` selected `three-perspectives` / `tdd` /
  `update-docs` / `infer` for the Firebase-compatible browser-auth and Hosting
  foundation. Planner scope: retain the frozen auth/API contract while using
  Firebase Hosting's forwardable `__session` cookie, a canonical same-origin
  Wasm production default, and a non-secret Hosting rewrite. Implementer scope:
  change only the API cookie boundary, regression tests, Wasm packaging, Hosting
  configuration, and their documentation. Evaluator guardrails: preserve all
  cookie attributes and 30-day rotation semantics, allow no wildcard origin,
  add no secret or cloud credential, and do not inspect or alter the operator's
  uncommitted iOS signing settings.
- Completion checklist: authentication tests prove `__session` is set, read,
  rotated, and expired with the existing secure attributes; Wasm's production
  build receives `https://algorithmlearning.web.app`; Firebase Hosting serves
  the generated static bundle with an SPA fallback and rewrites only `/api/**`
  to `algorithm-learning-api` in `asia-east1`; docs identify the operator-gated
  deploy boundary; only CDS-005's four verification commands run before
  closeout.

### Recovery — production Wasm Hosting directory

- `max-3-healing`, attempt 1: the first production packaging run established
  that Kotlin/Wasm writes optimized artifacts to
  `composeApp/build/kotlin-webpack/wasmJs/productionExecutable` but leaves the
  processed `index.html` separately under `build/processedResources`. The first
  staging-task wiring also resolved `wasmJsBrowserProductionWebpack` before the
  Kotlin target had registered it, causing the required production command to
  fail with `Task ... not found`.
- Repair: register the `Sync` staging task after the Kotlin target/source-set
  block, finalize the production webpack task with it, and copy both artifact
  directories into `composeApp/build/firebaseHosting`, the directory selected
  by `firebase.json`. The next attempt reruns the exact CDS-005 production
  command and confirms the staged `index.html` contains the canonical origin.

## CDS-004 closeout — 2026-09-21

- Replaced the obsolete TestFlight external-release procedure with a concise
  iOS source/Simulator showcase runbook. `COMPOSE_GUIDE.md` and
  `iosApp/README.md` now identify the local Xcode project, Simulator
  destination, and `iosApp` scheme as the complete iOS path; TestFlight, App
  Store submission, and public IPA distribution remain explicit non-goals.
- Planner conclusion: documentation is sufficient because no external iOS
  distribution is in scope. Implementer conclusion: only the Compose guide,
  iOS shell runbook, and task governance artifacts changed. Evaluator
  conclusion: the HTTPS endpoint default remains documented, no signing or
  account material was introduced, and the operator's uncommitted Xcode changes
  were not modified.
- Task-limited verification passed: `cd apps/multiplatform && xcodebuild
  -project iosApp/iosApp.xcodeproj -scheme iosApp -sdk iphonesimulator
  -configuration Debug build CODE_SIGNING_ALLOWED=NO` (with existing
  Gradle/AGP and Xcode script-phase warnings) and `git diff --check`.
- CDS-004 is completed. CDS-005 is now the sole ready task.

## CDS-004 execution started — 2026-09-21

- `$execution-strategy` selected `three-perspectives` / `rapid` /
  `update-docs` / `infer` for the iOS development-only documentation task.
  The scope is limited to replacing the withdrawn TestFlight/archive procedure
  with a source and Simulator demonstration runbook; it preserves the
  production HTTPS endpoint default and does not inspect or modify the
  operator's uncommitted Xcode signing settings.
- Completion checklist: the Compose guide and iOS shell documentation identify
  iOS as development/simulator only; local Xcode open/build/run steps are
  actionable; TestFlight, App Store, IPA, archive, upload, review, and tester
  instructions are removed; no signing/account material is added; only the two
  CDS-004 verification commands will run before closeout.

## Public showcase work graph refreshed — 2026-09-21

- The user approved the revised public-showcase specification. `$work-graph`
  replaced the withdrawn TestFlight and Web/CORS-deferral plan with the ordered
  public Web release path: CDS-004 documents iOS as source/simulator only,
  CDS-005 implements the Firebase-compatible same-origin browser foundation,
  CDS-006 is the operator-gated Firebase/Cloud Run deployment, and CDS-007
  publishes the signed Android APK outside Firebase Hosting.
- The graph uses the `three-perspectives` planning depth because its boundary
  spans browser cookie behavior, Firebase Hosting rewrites, Cloud Run identity,
  and a separately operated Neon database. The exact public origin remains
  `https://algorithmlearning.web.app`; the refresh cookie requirement remains
  `__session`; the existing production project remains out of scope.
- CDS-004 is the sole ready task. CDS-005, CDS-006, and CDS-007 are pending on
  their declared dependencies. The earlier CDS-004 TestFlight blocker is
  retained below as historical evidence only and no longer blocks this scope.

## Client distribution governance revision — 2026-09-21

- The approved showcase model replaces TestFlight external distribution and the
  Web/CORS deferral: public Wasm at `https://algorithmlearning.web.app`, direct
  Android APK distribution, and iOS source/simulator demonstration only.
- The revised spec defines a new `algorithmlearning` Firebase/GCP project with
  Firebase Hosting's `/api/**` rewrite to its own `asia-east1` Cloud Run API;
  the existing production project remains unchanged. It records the exact
  browser origin and the `__session` refresh-cookie requirement imposed by the
  Hosting rewrite boundary.
- Spec self-review found no placeholders or contradictions. No cloud resource
  has been created by this planning task.

## CDS-004 terminal blocker — 2026-09-21

- Selected contract: `three-perspectives` / `rapid` / `update-docs` / `infer`.
  The repository portion now documents the operator-only TestFlight process and
  connects `Info.plist` bundle version fields to Xcode's marketing version and
  monotonic build number.
- Required command attempted exactly:
  `cd apps/multiplatform && xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp -configuration Release archive -archivePath build/iosApp.xcarchive`.
  It stopped during `GatherProvisioningInputs` with: `Signing for "iosApp"
  requires a development team. Select a development team in the Signing &
  Capabilities editor.` No archive was produced.
- Recovery mode: `force-once`. No repository-only change can infer or create an
  Apple Developer team, registered bundle identifier, certificate, provisioning
  profile, App Store Connect access, beta review, or external invitation without
  crossing the operator-only credential/account boundary. CDS-004 is `blocked`.
- Operator next action: configure the registered bundle identifier, Apple
  Developer team, distribution certificate, and provisioning profile in Xcode;
  archive and upload the build; complete any external-test review; invite a
  tester and record their production-endpoint installation. Resume CDS-004 only
  after that evidence is available.

## CDS-004 execution started — 2026-09-21

- `$execution-strategy` selected `three-perspectives` / `rapid` /
  `update-docs` / `infer` for the operator-gated TestFlight task and moved it
  to `in_progress`. Completion checklist: a release archive uses only an
  operator-configured bundle identifier/team/signing identity; the runbook
  covers upload, processing, external review, invitation, production-endpoint
  installation, expiry/retest, and rollback; no Apple credential or signing
  material is inspected, handled, or committed; only the three CDS-004
  verification commands will run before closeout.

## CDS-003 closeout — 2026-09-21

- Added optional Android release signing in `composeApp`: it reads only the four
  operator-provided references from ignored local configuration or CI Gradle
  properties, rejects an incomplete set, and otherwise leaves debug and unsigned
  release assembly available. `androidVersionCode` and `androidVersionName`
  provide release-time version metadata without embedding a version workflow in
  source.
- Added the ignored configuration example and the Android direct-release runbook
  (build, SHA-256, unknown-source installation, version record, and rollback).
  No keystore, alias, password, or other secret was read or committed.
- Contracted verification passed: `:composeApp:assembleDebug`,
  `:composeApp:assembleRelease`, and `git diff --check`. The builds emitted only
  existing AGP/KMP compatibility and Gradle-deprecation warnings.
- CDS-003 is completed. CDS-004 and CDS-005 remain ready in Phase 3; the ordered
  next task is CDS-004.

## CDS-003 execution started — 2026-09-21

- `$execution-strategy` selected `three-perspectives` / `rapid` /
  `update-docs` / `infer` for the ready signed-APK packaging task and moved it
  to `in_progress`. Completion checklist: optional release signing activates
  only with all operator-provided references; no signing value or keystore is
  committed; debug and unsigned release builds remain possible; the runbook
  covers versioning, checksums, direct installation, and rollback; only the
  three CDS-003 verification commands will run before closeout.

## CDS-002 execution started — 2026-09-21

- `$execution-strategy` selected `three-perspectives` / `rapid` /
  `update-docs` / `infer` for the ready iOS-foundation task and moved CDS-002
  to `in_progress`. Completion checklist: declare both Kotlin/Native iOS
  targets in `shared` and `composeApp`; make the Darwin Ktor engine actual;
  add an operator-configurable Xcode shell which renders the same `App` with
  a locally persisted endpoint override; document local iOS build/run steps;
  run only the five CDS-002 verification commands; then close and commit.

## CDS-002 closeout — 2026-09-21

- Added `iosArm64` and `iosSimulatorArm64` targets to both KMP modules, the
  `iosMain` Ktor Darwin engine actual, and a thin `iosApp` SwiftUI/Xcode shell.
  The shell gets its non-secret default endpoint from `Info.plist`, persists the
  shared Settings override in `NSUserDefaults`, and builds the Compose framework
  through `embedAndSignAppleFrameworkForXcode`; it contains no signing material.
- `apps/multiplatform/iosApp/README.md` documents simulator build/run and the
  operator-only bundle-ID, team, certificate, provisioning, and endpoint-default
  settings. `COMPOSE_GUIDE.md` now includes iOS source-set, entry-point, and
  Darwin-engine boundaries.
- Contracted verification passed: `:shared:allTests`, `:composeApp:allTests`,
  `:composeApp:assembleDebug`, and the exact `xcodebuild` simulator command
  with `CODE_SIGNING_ALLOWED=NO`. The Xcode build uses `iosSimulatorArm64` and
  succeeded; its only non-failing warnings are existing AGP/KMP deprecations,
  Gradle configuration-time Wasm notices, and Xcode's expected no-output script
  phase notice.
- CDS-002 is completed. CDS-003, CDS-004, and CDS-005 are ready; the next
  ordered Phase 3 task is CDS-003.

## Client distribution governance and work graph — 2026-09-21

- CDS-001 closed: added a tested shared endpoint-selection boundary, persistent
  Android/Wasm overrides, endpoint Settings UI/localization, fresh container
  creation on origin changes, Android debug/release defaults, and the Wasm
  `-PapiBaseUrl` build-time production override. Contracted verification passed:
  `:shared:allTests`, `:composeApp:allTests`, `:composeApp:assembleDebug`, and
  `git diff --check`.

- CDS-001 is `in_progress` under `three-perspectives` / `tdd` / `update-docs` /
  `infer`. Completion checklist: build defaults are non-secret and not localhost
  in production; override validation/persistence/reset is shared and testable;
  a changed origin rebuilds the container/session; Settings strings/UI are fully
  localized; only the four CDS-001 verification commands will be run.

- `$spec-governance` selected `update-docs + infer` and governed
  `specs/client-distribution/spec.md`. The resolved contract is both a per-build
  local/production default and a validated persisted user override; an endpoint
  switch constructs a fresh dependency graph and does not reuse prior-origin
  session state.
- Distribution decisions: direct signed Android release APK (not a Play track),
  TestFlight external testing (not an App Store submission), and production Web
  hosting deferred. `GCP_CORS_ALLOWED_ORIGINS` stays absent: native clients do
  not need CORS, and no owned HTTPS Web origin currently exists.
- Android signing material, Apple Developer/App Store Connect identity,
  certificates, profiles, and TestFlight release/invitations are operator-only.
  The agent may only implement non-secret wiring, examples, and runbooks.
- `$work-graph` created `specs/client-distribution/plan.md`,
  `specs/client-distribution/tasks.md`, and CDS-001..005 in
  `agent-workflow/WORK_GRAPH.yaml` / `WORK_GRAPH.md`. `CDS-001` is the sole
  ready task; its execution contract has not yet been selected.

The Flutter application root and backend composition root are complete.

## Client distribution intake — 2026-09-21

- `$workflow-intake` selected `brainstorm` for the request to make the client
  distributable to external users with a configurable API endpoint and an iOS
  target: the product/distribution shape is still open-ended (which platforms,
  which distribution mechanism, whether production Web/CORS is in scope).
- Created `specs/client-distribution/spec.md` with the gap (hard-coded
  `DEFAULT_API_BASE_URL = "http://localhost:8080"` and no override in
  `AppContainer`/`App`; `composeApp` declares only `androidTarget` and `wasmJs`),
  scope and non-goals, `AC-CDS-01..07`, technical constraints, open decisions for
  governance, and known risks (Apple distribution prerequisites, iOS
  Kotlin/Native gaps, public-API abuse follow-up).
- No `plan.md`, `tasks.md`, or `WORK_GRAPH.yaml` node exists yet;
  `$spec-governance` is the next phase and must resolve the endpoint-config
  mechanism, the per-platform distribution mechanism, the CORS/production-Web
  decision, and the operator-only signing/account boundary.

## NRS-003 closeout — 2026-09-21

- Operator attestation (`AC-NRS-05`/`AC-NRS-09`): connected directly as
  `algorithm_learning_app` (the SQL-created runtime role) and ran the probe —
  `SELECT current_user` → `algorithm_learning_app`; `SELECT count(*) FROM problems`
  → `0` (succeeds); `CREATE TABLE ddl_probe (id int)` →
  `ERROR: permission denied for schema public`. The role is not a member of
  `neon_superuser` (`pg_has_role(...,'MEMBER') = false`) and `neondb_owner` holds
  `admin_option = true` on it.
- Release evidence: revision `algorithm-learning-api-00010-7k6` serving image
  `api:7396207879f8a5ccb8d6664f64b8ab514f7c511f`; migration execution
  `algorithm-learning-migrate-b7dt8` succeeded; previous known-good revision
  `algorithm-learning-api-00009-fhk`. Rollback is credential/revision-only and
  never reverses Flyway migrations.
- The serialized workflow `35523098287` ran exactly one migration as
  `algorithm_learning_migrate` before deploying the service, which serves as the
  SQL-created `algorithm_learning_app` with readiness `UP`.
- Secret hygiene: the agent never read, printed, or committed a secret value. The
  migration secret's stray v2 was disabled then destroyed (Cloud Run's `latest`
  resolves to the highest version number even when disabled/destroyed) and
  replaced with an enabled v3; the runtime secret has an enabled v4.
- `update-docs`: the artifact creates both roles with top-level SQL
  (`createrole_self_grant = 'set, inherit'`); the runbook documents the
  SQL-creation requirement, the Neon password policy, the `latest` resolution
  quirk, and the direct-login probe; the spec records section 10 and `AC-NRS-09`.
- Evaluator conclusions: the runtime identity holds no object ownership and no
  schema `CREATE`; the migration role is the only `table_owners` member; service
  and Job use distinct database identities; `AC-NRS-01..09` hold; WIF, SHA-pinned
  actions, serialized deploy, the pooled/direct endpoint split,
  `prepareThreshold=0`, and forward-only migrations are unchanged.
- `NRS-003` is completed. `neon-role-separation` (Phase 1 + Phase 2) is complete;
  no next ready task exists.

## NRS-003 release (attempt 3) — split identity deployed — 2026-09-21

- Added an enabled v3 to `algorithm-learning-db-migration-password` (so `latest`
  resolves to an enabled version) and re-ran the serialized workflow; run
  `35523098287` attempt 3 succeeded (verify → migration Job → service deploy →
  readiness).
- Verification passed exactly as contracted:
  - Job env `DATABASE_USERNAME=algorithm_learning_migrate` +
    `algorithm-learning-db-migration-password`; service env
    `DATABASE_USERNAME=algorithm_learning_app` + `algorithm-learning-db-password`.
  - `gcloud run jobs execute` → execution `algorithm-learning-migrate-b7dt8`
    completed successfully in 29.52s.
  - `curl .../actuator/health/readiness` → `{"status":"UP"}` at
    `https://algorithm-learning-api-qvepavg7qa-de.run.app`.
  - Identity assertion passed; `git diff --check` clean.
- New revision `algorithm-learning-api-00010-7k6` serving image
  `api:7396207879f8a5ccb8d6664f64b8ab514f7c511f`; previous known-good revision
  `algorithm-learning-api-00009-fhk`. Rollback is credential/revision-only.
- Remaining for closeout: the operator `AC-NRS-09` DDL-denial probe as the
  SQL-created runtime role via a direct login (not the SQL Editor): DML succeeds,
  `CREATE TABLE` is denied, and `pg_has_role(...,'neon_superuser','MEMBER')` is
  `false`.

## NRS-003 recovery — Cloud Run `latest` resolves to a destroyed version — 2026-09-21

- Disabling, then destroying, `algorithm-learning-db-migration-password` v2 did
  not unblock the Job: Cloud Run resolves `:latest` to the highest version number
  even when that version is DISABLED/DESTROYED, so both workflow attempts failed
  with "versions/2 is in DISABLED/DESTROYED state" at the migration Job deploy.
- Fix: add a new **enabled** version (v3) to
  `algorithm-learning-db-migration-password` so `latest` resolves to it. Set the
  migration role's login secret first with
  `ALTER ROLE algorithm_learning_migrate WITH PASSWORD '<value>'` so v3 is
  guaranteed to match, then add v3 with that value.

## NRS-003 recovery — runtime secret version added to the wrong secret — 2026-09-21

- The new version landed on `algorithm-learning-db-migration-password` (v2,
  `2026-09-20T16:31:06`) instead of `algorithm-learning-db-password`, which still
  has only v1–v3. A deploy now would resolve the migration secret's `:latest` to
  the wrong value and the migration Job would fail authentication.
- Next: add the runtime value to `algorithm-learning-db-password`, and disable or
  destroy the accidental `algorithm-learning-db-migration-password` v2 so its
  `:latest` resolves back to v1 (the migration role's password). No secret value
  is read by the agent.

## NRS-003 recovery — runtime role is Console-created again — 2026-09-21

- `pg_has_role('algorithm_learning_app','neon_superuser','MEMBER')` now returns
  `t` (it returned `f` for the earlier SQL attempt). Combined with the missing
  ADMIN grant, the `42501` on `GRANT`/`ALTER`/`DROP`, and the Console listing it,
  the current runtime role is **Console-created** and therefore cannot be
  confined.
- Fix: delete it in the Neon Console, then recreate it with a **top-level**
  `CREATE ROLE ... LOGIN ... PASSWORD` (never a `DO` block, and never via the
  Console/CLI/API), and verify `neondb_owner` gets `admin_option=t` while
  `pg_has_role(...,'neon_superuser','MEMBER')` is `f`.

## NRS-003 recovery — runtime role not administrable — 2026-09-21

- `DROP ROLE algorithm_learning_app` also returned SQLSTATE `42501`. The
  membership dump shows no ADMIN grant for the runtime role, unlike
  `algorithm_learning_migrate`, which has the automatic `cloud_admin` ADMIN grant
  (plus the `neondb_owner` SET/INHERIT grant we added).
- Consequence: the current `algorithm_learning_app` cannot be altered, granted,
  or dropped by any role we can use. It has no password and is inert (it is not a
  member of `neon_superuser`), so it is unusable but harmless.
- Suspected cause: a role created inside the `DO ... CREATE ROLE ... $$` block did
  not receive the automatic creator ADMIN grant that a top-level `CREATE ROLE`
  receives (the migration role, created top-level, did). This must be confirmed
  before changing the artifact.
- Next: confirm whether the Neon Console lists the role (and how it was created);
  then either delete and recreate it with a plain top-level `CREATE ROLE` carrying
  the password, or fall back to a new SQL runtime role name (spec section 10
  option 2).

## NRS-003 recovery — runtime role administration denied — 2026-09-21

- After recreating `algorithm_learning_app` with SQL (no `neon_superuser`; the
  operator confirmed `pg_has_role(...,'neon_superuser','MEMBER') = false`),
  `GRANT algorithm_learning_app TO neondb_owner WITH SET TRUE, INHERIT TRUE` and
  `ALTER ROLE algorithm_learning_app WITH PASSWORD ...` both returned SQLSTATE
  `42501`.
- PostgreSQL 16+ automatically grants the creating role `ADMIN` on a role it
  creates (`GRANT created TO creator WITH ADMIN TRUE, SET FALSE, INHERIT FALSE`),
  so an `ALTER ROLE` denial means the session running the commands is not the role
  that created the runtime role. The SQL Editor's active role and branch, and the
  membership grantors, must be confirmed before continuing.
- Production readiness is still HTTP 200 on revision `algorithm-learning-api-00009-fhk`,
  which is inconclusive: pooled sessions can keep serving after a role change while
  new logins would fail.
- Next: confirm `current_user`, the SQL Editor branch versus `ep-odd-dream-b33qkix1`,
  and the membership grantor/options for both roles; if the creator cannot be
  identified, recreate the runtime role with `SET createrole_self_grant = 'set,
  inherit'` and set the password in the `CREATE ROLE` statement so no `ALTER` is
  needed.

## Neon role separation revision decision — 2026-09-21

- Decision (spec section 10): reuse the `algorithm_learning_app` name — delete the
  Console-created role, recreate it with SQL.
- Updated `infra/gcp/neon-role-separation.sql` so Part A creates **both** login
  roles with SQL (guarded `DO` blocks); the runtime step no longer assumes a
  pre-existing Console role. Re-ran the NRS-001 static check: pass.
- Updated `infra/gcp/README.md`: both roles must be SQL-created; the runbook now
  says to delete the Console role first, and the DDL-denial probe must use a real
  `algorithm_learning_app` login (psql), not the SQL Editor, and assert
  `pg_has_role('algorithm_learning_app','neon_superuser','MEMBER') = false`.
- Updated `specs/neon-role-separation/plan.md`, `tasks.md`, and the `NRS-003`
  graph node with `AC-NRS-09` and spec section 10.
- Next: operator deletes the Console runtime role and re-runs Parts A and B, sets
  the runtime password and its Secret Manager version; then redeploy and re-run
  the probe.

## Neon role separation spec revision — SQL-created runtime role — 2026-09-21

- The NRS-003 runtime probe could not prove confinement. Production diagnostics
  show `algorithm_learning_app` is a member of `neon_superuser` (`inherit=t`,
  `set=t`); the role effectively holds broad table privileges and can
  `SET ROLE neon_superuser` to reach `CREATEDB`/`CREATEROLE`/`BYPASSRLS`. Neon
  documents this membership as un-removable for Console/CLI/API roles, so the
  Console-created runtime role cannot meet AC-NRS-01/02. (The failing `CREATE
  TABLE` probe was most likely run as `neondb_owner`; the app role itself has no
  `CREATE` on `public`.)
- `$spec-governance` selected `update-docs + ask-with-options` and amended
  `specs/neon-role-separation/spec.md`: section 3 decision 1 and section 7 now
  require a SQL-created runtime role, and the new section 10 records the trigger,
  the ruling, `AC-NRS-09`, and two remediation options.
- Material decision pending: reuse the `algorithm_learning_app` name (delete it in
  the Neon Console, recreate with SQL; brief serving outage) versus a new
  SQL-created name (zero downtime; runtime username changes).
- `NRS-003` stays `in_progress`; the release is not accepted until `AC-NRS-09`
  holds and the DDL-denial probe passes as the SQL-created runtime role.

## NRS-003 recovery — runtime DDL-denial probe failed — 2026-09-20

- Operator ran the runtime-role probe as `algorithm_learning_app`:
  `SELECT count(*) FROM problems` succeeded, but `CREATE TABLE ddl_probe (id int)`
  **also succeeded**. The runtime role therefore still holds `CREATE` capability,
  so AC-NRS-02/AC-NRS-05 are not met and the release is not accepted.
- Leading diagnosis: `algorithm_learning_app` was created through the Neon Console
  in NLP-001, so Neon granted it membership in `neon_superuser` (CREATEDB,
  CREATEROLE, BYPASSRLS, REPLICATION, and broad object access). Revoking
  `table_owners` and `CREATE` in Part A does not remove privileges inherited from
  `neon_superuser`, so the effective DDL capability survives. Consequence: the
  static confinement query (role attributes false) passes while effective
  privileges are broader.
- A stray `ddl_probe` table now exists, owned by `algorithm_learning_app`.
- Next: confirm the privilege source and remove it (revoke `neon_superuser`
  membership, or recreate the runtime role as a SQL-created role without
  `neon_superuser`), then re-run the probe. `NRS-003` remains `in_progress`.

## NRS-003 release — serialized workflow and identity split — 2026-09-20

- Pushed the NRS-001/NRS-002 closeout commits plus the recovery fixes to `main`
  (`4e13491..f7f2def`); the serialized workflow run `35520018112` completed
  successfully (verify → migration Job → service deploy → in-workflow readiness).
- Task-limited verification passed exactly as contracted:
  - `gcloud run jobs describe algorithm-learning-migrate` shows
    `DATABASE_USERNAME=algorithm_learning_migrate` and `DATABASE_PASSWORD` from
    `algorithm-learning-db-migration-password`. (The shallow
    `spec.template.spec.containers[0].env` path returns empty for a Job; the Job
    env lives at `spec.template.spec.template.spec.containers[0].env`, which is
    the path the contracted Python assertion uses.)
  - `gcloud run jobs execute algorithm-learning-migrate --region=asia-east1 --wait`
    → execution `algorithm-learning-migrate-6mhxz` succeeded (schema was already at
    `V2`, so this was an idempotent re-run).
  - `gcloud run services describe algorithm-learning-api` →
    `https://algorithm-learning-api-qvepavg7qa-de.run.app`;
    `curl .../actuator/health/readiness` → `{"status":"UP"}`.
  - The identity assertion passed: the service env has `algorithm_learning_app`
    and no `algorithm_learning_migrate`; the Job env has
    `algorithm_learning_migrate`.
  - `git diff --check` clean.
- Release evidence: new revision `algorithm-learning-api-00009-fhk` serving image
  `api:f7f2defe80350db1c75285906db0d9a9bdc6ea6f`; previous known-good revision
  `algorithm-learning-api-00008-xwq`. Rollback is credential/revision-only and
  never reverses Flyway migrations.
- Remaining for closeout: the operator's runtime-role DDL-denial attestation
  (`SELECT count(*) FROM problems` succeeds; `CREATE TABLE ddl_probe (id int)`
  is denied). `NRS-003` stays `in_progress` until the attestation is recorded.

## NRS-003 progress — migration role password via SQL — 2026-09-20

- The Neon Console Roles list shows only `algorithm_learning_app` and
  `neondb_owner`; Neon does not list SQL-created roles there. Console-created
  roles also receive `neon_superuser`, which this migration role must not have, so
  `algorithm_learning_migrate` stays SQL-managed.
- Set its password with
  `ALTER ROLE algorithm_learning_migrate WITH PASSWORD '<value>';` from the Neon
  SQL Editor as `neondb_owner` (it holds ADMIN OPTION and CREATEROLE on the role),
  then add that value as a new version of
  `algorithm-learning-db-migration-password` in GCP Secret Manager. The Console
  "Reset password" action is unavailable for this role.
- Confirm the SQL Editor session targets the production branch whose compute
  endpoint host is `ep-odd-dream-b33qkix1`.

## NRS-003 recovery resolved — split roles applied — 2026-09-20

- Operator applied the split successfully. Part A ran after `CREATE ROLE` was
  made idempotent with guarded `DO` blocks; Part B ran after
  `GRANT algorithm_learning_migrate TO neondb_owner WITH SET TRUE, INHERIT TRUE;`
  plus `SET ROLE algorithm_learning_migrate`. The operator reports both parts
  succeeded.
- `update-docs`: `infra/gcp/neon-role-separation.sql` and the "Split the migration
  and runtime roles" runbook now document the working recipe and the two Neon
  failure modes (`42501` when the owner has not switched into the role, `0LP01`
  when re-granting `ADMIN OPTION`), and note that Part A is safe to re-run.
- Re-ran the NRS-001 static verification after the doc edits: passed;
  `git diff --check` clean; `bash -n infra/gcp/bootstrap.sh` ok.
- Remaining before the release: create the `algorithm-learning-db-migration-password`
  Secret Manager container and accessor (`bash infra/gcp/bootstrap.sh --apply`),
  add the migration role's password as a version of that secret, set the GitHub
  `production` `NEON_MIGRATION_DATABASE_USERNAME`, then authorize `push main` and
  the serialized workflow. The runtime secret needs no rotation because the
  runtime role's password is unchanged. `NRS-003` stays `blocked` until the
  operator adds the migration secret version and authorizes the deploy.

## NRS-003 recovery — Part B defaults still absent — 2026-09-20

- The four `ALTER DEFAULT PRIVILEGES` statements failed again, and
  `pg_default_acl` contains no rows for `algorithm_learning_migrate` or
  `table_owners` (only Neon's `cloud_admin` entries), so the defaults are not
  installed.
- Membership query: `algorithm_learning_migrate -> neondb_owner` exists,
  `table_owners -> algorithm_learning_migrate` exists, `table_owners -> neondb_owner`
  appears twice, and `table_owners -> algorithm_learning_app` is correctly absent.
  The migration-role membership for `neondb_owner` lacks `SET`, so `neondb_owner`
  cannot act as the role; the `WITH ADMIN OPTION` retry hit `0LP01` because admin
  is already held.
- Change (recovery round 4): set the membership options explicitly with the
  documented PostgreSQL 16 syntax `GRANT role TO grantee WITH SET TRUE,
  INHERIT TRUE;` (multiple options are allowed), then `SET ROLE
  algorithm_learning_migrate` and run Part B. This avoids re-granting ADMIN while
  enabling both `SET ROLE` and the schema `CREATE` that `IN SCHEMA public`
  requires.

## NRS-003 recovery — Part B admin grant rejected — 2026-09-20

- Retrying with `GRANT algorithm_learning_migrate TO neondb_owner WITH ADMIN OPTION`
  produced `ERROR: ADMIN option cannot be granted back to your own grantor
  (SQLSTATE 0LP01)`. That confirms `neondb_owner` already holds an ADMIN
  membership on the migration role (created automatically when Part A's
  `CREATE ROLE` ran) and cannot be granted admin again; on Neon/PG16 that
  auto-grant does not include `SET`/`INHERIT`, so `SET ROLE` and
  `ALTER DEFAULT PRIVILEGES FOR ROLE` as `neondb_owner` remain denied (the
  earlier 42501).
- Change (recovery round 3): stop administering the role through `neondb_owner`.
  Run Part B connected as `algorithm_learning_migrate` itself (its own connection
  string), where altering its own default privileges needs no membership. The
  operator resets that role's password, which is also needed for the migration
  Secret Manager version.
- Next: operator connects as the migration role, runs the four
  `ALTER DEFAULT PRIVILEGES` statements, and confirms with `pg_default_acl` and
  the membership query.

## NRS-003 recovery — operator Part B denied default privileges — 2026-09-20

- Operator ran Part B and received
  `ERROR: permission denied to change default privileges (SQLSTATE 42501)`.
- Diagnosis: Part B's `ALTER DEFAULT PRIVILEGES FOR ROLE algorithm_learning_migrate`
  requires the executing role to be a member of (or administer) the migration
  role, and `ALTER DEFAULT PRIVILEGES ... IN SCHEMA public` also requires CREATE
  on `public`. If `algorithm_learning_migrate` was created through the Neon
  Console, `neondb_owner` has no admin option on it (the same Neon constraint
  recorded in NLP-001/SR-004), so running Part B as `neondb_owner` cannot set the
  migration role's defaults.
- Next: operator either (a) runs, as `neondb_owner`,
  `GRANT algorithm_learning_migrate TO neondb_owner WITH ADMIN OPTION;` then
  `SET ROLE algorithm_learning_migrate;` and the four Part B statements, or
  (b) connects as `algorithm_learning_migrate` directly and runs Part B there.
  The exact failing statement is still needed to confirm the cause.

## NRS-003 recovery — operator Part A failed — 2026-09-20

- Operator ran Part A of `infra/gcp/neon-role-separation.sql` as `neondb_owner`
  in the Neon SQL editor and received the Neon UI message "Failed transaction:
  ROLLBACK required"; the transaction aborted and rolled back.
- Diagnosis: Part A runs as a single transaction and used unconditional
  `CREATE ROLE` statements. `table_owners` already exists from the NLP-001/SR-004
  shared-group-role workaround, so `CREATE ROLE table_owners` raises
  "role \"table_owners\" already exists" and aborts the whole transaction. The
  artifact's "skip if it exists" comment cannot be honored inside one transaction.
- Change (recovery round 1): made role creation idempotent. The migration and
  group roles are now created inside `DO` blocks guarded by
  `IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = ...)`; the grants,
  `REVOKE`, ownership, confinement, and default-privilege statements are
  unchanged.
- Re-ran the NRS-001 static verification after the edit: passed; `git diff --check`
  clean.
- Next: the operator re-runs the corrected Part A. If a different error appears,
  its exact text is required to continue.

## NRS-003 blocker (operator setup pending) — 2026-09-20

- `$execution-strategy` confirmed the recorded contract for `NRS-003`:
  `three-perspectives` / `test-candidates` / `update-docs` / `infer`. It is the
  operator-gated Phase 2 split release and the only task in that phase.
- `$task-execute` preflight passed (`NRS-001`/`NRS-002` completed), but read-only
  production checks show the operator setup has not been performed, so no
  production change was made and `main` was not pushed:
  - `gcloud secrets versions list algorithm-learning-db-migration-password`
    returns `NOT_FOUND`; the container does not exist, so no migration secret
    version is set.
  - `gh variable list --env production` has no `NEON_MIGRATION_DATABASE_USERNAME`.
  - The `algorithm-learning-migrate` Job and the `algorithm-learning-api` service
    both still set `DATABASE_USERNAME=algorithm_learning_app` and read
    `algorithm-learning-db-password`, i.e. the pre-split identity from NLP-002.
- Blocker: the operator must, without exposing any value to the agent,
  (1) create `algorithm_learning_migrate` and reconcile `algorithm_learning_app`
  by running Parts A and B of `infra/gcp/neon-role-separation.sql`;
  (2) run `bash infra/gcp/bootstrap.sh --apply` so the
  `algorithm-learning-db-migration-password` container and its Secret Accessor
  exist, reset both role login secrets, and add them as new
  `algorithm-learning-db-password` (runtime) and
  `algorithm-learning-db-migration-password` (migration) Secret Manager versions;
  and (3) set the GitHub `production` variables `NEON_DATABASE_USERNAME` and
  `NEON_MIGRATION_DATABASE_USERNAME`.
- After that the user must authorize pushing `main` (`7f4fb25`, `ff7189f`) and
  triggering the serialized production workflow; the operator then runs the
  runtime-role DDL-denial probe and records the attestation so `NRS-003` can close
  out. The agent never receives, reads, prints, or commits a password.
- `NRS-003` set from `ready` to `blocked` in `WORK_GRAPH.yaml`, `WORK_GRAPH.md`,
  and `tasks.md`; no code or config was changed.

## NRS-003 execution strategy — 2026-09-20

- Dependency reconciliation confirmed `NRS-001` and `NRS-002` are completed, so
  `NRS-003` is the only eligible task and opens Phase 2 — Split release.
- Confirmed the contract recorded by `$work-graph`: `three-perspectives` /
  `test-candidates` / `update-docs` / `infer`.
- Planner boundary: one operator-gated split release — the operator applies the
  split roles and sets both secrets and the GitHub variables, then the serialized
  workflow migrates once as `algorithm_learning_migrate` and serves readiness as
  `algorithm_learning_app`. Implementer boundary: no repository files; only the
  operator's Neon/Secret Manager/GitHub setup and the release trigger. Evaluator
  boundary: the migration Job binds the migration identity, the service binds the
  runtime identity, readiness is `UP`, and the operator attests DDL denial.

## NRS-002 closeout — 2026-09-20

- `infra/gcp/bootstrap.sh` now creates the
  `algorithm-learning-db-migration-password` Secret Manager container and grants
  the `algorithm-learning-runtime` account Secret Accessor on it; the `--plan`
  output lists the fourth container. No secret value is read or created.
- `.github/workflows/gcp-production-deploy.yml` now binds the migration Job to
  `NEON_MIGRATION_DATABASE_USERNAME` and the
  `algorithm-learning-db-migration-password` secret, while the serving revision
  keeps `NEON_DATABASE_USERNAME` and the `algorithm-learning-db-password` secret.
  SHA-pinned actions, OIDC (`id-token: write`), serialized concurrency, the
  pooled/direct endpoint split, and `prepareThreshold=0` are unchanged.
- Task-limited verification passed exactly as contracted:
  `bash -n infra/gcp/bootstrap.sh`; `bash infra/gcp/bootstrap.sh --help`; the
  static assertion that `bootstrap.sh` names the migration secret; the static
  assertion that the workflow contains `id-token: write`, no `GCP_SA_KEY`,
  `concurrency:`, both usernames, and both secrets; `git diff --check` clean. The
  workflow also parses as YAML, and the Job/service bindings were inspected.
- Evaluator conclusions: the migration Job and the service use distinct database
  identities; no credential value is present; the change is config-only and does
  not alter the endpoint split, the image, or the migration order.
- `NRS-002` is completed. Phase 1 — Split tooling is complete, so `NRS-003`
  (operator-gated split release) advanced to `ready` and opens Phase 2 — Split
  release, which requires a new conversation per the workflow continuation
  contract.

## NRS-001 closeout — 2026-09-20

- Added `infra/gcp/neon-role-separation.sql`, a password-free template with two
  parts. Part A (run as `neondb_owner`) creates the `algorithm_learning_migrate`
  DDL role, keeps `table_owners` as the schema/object owner, grants the migration
  role membership in `table_owners`, revokes that membership and `CREATE` from the
  runtime role `algorithm_learning_app` (keeping `USAGE`), and grants the runtime
  role `SELECT`/`INSERT`/`UPDATE`/`DELETE` on application tables (excluding
  `flyway_schema_history`) plus `USAGE`/`SELECT` on sequences. Part B (run as the
  migration role, or as the owner with `SET ROLE` when it administers the role)
  installs `ALTER DEFAULT PRIVILEGES` for the migration role and `table_owners` as
  a fallback, so future Flyway objects stay DML-usable by the runtime role.
- `update-docs`: added the "Split the migration and runtime roles" runbook to
  `infra/gcp/README.md` covering the two-part procedure, the connection identity
  for the default-privilege statements, the secret/variable order, redeploy, the
  DDL-denial probe, and a credential/revision-only rollback. Updated the Secret
  Manager and GitHub Environment tables with the migration secret/username,
  updated the workflow description to name the split identities, and marked the
  single-role rotation section superseded.
- Task-limited verification passed exactly as contracted:
  `test -f infra/gcp/neon-role-separation.sql`; the static assertion that the SQL
  names both roles and `table_owners`, contains `REVOKE`, `CREATE`,
  `GRANT SELECT`, `ALTER DEFAULT PRIVILEGES`, `NOSUPERUSER`, and no credential
  assignment; `git diff --check` clean.
- Evaluator conclusions: the artifact never handles a secret; the runtime role is
  confined to DML with no ownership and no `flyway_schema_history` access; the
  migration role is the only `table_owners` member; the default-privilege
  mechanism and its connection-identity requirement match the governed spec; the
  runbook preserves the operator-only secret boundary and the forward-only
  migration rule.
- `NRS-001` is completed. `NRS-002` (bootstrap second secret container/accessor +
  workflow split identity) remains `ready` in the same Phase 1
  `neon-split-foundations` group.

## Neon role separation work graph — 2026-09-20

- `$work-graph` selected `three-perspectives` because the change crosses Neon role
  ownership/grants, GCP Secret Manager, GitHub Environment variables, the
  production workflow, and a serialized production release while the agent must
  never handle a password.
- Created `specs/neon-role-separation/plan.md` and `tasks.md` and appended
  `NRS-001`..`NRS-003` to `agent-workflow/WORK_GRAPH.yaml` with a readable graph in
  `WORK_GRAPH.md`.
- Phase 1 — Split tooling: `NRS-001` (password-free split-role SQL artifact +
  runbook) and `NRS-002` (bootstrap second secret container/accessor + workflow
  split identity) are both `ready` and share the `neon-split-foundations` group.
  Phase 2 — Split release: `NRS-003` (operator-gated apply + readiness/DDL-denial
  verification) is `pending` on both.
- Governed contract: `algorithm_learning_app` is the runtime (DML-only) role and
  `algorithm_learning_migrate` the migration (DDL) role; new
  `NEON_MIGRATION_DATABASE_USERNAME` + `algorithm-learning-db-migration-password`
  bind the migration Job while the service keeps `NEON_DATABASE_USERNAME` +
  `algorithm-learning-db-password`.

## NLP-002 closeout — 2026-09-20

- Operator-side setup (user, agent never handled a value): dedicated Neon role
  `algorithm_learning_app` created with ownership of the application schema, and
  a new `algorithm-learning-db-password` version 3
  (`2026-09-20T06:53:16Z`, enabled) holding its login secret.
- Agent-side: set the non-secret GitHub `production` variable
  `NEON_DATABASE_USERNAME=algorithm_learning_app`
  (`2026-09-20T06:53:51Z`) and triggered the serialized production workflow with
  `gh workflow run gcp-production-deploy.yml --ref main`.
- Workflow run `35495425841` succeeded: Verify API, migration Job, service
  deploy, and the in-workflow readiness check all passed.
- Task-limited verification passed exactly as contracted:
  `gcloud run jobs execute algorithm-learning-migrate --region=asia-east1 --wait`
  (execution `algorithm-learning-migrate-657tm` succeeded);
  `gcloud run services describe ... --format='value(status.url)'` returned
  `https://algorithm-learning-api-qvepavg7qa-de.run.app`;
  `curl .../actuator/health/readiness` returned `{"status":"UP"}`; the service
  env assertion confirmed `DATABASE_USERNAME=algorithm_learning_app` and no
  `neondb_owner`; `git diff --check` clean.
- Release evidence for rollback: new revision `algorithm-learning-api-00004-vhf`
  serving image
  `asia-east1-docker.pkg.dev/alert-study-508214-s5/algorithm-learning/api:738b195284c3e47875e30afcb553c3d4a58cc962`
  (origin/main `738b195`); previous known-good revision
  `algorithm-learning-api-00003-6xp`. Rollback is credential/revision-only and
  never reverses Flyway migrations.
- Executed via `workflow_dispatch` on `main`, so the earlier local closeout
  commits remain unpushed; the deployed image content is unchanged by them
  (no `services/api` change).
- Evaluator conclusions: the API and migration Job authenticate as
  `algorithm_learning_app`, not the Neon owner; no secret value was read,
  printed, or committed; the agent boundary held. `NLP-002` and the
  `neon-least-privilege-role` feature are completed. The split into separate DDL
  migration and DML-only runtime roles remains the recorded future hardening
  option.

## NLP-002 execution strategy — 2026-09-20

- Dependency reconciliation confirmed `NLP-001` is completed; `NLP-002`
  advanced from `ready` to `in_progress`.
- The user confirmed the operator setup: dedicated Neon role
  `algorithm_learning_app` created, and a new `algorithm-learning-db-password`
  version 3 (`2026-09-20T06:53:16Z`, enabled) holds its login secret. The agent
  never read the value.
- Confirmed the contract recorded by `$work-graph`: `three-perspectives` /
  `test-candidates` / `update-docs` / `infer`.
- Boundary: update the non-secret GitHub `production` `NEON_DATABASE_USERNAME`,
  trigger the serialized production workflow, and verify readiness and that the
  served `DATABASE_USERNAME` is no longer `neondb_owner`. No secret value is
  handled.

## NLP-001 closeout — 2026-09-19

- Added `infra/gcp/neon-least-privilege-role.sql`, a password-free template that
  creates the dedicated login role (`NOSUPERUSER` `NOCREATEDB` `NOCREATEROLE`
  `NOREPLICATION` `NOBYPASSRLS`), grants the owner session the temporary
  membership needed to hand over ownership, makes the role own `public`, and
  `REASSIGN OWNED BY neondb_owner` transfers the existing application objects so
  Flyway can run DDL, then revokes the temporary membership. The file contains
  no credential value.
- `update-docs`: added the "Rotate to the least-privilege role" runbook to
  `infra/gcp/README.md` covering the boundary, first and later rotations, the
  Neon console fallback when the owner cannot create roles, the
  secret/variable update order, the redeploy, verification, and a credential-only
  rollback that never reverses Flyway migrations. The Secret Manager table now
  names the dedicated role.
- No workflow or application code change was needed: the delivery already reads
  `NEON_DATABASE_USERNAME` and the `algorithm-learning-db-password` secret by
  name.
- Task-limited verification passed exactly as contracted:
  `test -f infra/gcp/neon-least-privilege-role.sql`; the static assertion that the
  SQL contains `reassign owned`/`nosuperuser`/`nocreatedb`/`nocreaterole`/
  `noreplication` and no credential assignment; `git diff --check` clean.
- Evaluator conclusions: the artifact never handles a secret; ownership transfer
  plus confinement is present; the runbook preserves the operator-only secret
  boundary and forward-only migration rule; the closeout commit is local only.
- `NLP-001` is completed. `NLP-002` (operator-gated credential rotation) advanced
  to `ready` but cannot run until the operator creates the Neon role, sets the
  Secret Manager value, and updates the GitHub `production` variable; it also
  requires an authorized production deploy.

## NLP-001 execution strategy — 2026-09-19

- Dependency reconciliation confirmed `NLP-001` has no dependencies; it advanced
  from `ready` to `in_progress`.
- Confirmed the contract recorded by `$work-graph`: `three-perspectives` /
  `test-candidates` / `update-docs` / `infer`.
- Planner boundary: one password-free ownership/rotation SQL template that
  creates the dedicated role, transfers ownership from `neondb_owner`, and
  confines the role, plus the rotation runbook. Implementer boundary:
  `infra/gcp` only; no workflow or application change; never handle a password.
  Evaluator boundary: the artifact contains no password, includes the ownership
  and confinement clauses, and the exact task verification passes.

## SR-004 closeout — 2026-09-20

- After the operator applied the shared-group-role ownership fix, the production
  workflow was re-run (`gh workflow run gcp-production-deploy.yml --ref main`,
  run `35514942600`) and succeeded: Verify API (34 tests with Docker), migration
  Job, service deploy, and the in-workflow readiness check.
- Task-limited verification passed exactly as contracted:
  `gcloud run jobs execute algorithm-learning-migrate --region=asia-east1 --wait`
  (execution `algorithm-learning-migrate-p4986` succeeded; logs show
  `Current version of schema "public": 2` and `Schema "public" is up to date`);
  `gcloud run services describe ...` returned
  `https://algorithm-learning-api-qvepavg7qa-de.run.app`;
  `curl .../actuator/health/readiness` returned `{"status":"UP"}`;
  `git diff --check` clean.
- Release evidence: new revision `algorithm-learning-api-00007-jpn` serving image
  `asia-east1-docker.pkg.dev/alert-study-508214-s5/algorithm-learning/api:f54d13176d5029f747ab50198d2739e3274ae75d`;
  previous known-good `algorithm-learning-api-00005-vw6`. The service still
  authenticates as `algorithm_learning_app`. Rollback is revision/credential-only
  and never reverses the `V2` migration.
- Fixed a real latent defect along the way: `JpaReviewRepository.save` bound
  `java.time.Instant` through a bean parameter source, which pgjdbc cannot type;
  review submission would have failed at runtime. It now binds `OffsetDateTime`.
- Corrected `infra/gcp/neon-least-privilege-role.sql` and its runbook to use
  Neon's shared-group-role ownership workaround (`table_owners`), because Neon
  roles are not Postgres superusers and `neondb_owner` cannot grant or directly
  own them.
- Evaluator conclusions: `V2` is applied and validated; `fixed-v1` history is
  intact (additive columns only); the API serves under the least-privilege role;
  the spaced-repetition feature is complete and every local commit is pushed.
- `SR-004` is completed; `spaced-repetition` has no remaining tasks.

## SR-004 recovery evidence (blocked on operator) — 2026-09-20

- Round 4: pushed `f54d131`. CI Verify API passed (34 tests with Docker,
  including the `V2` migration and review acceptance). The migration Job
  execution `algorithm-learning-migrate-bqvcp` then failed with
  `ERROR: must be owner of table reviews` (SQLSTATE 42501) on the first `V2`
  statement. The service deploy was skipped, so the API stayed on revision
  `algorithm-learning-api-00005-vw6` and `/actuator/health/readiness` is `UP`.
- Root cause: the `algorithm_learning_app` role does not own the `reviews`
  table, so it cannot `ALTER TABLE` it. The operator's ownership transfer
  (`infra/gcp/neon-least-privilege-role.sql` step 4, `REASSIGN OWNED BY
  neondb_owner TO algorithm_learning_app`) did not cover `reviews`. PostgreSQL
  DDL is transactional and Flyway wraps the migration, so the failure rolled
  back with no schema change; the database remains at `V1`.
- Blocker: transferring ownership requires the Neon owner credential, which the
  agent must not handle. The operator must, as the Neon owner, make
  `algorithm_learning_app` own every application object (at minimum `reviews`,
  plus `users`, `problems`, `solutions`, `tags`, `problem_tags`, `auth_sessions`,
  and `flyway_schema_history`), then the workflow can be re-run.
- Round 5: the operator's `GRANT algorithm_learning_app TO neondb_owner` failed
  with `permission denied to grant role` (SQLSTATE 42501). Neon roles created
  through the Console/CLI/API are not Postgres superusers and `neondb_owner` has
  no admin option on them, so the original `neon-least-privilege-role.sql`
  ownership approach cannot work as written. Rewrote the artifact and runbook to
  use Neon's documented shared-group-role workaround: create
  `table_owners NOLOGIN`, grant it to both roles, and transfer `public` and every
  application object to it, so `<APP_ROLE>` inherits ownership for Flyway DDL.

## SR-004 recovery evidence — 2026-09-20

- Round 1: pushed `98f114f`; the CI Verify API job failed on
  `ReviewAcceptanceIntegrationTest` with `Can't infer the SQL type to use for an
  instance of java.time.Instant` while seeding via `NamedParameterJdbcTemplate`.
  The `V2` migration itself applied cleanly in CI. The deploy job never ran, so
  production was not migrated.
- Round 2: bound the seed `Instant` as `java.sql.Timestamp` in the test, and
  found the same class of defect in production code: `JpaReviewRepository.save`
  passed `java.time.Instant` through `BeanPropertySqlParameterSource`, which
  pgjdbc cannot type. This was a latent defect in the delivered review
  submission path, surfaced by the new integration test. Replaced the bean
  parameter source with an explicit `MapSqlParameterSource` binding
  `reviewedAt`/`nextReviewAt` as `OffsetDateTime` (UTC).
- Round 3: `dueProblemIds` bound `now` as `Instant`; bound it as
  `OffsetDateTime` too. Corrected the acceptance expectation (the fixed-v1
  history problem is due at `AT`, so the due list is `[NEVER, FIXED_HISTORY]`).
- Local verification with Docker
  (`DOCKER_HOST=unix:///Users/allen/.colima/default/docker.sock`):
  `./mvnw -o verify` → 34 tests, 0 failures, 0 skipped (all Testcontainers
  integration tests ran, including `V2` migration and the review acceptance
  suite). The local Docker socket is colima; the default run skips Docker tests.

## SR-004 execution strategy — 2026-09-20

- Dependency reconciliation confirmed `SR-003` is completed; `SR-004` advanced
  from `ready` to `in_progress`.
- The user authorized the push and the production migration (recorded in
  conversation).
- Confirmed the contract recorded by `$work-graph`: `three-perspectives` /
  `test-candidates` / `update-docs` / `infer`.
- Boundary: push `main` so the serialized workflow verifies the API (with Docker,
  so the `V2` migration integration tests run), migrates once, redeploys, and
  checks readiness; then collect revision/readiness evidence and a revision-based
  rollback. No secret value is read or printed.

## SR-003 closeout — 2026-09-20

- Added server acceptance `ReviewAcceptanceIntegrationTest` (Testcontainers
  PostgreSQL): applies the `V2` migration, proves adaptive-v1 persistence
  (bootstrap interval 4, ease 2.50, reps 1; repeat interval 11, ease 2.65),
  fixed-v1-history bootstrap, latest-event due derivation (never-reviewed due
  from `createdAt`, reviewed problem due only after its next review), and the
  derived summary (`scheduled`, `intervalDays`, `schedule.adaptive.rated`).
- Extended the Compose acceptance: `ControlledApiAdapter.submit` now applies the
  same adaptive-v1 progression and attaches `intervalDays`/`easeFactor`/
  `repetitions`/`scheduleExplanationKey`; `ComposeAcceptanceTest` asserts the
  submitted review's schedule (confidence 4 → 7 days, reps 1) and
  `AcceptanceUiTest` asserts the rendered `schedule-context`.
- `update-docs`: added the spaced-repetition acceptance boundary and evidence
  table to `infra/acceptance/README.md`.
- Task-limited verification passed exactly as contracted:
  `cd services/api && ./mvnw -q verify` (BUILD SUCCESSFUL; Testcontainers
  integration tests skipped locally without Docker);
  `cd apps/multiplatform && ./gradlew :composeApp:allTests` (Android 29, Wasm
  48, 0 failures); `git diff --check` clean.
- Evaluator conclusions: the adaptive schedule is proven end to end; fixed-v1
  history is compatible and never rewritten; due derivation uses the latest
  event; the Compose schedule context is localized and render-only; the closeout
  commit is local only.
- `SR-003` is completed. `SR-004` (operator-gated production migration) advanced
  to `ready`; it requires the user's authorization to push/deploy, which will
  run the `V2` migration against production.

## SR-002 closeout — 2026-09-20

- Compose client: added nullable schedule metadata to `ReviewSummary`/`Review`
  and their DTOs/mappers (`intervalDays`, `scheduleExplanationKey`; `Review`
  also `easeFactor`, `repetitions`), so the client tolerates both adaptive and
  historical `fixed-v1` payloads.
- Added the shared `ScheduleContext` composable and
  `Labels.kt#scheduleExplanation`, which resolves the server's locale-neutral
  key (`schedule.neverReviewed|fixed.rated|adaptive.rated`) to catalog text and
  never invents a value. The problem-detail pane shows the latest summary's
  schedule and the Review completion shows the just-submitted review's schedule
  via `ReviewViewModel.ReviewSessionState.submittedReview`.
- `update-docs`: extended both `AppStrings` catalogs (EN/zh) with the schedule
  strings and added `COMPOSE_GUIDE.md` section 15 (review schedule context,
  null-tolerance, testing seams). The due browse already consumed the reconciled
  problem-summary contract, so no due mapping change was needed.
- Task-limited verification passed exactly as contracted:
  `cd apps/multiplatform && ./gradlew :shared:allTests` (Android and Wasm: 71
  tests each, 0 failures) and `./gradlew :composeApp:allTests` (Android 29, Wasm
  48, 0 failures); `git diff --check` clean. Added `schedule-context` assertions
  to `ReviewScreenUiTest` and `ProblemsScreenUiTest`.
- Evaluator conclusions: composables render state only and never calculate
  dates; every user-visible string resolves through `AppStrings`; no DTO leaks;
  the reconciled due contract matches `ReviewRepository.due`; the closeout
  commit is local only.
- `SR-002` is completed. `SR-003` (cross-platform acceptance) advanced to
  `ready`.

## SR-001 closeout — 2026-09-20

- Added the `adaptive-v1` server scheduling stack in `services/api`:
  `AdaptiveReviewPolicy` (deterministic confidence transitions, bootstrap
  reps=0/interval=0/ease=2.50 for fixed-v1 history, ease clamp `[1.30, 3.00]`,
  half-up rounding, UTC), the `V2__adaptive_review_schedule.sql` migration that
  adds the snapshot columns and replaces the `V1` `reviews_policy_version_check`
  with `fixed-v1`/`adaptive-v1`, and `ReviewService` selecting `adaptive-v1` for
  new events with persisted snapshot fields. Historical `fixed-v1` rows keep
  null adaptive columns and their stored `nextReviewAt`.
- Reconciled the latest-review read model (Option A): `ReviewRepository`
  exposes `latest`, `dueProblemIds` (latest event per problem, never-reviewed
  due from `createdAt`, oldest-due first), and `reviewStates`;
  `ReviewService.summaries` derives `neverReviewed|due|scheduled` plus schedule
  metadata; `ProblemsController` now returns the real latest review summary on
  list/detail; `ReviewsController` `GET /reviews/today` returns due problem
  summaries and `POST /reviews` keeps returning the created event with schedule
  metadata.
- `update-docs`: added the "Review scheduling API" section to
  `services/api/README.md`.
- Task-limited verification passed exactly as contracted:
  `cd services/api && ./mvnw -q test -Dtest='*ReviewPolicyTest,*ReviewTest,*ReviewControllerTest,*SchemaMigrationTest'`
  (12 run, 0 failures, 1 skipped: Testcontainers without Docker);
  `cd services/api && ./mvnw -q test` (33 run, 0 failures, 2 skipped Docker
  integration tests); `./mvnw -q package -DskipTests`; `git diff --check` clean.
- Evaluator conclusions: the policy is reproducible from persisted inputs;
  fixed-v1 history is never rewritten; due derivation uses the latest event;
  `/reviews/today` matches the Compose `ReviewRepository.due` problem-summary
  contract; no DTO or HTTP type leaks; the closeout commit is local only.
- `SR-001` is completed. `SR-002` (Compose schedule context) advanced to `ready`.

## Spaced-repetition work graph — 2026-09-20

- `$work-graph` selected `three-perspectives` because the feature crosses a
  forward-only migration, server scheduling, the frozen REST contract, and the
  Compose client, ending in a production migration.
- Created `specs/spaced-repetition/plan.md` and `tasks.md` and appended
  `SR-001`..`SR-004` to `agent-workflow/WORK_GRAPH.yaml` with a readable graph in
  `WORK_GRAPH.md` (`SR-001` server scheduling → `SR-002` Compose client →
  `SR-003` acceptance → `SR-004` operator-gated production migration).
- `SR-001` (adaptive-v1 policy, `V2` migration, latest-event due derivation, API
  schedule metadata) is `ready`; `SR-002`..`SR-004` are sequential.

## Spaced-repetition spec governance — 2026-09-20

- `$spec-governance` selected `update-docs + infer` and advanced
  `spaced-repetition` to governed, ready for `$work-graph`.
- Client ruling: the spec predated the Compose migration, so all client
  boundaries are now the Compose Multiplatform app `apps/multiplatform`
  (`COMPOSE_GUIDE.md`); the Flutter client is archived.
- Migration ruling: `V2` adds the nullable adaptive snapshot columns and
  replaces the `V1` `reviews_policy_version_check` (`policy_version =
  'fixed-v1'`) with a check allowing `fixed-v1`/`adaptive-v1`; historical rows
  stay valid.
- Due-contract ruling: the delivered `ReviewsController` returns review events
  for `GET /reviews/today`, but the ALG MVP spec (`AC-REV-04`) and the Compose
  `ReviewRepository.due` both define due **problem summaries** from the latest
  event. Governance resolves in favor of the MVP spec/client and adds `AC-SR-08`
  to reconcile the endpoint as part of the due change; `POST /reviews` keeps
  returning the created event. `policyVersion` on the wire is `fixed-v1` (the
  `mvp-1` value is only a Compose test fixture).
- Locked `adaptive-v1` (section 3.2 algorithm, bootstrap reps=0/interval=0/
  ease=2.50, UTC injected clock, clamp `[1.30, 3.00]`) and `AC-SR-01..08`;
  non-goals preserved (no user-configurable policy, notifications, ML, offline,
  confidence-scale change, or history rewrite).
- `plan.md`, `tasks.md`, and the `WORK_GRAPH.yaml` node do not exist yet;
  `$work-graph` is the next phase.

## Neon least-privilege role work graph — 2026-09-19

- `$work-graph` selected `three-perspectives` because the change crosses Neon
  role ownership/grants, Secret Manager, a GitHub Environment variable, and a
  serialized production redeploy.
- Created `specs/neon-least-privilege-role/plan.md` and `tasks.md` and appended
  `NLP-001` and `NLP-002` to `agent-workflow/WORK_GRAPH.yaml` with a readable
  graph in `WORK_GRAPH.md`.
- `NLP-001` (password-free SQL ownership/rotation artifact + runbook) is
  `ready`; `NLP-002` (operator-gated rotation and readiness verification) waits
  on it and needs the operator's private Neon and Secret Manager setup.

## Neon least-privilege role spec governance — 2026-09-19

- `$spec-governance` selected `update-docs + infer` and advanced
  `neon-least-privilege-role` to governed, ready for `$work-graph`.
- Topology ruling: **one dedicated application role** that owns the application
  schema and existing objects, replacing the Neon owner `neondb_owner`. Rationale:
  it removes the actual `NEO-003` risk with no config-contract expansion, and
  Flyway's DDL requirement is met by object ownership. Split DDL-migration versus
  DML-only runtime roles is deferred as the future hardening option the residual
  serving DDL capability would justify.
- Contradiction resolved: intake's "grants only" was tightened to "ownership
  transfer plus grants", because PostgreSQL `ALTER TABLE` requires ownership for
  Flyway to evolve existing tables. Concrete grants/`REASSIGN OWNED` belong in
  the operator artifact and runbook.
- Locked the config contract (`NEON_DATABASE_USERNAME` new role value,
  `algorithm-learning-db-password` new secret version; endpoints/WIF/IAM
  otherwise unchanged) and acceptance criteria `AC-NLP-01..07`; non-goals and the
  agent-never-handles-the-password boundary preserved.
- `plan.md`, `tasks.md`, and the `WORK_GRAPH.yaml` node do not exist yet;
  `$work-graph` is the next phase.

## Neon least-privilege role intake — 2026-09-19

- `$workflow-intake` selected `quick-analysis` for the follow-up recorded in the
  `NEO-003` closeout: replace the Neon owner `neondb_owner` with a dedicated
  least-privilege application role and rotate the production credential.
- Created `specs/neon-least-privilege-role/spec.md`. Recommended topology is one
  dedicated application role that owns the application schema (Flyway needs DDL);
  splitting into separate migration and runtime roles is recorded as a future
  hardening option and is the one ambiguity for `$spec-governance`. Acceptance
  criteria `AC-NLP-01..07` cover the operator artifact, credential rotation,
  readiness under the new role, runbook, and secret safety.
- Boundary preserved from `NEO-003`: the agent never handles the role password;
  role creation and the Secret Manager value are operator-only, so the rotation
  release task is operator-gated.
- No `plan.md`, `tasks.md`, or `WORK_GRAPH.yaml` node exists yet; `$spec-governance`
  is the next phase.

## CMP-008 closeout — 2026-09-19

- Retired the Flutter client by explicit archive: added
  `apps/learning_app/ARCHIVED.md` marking the directory inactive and pointing to
  `apps/multiplatform`, and rewrote `apps/learning_app/README.md` as an archive
  notice. No Flutter source was deleted, preserving the ALG-018 historical
  evidence.
- Recorded parity and migration notes in
  `specs/compose-multiplatform-migration/retirement.md`: the ALG→CMP task
  mapping, architecture/target differences (Android + Wasm active; iOS/Desktop
  deferred), acceptance evidence, and the remaining operator follow-up
  (least-privilege Neon role rotation).
- Updated the entry docs and requirement baseline: the root `README.md` now names
  `apps/multiplatform` as the active client and the Flutter toolchain/entry as
  archived; `requirement.md` frontend platform/architecture/packages, the
  REST-sharing note, Docker/test/roadmap sections, and the spec reference now
  describe the Compose client, with section 2.2 recording the Flutter client as
  retired/archived. `infra/acceptance/README.md` marks the Flutter harness as
  frozen historical evidence and names the Compose release checks.
- Task-limited verification passed exactly as contracted:
  `test ! -d apps/learning_app || test -f apps/learning_app/ARCHIVED.md` and
  `git diff --check` both exited successfully (rapid approach; no new tests).
- `CMP-008` is completed. Phase 4 — Release is complete and every
  `compose-multiplatform-migration` task is done; there is no next ready task.

## CMP-008 execution strategy — 2026-09-19

- Dependency reconciliation confirmed `CMP-007` is completed; `CMP-008`
  advanced from `ready` to `in_progress`.
- Confirmed the contract recorded by `$work-graph`: `single-agent` / `rapid` /
  `update-docs` / `infer`.
- `infer` decision: retire the Flutter client by **explicit archive** rather than
  deletion. AC-CMP-07 and the task verification both accept `ARCHIVED.md`, and
  keeping the frozen source preserves the ALG-018 acceptance evidence that
  `infra/acceptance/README.md` records. The archive notice marks it inactive and
  names `apps/multiplatform` as the replacement.
- Boundary: archive the Flutter client, update the entry READMEs and the
  requirement baseline, and record the parity/migration notes. No Compose code
  and no API behavior change.

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
