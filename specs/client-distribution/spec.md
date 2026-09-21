# Client Distribution and Public Web Showcase Specification

## Document status

- Feature ID: `client-distribution`
- Status: governed; public-showcase work graph refreshed
- Requirement baseline: [`../../requirement.md`](../../requirement.md), sections 2
  (platform direction), 3.2 (mobile review app), and 7 (security)
- Relates to: the delivered Compose Multiplatform client
  [`../../apps/multiplatform`](../../apps/multiplatform) (`CMP-001..CMP-008`) and
  the iOS target that
  [`../compose-multiplatform-migration/spec.md`](../compose-multiplatform-migration/spec.md)
  section 3 lists as in scope but was not delivered
- Intake mode: `brainstorm` (the product/distribution shape is still open-ended)
- Updated: 2026-09-21
- Governance mode: `update-docs` + `infer`; revised after the approved public
  showcase decision: Firebase Hosting at `https://allenljf-algorithm.web.app`,
  a same-project Cloud Run API, Android source-build guidance, and iOS source/
  simulator demonstration only.
- Execution contract: not selected; chosen per ready task after planning

## 1. Goal

Let a person outside the development machine use a public Web client and obtain
the Android source to build locally. The public showcase is served from
`https://allenljf-algorithm.web.app`; Android has no published APK artifact.
The iOS target remains in the repository for source and simulator demonstration,
not public distribution.

The delivered client already has configurable endpoint selection, Android
release-signing wiring, and an iOS Xcode shell. This revision changes the
remaining external-distribution decision from TestFlight plus deferred Web to a
public Firebase-hosted Web showcase plus Android source-build guidance.

## 2. Current state (the gap being closed)

- Endpoint selection is a shared build-default plus persisted runtime-override
  boundary. Android, Wasm, and iOS targets exist; Android signing is optional
  and operator-provided.
- The existing production project has a public Cloud Run API behind HTTPS but no
  public Web origin. Its `run.app` domain cannot safely serve as the browser API
  origin for a separately hosted site because refresh auth is cookie-based.
- The API is a single-tenant-per-user system: each account owns its own data;
  there is no shared/multi-tenant data model to change.

## 3. Scope

### In scope

- A **two-layer configurable API endpoint** for every target:
  - a build-time default selected per environment (local vs production), and
  - a user-visible setting persisted locally that overrides that default until
    reset, so a downloader can point the app at a reachable API without a
    rebuild.
- A **public Web deployment**: a new `allenljf-algorithm` Firebase/GCP project
  hosts the Wasm bundle at `https://allenljf-algorithm.web.app` and routes
  `/api/**` through Firebase Hosting to a same-project Cloud Run service in
  `asia-east1`.
- A production Cloud Run replica in the new project that uses the existing Neon
  database but its own operator-managed service account, Secret Manager
  containers, and non-secret environment configuration. No secret is copied,
  printed, or committed by the agent.
- An **iOS target**: Kotlin/Native `iosArm64` + `iosSimulatorArm64`, an `iosApp`
  Xcode entry point, and the Ktor iOS engine actual.
- A **distribution path** that is honest about platform reality:
  - Web: Firebase Hosting is the public production entry point; it is not a
    downloadable native artifact.
  - Android: visitors download the repository source and build the existing
    debug APK locally; no APK is published through Firebase or another artifact
    channel.
  - iOS: the checked-in Xcode shell is a source/simulator demonstration only;
    TestFlight, App Store submission, and public IPA distribution are deferred.
- Documentation (a client distribution/runbook section) and the engineering guide
  updates.

### Out of scope

- API, database, schema, migration, or REST-contract changes.
- New product features (notifications, offline mode, AI features).
- Multi-tenancy, accounts, billing, or admin tooling.
- Desktop target.
- Changing the existing production project's WIF, Artifact Registry, Cloud Run
  topology, or Neon role split. The new showcase project is an additional
  deployment boundary.
