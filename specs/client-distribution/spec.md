# Client API Configuration and iOS Distribution Specification

## Document status

- Feature ID: `client-distribution`
- Status: governed; ready for `$work-graph`
- Requirement baseline: [`../../requirement.md`](../../requirement.md), sections 2
  (platform direction), 3.2 (mobile review app), and 7 (security)
- Relates to: the delivered Compose Multiplatform client
  [`../../apps/multiplatform`](../../apps/multiplatform) (`CMP-001..CMP-008`) and
  the iOS target that
  [`../compose-multiplatform-migration/spec.md`](../compose-multiplatform-migration/spec.md)
  section 3 lists as in scope but was not delivered
- Intake mode: `brainstorm` (the product/distribution shape is still open-ended)
- Updated: 2026-09-21
- Governance mode: `update-docs` + `infer`; the decisions below are inferred
  from the mobile-first requirement, the delivered client architecture, and the
  absence of a controlled production Web origin.
- Execution contract: not selected; chosen per ready task after planning

## 1. Goal

Let a person outside the development machine obtain the client, launch it, and
use it against the deployed API without rebuilding it: a configurable API
endpoint (local or production) instead of the hard-coded
`http://localhost:8080`, plus an iOS target so the app can run on iPhone/iPad.

Today `apps/multiplatform/shared/src/commonMain/kotlin/com/algorithmlearning/shared/auth/data/AuthHttpClient.kt`
defines `DEFAULT_API_BASE_URL = "http://localhost:8080"` and
`composeApp/.../App.kt` constructs `AppContainer()` with no override, so every
build talks to `localhost`. The iOS app does not exist: `composeApp` declares only
`androidTarget` and `wasmJs` targets.

## 2. Current state (the gap being closed)

- API base URL is a compile-time constant with no runtime or build-time
  override (`AppContainer.kt:38`, `App.kt:51`).
- Android and Web (Wasm) targets exist. There is no `iosApp` project, no
  Kotlin/Native iOS target, and no signing or distribution path.
- Production is GCP Cloud Run behind HTTPS with a WIF-based deploy; it is
  `--allow-unauthenticated` for the API routes but has no CORS origin configured
  (`GCP_CORS_ALLOWED_ORIGINS` absent), so browser clients are blocked while
  native clients are not.
- The API is a single-tenant-per-user system: each account owns its own data;
  there is no shared/multi-tenant data model to change.

## 3. Scope

### In scope

- A **two-layer configurable API endpoint** for every target:
  - a build-time default selected per environment (local vs production), and
  - a user-visible setting persisted locally that overrides that default until
    reset, so a downloader can point the app at a reachable API without a
    rebuild.
- A **reachable production endpoint** decision: whether external users call the
  existing Cloud Run service directly, and what origin/CORS configuration it then
  needs.
- An **iOS target**: Kotlin/Native `iosArm64` + `iosSimulatorArm64`, an `iosApp`
  Xcode entry point, and the Ktor iOS engine actual.
- A **distribution path** that is honest about platform reality:
  - Web: how the Wasm bundle is built and served/handed to a user.
  - Android: how an installable artifact is produced and shared (for example a
    signed or debug APK, or an internal/closed release track).
  - iOS: Apple requires a signed build; document the realistic path (own device
    via development signing, TestFlight, or the App Store) and its prerequisites
    (an Apple Developer account, bundle identifier, provisioning).
- Documentation (a client distribution/runbook section) and the engineering guide
  updates.

### Out of scope

- API, database, schema, migration, or REST-contract changes.
- New product features (notifications, offline mode, AI features).
- Multi-tenancy, accounts, billing, or admin tooling.
- Desktop target.
- Changing WIF, Artifact Registry, Cloud Run topology, or the Neon role split.
- Payment, analytics, or crash-reporting SDKs.

## 4. User-facing behavior and acceptance criteria

- **AC-CDS-01:** The API endpoint is configurable per environment at build time
  (a default for local and a default for production), and no target is hard-wired
  to `localhost`.
- **AC-CDS-02:** A user can change the API endpoint in the app UI; the value
  persists across app restarts and takes effect without a rebuild. An
  unreachable/misconfigured endpoint produces a clear, localized error rather
  than a crash or a silent hang.
