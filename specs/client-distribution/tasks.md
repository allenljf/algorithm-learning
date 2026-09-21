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

## Phase 3 — Release channels

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

### CDS-004 — Produce the operator-gated TestFlight external-test release

- Deliverable: an operator-run TestFlight release of the iOS app and release
  evidence: archive/upload, App Store Connect processing, external-test review
  where required, and an invited external tester installation against the
  production endpoint. The repository runbook names prerequisites and rollback;
  no Apple account, certificate, profile, private key, or credential is handled
  by the agent.
- Depends on: CDS-002
- Parallel group: `client-distribution-release`
- Spec refs: 3, 4 AC-CDS-03/04/06/07, 5, 6.1/6.3, 7-8
- Execution contract: selected by `$execution-strategy`.
- Verification: `cd apps/multiplatform && xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp -configuration Release archive -archivePath build/iosApp.xcarchive`; `test -d apps/multiplatform/build/iosApp.xcarchive`; `git diff --check`
- Status: ready

### CDS-005 — Record the deliberate production-Web and GCP-CORS deferral

- Deliverable: a distribution runbook that states how to make a local Wasm
  development bundle, why it is not an external production release, and the
  exact future preconditions for hosting/CORS. Confirm the production workflow
  leaves `GCP_CORS_ALLOWED_ORIGINS` absent and does not introduce a wildcard or
  shared origin.
- Depends on: CDS-001
- Parallel group: `client-distribution-release`
- Spec refs: 3, 4 AC-CDS-05/06/07, 5, 6.1/6.3, 8
- Execution contract: selected by `$execution-strategy`.
- Verification: `cd apps/multiplatform && ./gradlew :composeApp:wasmJsBrowserDevelopmentWebpack`; `python3 -c "from pathlib import Path; t = Path('.github/workflows/gcp-production-deploy.yml').read_text(); assert 'GCP_CORS_ALLOWED_ORIGINS' in t and 'https://*' not in t and 'http://*' not in t"`; `git diff --check`
- Status: ready