- Payment, analytics, or crash-reporting SDKs.

## 4. User-facing behavior and acceptance criteria

- **AC-CDS-01:** The API endpoint is configurable per environment at build time
  (a default for local and a default for production), and no target is hard-wired
  to `localhost`.
- **AC-CDS-02:** A user can change the API endpoint in the app UI; the value
  persists across app restarts and takes effect without a rebuild. An
  unreachable/misconfigured endpoint produces a clear, localized error rather
  than a crash or a silent hang.
- **AC-CDS-03:** iOS remains a first-class development target:
  `apps/multiplatform` declares `iosArm64`/`iosSimulatorArm64` and its Xcode
  entry point builds and runs the same shared UI. No external iOS distribution
  is required.
- **AC-CDS-04:** An external Android user can obtain the public source, build a
  debug APK by following documented prerequisites and commands, and install it
  locally. The public Web client is reachable at
  `https://allenljf-algorithm.web.app`.
- **AC-CDS-05:** The Wasm client is publicly served by Firebase Hosting and
  reaches its API via the same public origin's `/api/**` rewrite. The backend
  allows exactly `https://allenljf-algorithm.web.app`; no wildcard or shared
  origin is accepted.
- **AC-CDS-06:** The distribution documentation states how a visitor opens the
  Web showcase, obtains and builds the Android source locally, and runs the iOS
  project locally in Xcode. It explicitly records that iOS public distribution
  is out of scope.
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
- **AC-CDS-10:** Existing optional release-signing wiring remains operator-only:
  its keystore location, aliases, and passwords are supplied only through
  ignored local/CI configuration. No signing value or APK artifact is required
  for the public showcase; it must remain possible to build a debug APK without
  those values.

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
- Firebase Hosting and its Cloud Run rewrite must be in the same new
  `allenljf-algorithm` GCP project. The rewrite region is `asia-east1`.
- The browser refresh cookie uses the Firebase Hosting forwardable `__session`
  name while retaining `HttpOnly`, `Secure`, `SameSite=Lax`, its `/api/v1/auth`
  path, and the existing 30-day rotation semantics.

## 6. Governance decisions and assumptions

### 6.1 Recorded governance choices

| Area | Decision | Rationale and boundary |
|---|---|---|
| Documentation / ambiguity | `update-docs` + `infer` | This is an architecture and release workflow change; the supplied requirements establish a mobile-first client and an existing public production API. |
| Endpoint mechanism | **Both build-time default and runtime override.** Each target gets an explicit local or production default at build time. A locally persisted, user-visible override wins; reset restores that build default. | A distributable client needs a safe production default, while developers and self-hosted/test users need a no-rebuild escape hatch. |
| Endpoint lifecycle | Validate and normalize before persistence; construct a fresh `AppContainer` for a changed origin and clear the prior session. | The current composition root binds all remotes to one base URL. Keeping its auth state across origins risks sending a token/cookie flow to the wrong server. |
| Android distribution | **Public source with local debug-build instructions.** No APK artifact, release channel, checksum, or Play Store track is part of this milestone. | This lets an interested visitor run the Android target without operating a signing identity or artifact-distribution channel. |
| iOS distribution | **Development/source and simulator demonstration only.** TestFlight, App Store submission, and public IPA distribution are deferred. | The product is a public showcase, not an iOS distribution milestone; retaining the target demonstrates Compose Multiplatform parity without Apple release operations. |
| Web distribution | **Public Firebase Hosting at `https://allenljf-algorithm.web.app`.** The Wasm bundle uses that same origin as its production API base URL, and Firebase rewrites `/api/**` to same-project Cloud Run. | Same-origin delivery avoids cross-site refresh-cookie failure and gives the showcase a stable HTTPS entry point. |
| GCP CORS / origin validation | **Allow only `https://allenljf-algorithm.web.app`; never use a wildcard.** | The Spring origin validator still validates browser-originated auth requests. A single canonical origin prevents an alternate Firebase subdomain from becoming an unintended credential origin. |
| Cloud Run endpoint | **Deploy a second `algorithm-learning-api` service to the new `allenljf-algorithm` project in `asia-east1`.** | Firebase Hosting's Cloud Run rewrite stays within the Firebase-associated GCP project. The existing production project and its release flow remain unchanged. |
| Browser refresh cookie | **Rename the refresh cookie to `__session` without changing its security attributes or lifecycle.** | Firebase Hosting forwards `__session` for rewritten dynamic requests; this preserves the existing HttpOnly rotating-refresh design under the public origin. |
| Signing and Apple identity | **Android signing remains operator-only; Apple identity is not required for the showcase.** | The agent may create non-secret configuration wiring and runbooks but never requests, reads, prints, uploads, or rotates secret/signing/account material. |