- **AC-CDS-03:** iOS is a first-class target: `apps/multiplatform` declares
  `iosArm64`/`iosSimulatorArm64` and provides an `iosApp` Xcode entry point that
  builds and runs the same shared UI and repositories.
- **AC-CDS-04:** The client runs on a physical Android device and a physical iOS
  device (or simulator) against the production API, and an external person can
  install it by following documented steps.
- **AC-CDS-05:** The Web (Wasm) client works when served from an origin that the
  API allows (documented per-target base-URL/CORS guidance), or the spec records
  explicitly that production Web is deferred with the reason.
- **AC-CDS-06:** The distribution documentation states, per platform, exactly how
  an external user obtains and installs the app, with the prerequisites and the
  cost/account requirements (especially the Apple Developer account for iOS).
- **AC-CDS-07:** No secret value, signing key, keystore, provisioning profile, or
  service-account credential is committed; anything required is operator-provided
  and referenced by name.
- **AC-CDS-08:** The endpoint setting accepts an absolute `http` or `https` API
  origin without a path, normalizes a trailing slash, rejects blank/malformed
  input before saving it, and distinguishes invalid input from a reachable API
  that returns an auth/transport failure. Production defaults use HTTPS; a local
  HTTP endpoint is an explicitly selected development setting only.
- **AC-CDS-09:** Changing or resetting the endpoint rebuilds the dependency
  graph/session against the selected origin before the next request; an access
  token or refresh state from the former origin is never reused at the new one.
- **AC-CDS-10:** A release APK is signed only when the operator supplies the
  keystore location, aliases, and signing passwords through ignored local/CI
  configuration. The repository may contain only a non-secret example and the
  signing configuration; it must remain possible to build an unsigned/debug APK
  without those values.

## 5. Technical constraints

- Must reuse the delivered client architecture: manual DI in `AppContainer`,
  Ktor engines per platform, immutable state, no DTO in UI
  (`apps/multiplatform/COMPOSE_GUIDE.md` is authoritative).
- Must keep Android and Web working; the existing verification
  (`./gradlew :shared:allTests`, `:composeApp:allTests`, `:composeApp:assembleDebug`)
  must still pass.
- The production API is HTTPS with a plain (non-self-signed) certificate; iOS
  App Transport Security is satisfied without exceptions.
- The API JWT/refresh contract and the pooled-endpoint `prepareThreshold=0`
  setting are unchanged.
- iOS builds require macOS + Xcode; CI is `ubuntu-24.04` and cannot build iOS, so
  iOS verification is operator/local unless a macOS runner is introduced.

## 6. Governance decisions and assumptions

### 6.1 Recorded governance choices

| Area | Decision | Rationale and boundary |
|---|---|---|
| Documentation / ambiguity | `update-docs` + `infer` | This is an architecture and release workflow change; the supplied requirements establish a mobile-first client and an existing public production API. |
| Endpoint mechanism | **Both build-time default and runtime override.** Each target gets an explicit local or production default at build time. A locally persisted, user-visible override wins; reset restores that build default. | A distributable client needs a safe production default, while developers and self-hosted/test users need a no-rebuild escape hatch. |
| Endpoint lifecycle | Validate and normalize before persistence; construct a fresh `AppContainer` for a changed origin and clear the prior session. | The current composition root binds all remotes to one base URL. Keeping its auth state across origins risks sending a token/cookie flow to the wrong server. |
| Android distribution | **Signed release APK, distributed directly as a versioned release artifact.** Debug APK is developer-only; Play Store tracks are deferred. | This gives an external Android user an installable artifact without adding a store account, listing, policy, or review workflow to this milestone. |
| iOS distribution | **TestFlight external testing** is the external-user path. Development signing is limited to the operator's own registered device/simulator; App Store submission is deferred. | TestFlight is Apple's realistic pre-store distribution mechanism and preserves a later App Store review/listing decision. |
| Web distribution | **Production Web hosting is deferred.** The Wasm bundle remains locally buildable for development and can be served only from a developer-controlled local origin. No public static host or external-Web release is created in this feature. | There is no controlled production Web domain yet; allowing a broad/shared origin would weaken the browser credential boundary. Android and iOS satisfy the mobile distribution goal. |
| GCP CORS | **Do not configure `GCP_CORS_ALLOWED_ORIGINS` in this feature; leave it absent.** | Native Android/iOS clients do not need CORS. It must be set later to one or more exact HTTPS origins only after a public Web host and ownership of its domain are approved; no wildcard, path, or empty value is valid. |
| Cloud Run endpoint | Native production builds use the existing HTTPS Cloud Run URL as their production default. | It is publicly reachable and satisfies iOS ATS without an exception; no API contract or deployment topology changes are required. |
| Signing and Apple identity | **Operator-only.** The operator owns Android keystore generation/storage, Google Play enrollment if later selected, Apple Developer membership, bundle-ID registration, certificates, provisioning profiles, App Store Connect access, and TestFlight invitation/release actions. | The agent may create non-secret configuration wiring, examples, and runbooks but never requests, reads, prints, commits, uploads, or rotates secret/signing/account material. |

