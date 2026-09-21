# Client Distribution — Task List

All task IDs, dependencies, statuses, and exact verification commands are
authoritative in `agent-workflow/WORK_GRAPH.yaml`. This document is the
human-readable execution contract.

## Phase 1 — Client configuration

### CDS-001 — Add safe build-time and runtime API endpoint selection

- Deliverable: a non-secret build-time local/production default for Android,
  Wasm, and future iOS; a shared validated persisted override/reset abstraction
  with platform storage actuals; and a localized Settings UI that rebuilds the
  `AppContainer` at an origin change and clears former-origin session state.
- Depends on: CMP-008
- Parallel group: `client-distribution-configuration`
- Spec refs: 3, 4 AC-CDS-01/02/07/08/09, 5, 6.1-6.2, 8
- Execution contract: `three-perspectives` analysis; `tdd` test approach;
  `update-docs` documentation; `infer` ambiguity handling.
- Verification: `cd apps/multiplatform && ./gradlew :shared:allTests`; `cd apps/multiplatform && ./gradlew :composeApp:allTests`; `cd apps/multiplatform && ./gradlew :composeApp:assembleDebug`; `git diff --check`
- Status: completed

## Phase 2 — iOS foundation

### CDS-002 — Add the Compose Multiplatform iOS target and Xcode entry point

- Deliverable: `iosArm64` and `iosSimulatorArm64` targets in `shared` and
  `composeApp`, a Ktor Darwin engine actual, an operator-configurable `iosApp`
  Xcode project/framework integration, and iOS build/run documentation. The
  shared UI/repositories and frozen REST API remain unchanged.
- Depends on: CDS-001
- Parallel group: `client-distribution-ios`
- Spec refs: 3, 4 AC-CDS-01/03/04/07, 5, 6.1-6.3, 7-8
- Execution contract: `three-perspectives` analysis; `rapid` test approach;
  `update-docs` documentation; `infer` ambiguity handling. This task changes
  target/build and generated Xcode integration rather than independently
  testable product behavior; its exact Gradle and Xcode build verification is
  the acceptance evidence.
- Verification: `cd apps/multiplatform && ./gradlew :shared:allTests`; `cd apps/multiplatform && ./gradlew :composeApp:allTests`; `cd apps/multiplatform && ./gradlew :composeApp:assembleDebug`; `cd apps/multiplatform && xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp -sdk iphonesimulator -configuration Debug build CODE_SIGNING_ALLOWED=NO`; `git diff --check`
- Status: completed

## Phase 3 — Showcase release

### CDS-003 — Define repeatable signed Android APK packaging

- Deliverable: non-secret Android release-signing wiring, ignored operator/CI
  configuration example, versioning/release-artifact instructions, checksum
  guidance, and installation/rollback runbook. It must still support debug
  builds without operator signing material and must never read or commit it.
- Depends on: CDS-001
- Parallel group: `client-distribution-release`
- Spec refs: 3, 4 AC-CDS-04/06/07/10, 5, 6.1/6.3, 8
- Execution contract: `three-perspectives` analysis; `rapid` test approach;
  `update-docs` documentation; `infer` ambiguity handling. This is build and
  release configuration rather than independently testable product behavior;
  the exact debug/release assembly commands are its acceptance evidence.
- Verification: `cd apps/multiplatform && ./gradlew :composeApp:assembleDebug`; `cd apps/multiplatform && ./gradlew :composeApp:assembleRelease`; `git diff --check`
- Status: completed

### CDS-004 — Record iOS as a development-only demonstration target

- Deliverable: remove the obsolete TestFlight release procedure and document the
  checked-in `iosApp` Xcode shell as a source/simulator demonstration target.
  It retains the production endpoint default but does not require an Apple
  identity, archive, upload, or external tester.
- Depends on: CDS-002
- Parallel group: `client-distribution-release`
- Spec refs: 3, 4 AC-CDS-03/06/07, 5, 6.1/6.3, 8
- Execution contract: selected by `$execution-strategy`.
- Verification: `cd apps/multiplatform && xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp -sdk iphonesimulator -configuration Debug build CODE_SIGNING_ALLOWED=NO`; `git diff --check`
- Status: ready

## Phase 4 — Public Web foundation

### CDS-005 — Add Firebase-compatible same-origin browser authentication and Hosting configuration

- Deliverable: rename the browser refresh cookie to `__session` without changing
  its secure rotation lifecycle; add regression coverage; make the production
  Wasm build use `https://algorithmlearning.web.app`; and add Firebase Hosting
  configuration for static assets, SPA fallback, and `/api/**` Cloud Run rewrite.
- Depends on: CDS-001, CDS-004
- Parallel group: `client-distribution-web-foundation`
- Spec refs: 3, 4 AC-CDS-01/02/05/06/07/08/09, 5, 6.1-6.3, 8
- Execution contract: selected by `$execution-strategy`.
- Verification: `cd services/api && ./mvnw -q test -Dtest='*AuthTest,*SecurityTest'`; `cd apps/multiplatform && ./gradlew :shared:allTests`; `cd apps/multiplatform && ./gradlew :composeApp:wasmJsBrowserProductionWebpack -PapiBaseUrl=https://algorithmlearning.web.app`; `git diff --check`
- Status: pending

## Phase 5 — Public deployment

### CDS-006 — Provision and release the public Firebase/Cloud Run showcase

- Deliverable: create the `algorithmlearning` Firebase/GCP project; deploy the
  Cloud Run API in `asia-east1` under its own least-privilege runtime identity;
  configure its exact allowed origin and operator-provided Neon secrets; deploy
  Firebase Hosting; and record public browser login/refresh/logout evidence.
- Depends on: CDS-005
- Parallel group: `client-distribution-public-deployment`
- Spec refs: 3, 4 AC-CDS-04/05/06/07/08/09, 5, 6.1-6.3, 7-8
- Execution contract: selected by `$execution-strategy`.
- Verification: `gcloud projects describe algorithmlearning`; `firebase hosting:sites:list --project algorithmlearning`; `curl --fail https://algorithmlearning.web.app`; `curl --fail https://algorithmlearning.web.app/api/actuator/health/readiness`; `git diff --check`
- Status: pending

## Phase 6 — Android artifact publication

### CDS-007 — Publish a signed Android showcase APK outside Firebase Hosting

- Deliverable: operator-published signed APK and SHA-256 through a non-Firebase
  release channel, plus public download/install evidence and rollback reference.
- Depends on: CDS-003, CDS-006
- Parallel group: `client-distribution-android-publication`
- Spec refs: 3, 4 AC-CDS-04/06/07/10, 5, 6.3, 8
- Execution contract: selected by `$execution-strategy`.
- Verification: `cd apps/multiplatform && ./gradlew :composeApp:assembleRelease`; `git diff --check`
- Status: pending