### 6.2 Implementation contract for the endpoint

- The build system supplies a non-secret `defaultApiBaseUrl` per target/build
  variant. Local defaults remain suitable for emulator/browser development;
  the public Wasm build defaults to `https://allenljf-algorithm.web.app`, while
  the Android release default remains the existing HTTPS Cloud Run origin. A
  distributable build must never default to `localhost`.
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

### 6.3 Public showcase distribution contract

- Android source users build the existing debug variant using the checked-in
  Gradle wrapper after installing the documented Android SDK prerequisites. The
  source-build runbook states that no signed APK, checksum, unknown-source
  download flow, or external release channel is provided. Existing optional
  signing wiring remains available only to an operator outside this scope.
- `iosApp` remains an Xcode project checked into the repository and consumes the
  Kotlin framework from `shared`/`composeApp`. Its bundle identifier is a
  non-secret development setting; the runbook covers local simulator use only.
- Firebase Hosting owns the canonical public origin. Its static deployment
  contains the Wasm production bundle and SPA fallback; `/api/**` is the sole
  dynamic rewrite to `algorithm-learning-api` in `asia-east1`. The deployed Web
  bundle uses `https://allenljf-algorithm.web.app` as its production API base URL.
- The new Cloud Run service gets its own least-privilege runtime service account
  and Secret Manager references. An operator supplies the existing Neon endpoint
  and credential values through the new project's secret/configuration boundary;
  the agent never reads or copies those values.
- The existing production GCP project, its WIF delivery pipeline, and its Neon
  migration/runtime split are unchanged. The showcase API is deployed only after
  its independent configuration is operator-complete.

Assumptions retained from intake:

- The API is already reachable from the public internet
  (`https://algorithm-learning-api-qvepavg7qa-de.run.app`,
  `--allow-unauthenticated`), so external devices can call it once the client
  knows the URL.
- A single production endpoint is sufficient for this feature; per-user
  self-hosting is not required.
- The Web client is the primary public showcase; Android is a locally built
  source demonstration. iOS source/simulator support demonstrates the third
  Compose Multiplatform target without public installation.

## 7. Known risks

- The `allenljf-algorithm` Firebase project ID and its `web.app` subdomain are
  globally allocated; creation can fail if another account claims the ID.
- The new Cloud Run service needs an operator-created, non-secret/secret
  configuration boundary for Neon before it can serve production traffic.
- Firebase Hosting rewrites forward the `__session` cookie only; changing the
  cookie name must be covered by authentication regression tests.
- Adding an iOS Kotlin/Native target can surface `commonMain` code that does not
  compile for Native (for example, engine actuals and any JVM-only APIs).
- A publicly reachable API used by external clients raises rate-limiting and abuse
  considerations that this feature does not yet address; it should be recorded as
  a follow-up rather than silently ignored.

## 8. Non-goals preserved

The frozen REST contract, the existing production project's Neon migration/runtime
role separation, its serialized GCP deploy, forward-only migrations, and the
"the agent never handles a secret" boundary all remain unchanged by this feature.
TestFlight, App Store submission, public iOS IPA distribution, and public Android
APK distribution are non-goals.