### 6.2 Implementation contract for the endpoint

- The build system supplies a non-secret `defaultApiBaseUrl` per target/build
  variant. Local defaults remain suitable for emulator/browser development;
  production defaults are the exact HTTPS Cloud Run origin recorded in the
  runbook. A production APK/IPA/Wasm build must never default to `localhost`.
- A shared endpoint-settings abstraction owns validation, normalization,
  persistence, reset, and the selected-origin state. Platform storage actuals
  remain behind that abstraction; `commonMain` neither imports platform storage
  APIs nor exposes their types to UI.
- `App` observes the selected origin and is the only UI composition point that
  creates the corresponding `AppContainer`. The settings screen receives state
  and callbacks only. It exposes the build default, current effective origin,
  save/reset actions, and localized validation/reachability feedback.
- Reachability is checked through the normal client flow (for example, a
  session restore or a bounded health/auth request). Failure must retain the
  editable value and explain that the endpoint could not be reached; it must not
  crash, hang indefinitely, or silently fall back to a different origin.

### 6.3 Distribution contract

- Android release artifacts use a stable application ID, monotonically increased
  version code/name, and the release signing config described in the runbook.
  The direct-download instructions must disclose Android's unknown-source
  installation permission and include checksum/version verification.
- `iosApp` is an Xcode project checked into the repository and consumes the
  Kotlin framework from `shared`/`composeApp`. The bundle identifier is a
  non-secret build setting selected by the operator. The TestFlight runbook
  names the Apple Developer and App Store Connect prerequisites, archive/upload,
  external-test review, invitation, and expiry/retest behavior.
- The deferred Web decision is intentional, not an unconfigured CORS fallback.
  Its follow-up must choose an owned HTTPS origin, static-host lifecycle and
  artifact publishing policy, then set `GCP_CORS_ALLOWED_ORIGINS` to that exact
  origin and verify credentialed auth/refresh/logout in a browser.

Assumptions retained from intake:

- The API is already reachable from the public internet
  (`https://algorithm-learning-api-qvepavg7qa-de.run.app`,
  `--allow-unauthenticated`), so external devices can call it once the client
  knows the URL.
- A single production endpoint is sufficient for this feature; per-user
  self-hosting is not required.
- The Web client remains optional for external distribution; Android and iOS are
  the primary "download and use" targets (requirement section 3.2 makes mobile
  the review surface).

## 7. Known risks

- Apple distribution has hard external prerequisites (paid account, provisioning,
  review) that can block "external people can download and use" for iOS; the
  realistic first milestone may be a development-signed device build or
  TestFlight rather than the App Store.
- Adding an iOS Kotlin/Native target can surface `commonMain` code that does not
  compile for Native (for example, engine actuals and any JVM-only APIs).
- A publicly reachable API used by external clients raises rate-limiting and abuse
  considerations that this feature does not yet address; it should be recorded as
  a follow-up rather than silently ignored.

## 8. Non-goals preserved

The frozen REST contract, the Neon migration/runtime role separation, the
serialized GCP deploy, forward-only migrations, production-Web deferral, and the
"the agent never handles a secret" boundary all remain unchanged by this feature.
