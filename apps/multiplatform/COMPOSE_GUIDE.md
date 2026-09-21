# Compose Multiplatform Engineering Guide

This guide governs `apps/multiplatform`, the Kotlin Compose Multiplatform client
that replaces the Flutter client `apps/learning_app`. `flutter-dev-guide/` governs
the Flutter client only and does not apply here. The Spring Boot REST contract is
frozen; this client adapts to it and never changes it.

## 1. Module architecture

```text
apps/multiplatform/
  settings.gradle.kts        # includes :shared and :composeApp
  build.gradle.kts           # plugin aliases only
  gradle/libs.versions.toml  # single pinned version catalog
  local.properties           # operator's Android SDK path, never committed
  shared/                    # domain, state, and composition root (Android + Wasm + iOS)
    src/commonMain/kotlin/com/algorithmlearning/shared
    src/commonTest/kotlin/com/algorithmlearning/shared
  composeApp/                # shared Compose UI + Android app + Wasm/iOS entries
    src/commonMain/kotlin/com/algorithmlearning/app
    src/androidMain/kotlin/com/algorithmlearning/app
    src/androidMain/AndroidManifest.xml
    src/wasmJsMain/kotlin/com/algorithmlearning/app
    src/wasmJsMain/resources/index.html
  iosApp/                    # thin Xcode shell consuming ComposeApp.framework
```

Dependency direction is one way: `composeApp` depends on `shared`; `shared` never
depends on `composeApp` or on any Compose UI type.

## 2. Layer responsibilities

| Layer | Lives in | Owns | Must not |
|---|---|---|---|
| Domain | `shared` `commonMain` | Immutable models, value types, repository contracts | Import Compose, Ktor DTOs, or platform types |
| State / navigation | `shared` `commonMain` | `Navigator`, `AppContainer`, language state | Touch Android/Wasm APIs |
| UI | `composeApp` `commonMain` | Composables, theme, screens | Read a repository or a service locator directly |
| Entry points | `composeApp` `androidMain` / `wasmJsMain` / `iosMain`, `iosApp` | `MainActivity`, Wasm `main`, `MainViewController`, Xcode shell | Contain product logic |

`shared` targets `androidLibrary` and `wasmJs`; `composeApp` targets the Android
application and `wasmJs` executable, so one `commonMain` UI serves both.

## 3. Dependency injection

`AppContainer` in `shared` is the composition root. It exposes the `Navigator`
and language state. Compose code obtains it once with
`val container = remember { AppContainer() }` and passes derived state down as
parameters.

Rules:

1. A composable receives data and callbacks as parameters; it never constructs a
   repository, `HttpClient`, or container.
2. `AppContainer` is the only place allowed to assemble concrete dependencies.
3. When CMP-002/CMP-003 add repositories, add them to `AppContainer` rather than
   introducing a global service locator.
4. Prefer constructor injection in `shared` so tests can substitute fakes.

## 4. State and data flow

- State is immutable. `Navigator` publishes an immutable `List<AppDestination>`
  through a `StateFlow`; UI collects it with `collectAsState()`.
- Data flows one way: container → composable parameters → callbacks back up.
- Screens hold only ephemeral UI state (`remember { mutableStateOf(...) }`).
- When networking arrives, Ktor `HttpClient` lives in `shared` `commonMain` behind
  a repository contract, with platform engine actuals in `androidMain`, `iosMain`,
  and `wasmJsMain`. DTOs stay inside the data layer and are mapped to domain
  models before reaching the UI.

## 5. Localization

`AppLanguage` and `StringCatalog` in `shared` resolve every user-visible string.
The supported languages mirror the Flutter client: English and Traditional
Chinese. Do not hard-code display strings in composables; add them to
`AppStrings` and both catalog branches.

## 6. Testing

- Tests live in `shared/src/commonTest` and run for every target with
  `./gradlew :shared:allTests`.
- Test domain behavior and state transitions (`Navigator`, `StringCatalog`,
  `AppContainer`) without Compose.
- Compose UI/structure tests belong in `composeApp` and arrive with the
  experience tasks.
- Keep tests deterministic: no sleeps, no network, no clock dependence.
- Verification for foundation work is:
  `./gradlew :shared:allTests` and `./gradlew :composeApp:assembleDebug`.
- Experience verification is `./gradlew :composeApp:allTests`. Plain state
  holder tests live in `composeApp/src/commonTest` and run on Android and Wasm.
  Compose structure tests that render a screen live in
  `composeApp/src/wasmJsTest` and run under `wasmJsBrowserTest`, because Compose
  Multiplatform cannot run common UI tests through the Android local test
  configuration; they must not be placed in `commonTest`. The release acceptance
  suite follows the same split; see section 14.

## 7. Versions and build

Versions are pinned in `gradle/libs.versions.toml`; never use dynamic versions.
The wrapper pins the Gradle distribution with a SHA-256 checksum. The Android SDK
path is supplied by the operator through `local.properties` (ignored by git).

```sh
cd apps/multiplatform
./gradlew :shared:allTests
./gradlew :composeApp:assembleDebug
./gradlew :composeApp:wasmJsBrowserDevelopmentRun   # optional local web run
```

`assembleDebug` requires an Android SDK; the Web target only needs the wrapper
and a JDK. The build runs on a JDK 17+ toolchain.

### Android source build

The public Android path is a local debug build from repository source; its
prerequisites, build, installation, endpoint confirmation, and source rollback
steps are documented in [`ANDROID_RELEASE.md`](ANDROID_RELEASE.md). The showcase
does not publish an APK, checksum, or external artifact. `composeApp` retains
optional release-signing references from ignored
`android-release-signing.properties` or Gradle project properties for an
operator-only use outside this scope. Keystores and password values never belong
in this repository.

### iOS development demonstration

The checked-in iOS shell is a source and iOS Simulator demonstration of the
shared Compose UI. Its local build and run instructions live in
[`iosApp/README.md`](iosApp/README.md). It needs no Apple account, signing
identity, archive, upload, review, tester, or public iOS artifact. TestFlight,
App Store submission, and public IPA distribution are deliberately out of scope
for this showcase.

### AGP 9 compatibility mode

`composeApp` is one Kotlin Multiplatform application module that owns the shared
`commonMain` UI, the Android application, and the Wasm executable. AGP 9's
defaults (`android.newDsl` and built-in Kotlin) reject combining
`com.android.application` with the Kotlin Multiplatform plugin, so
`gradle.properties` opts into AGP's documented compatibility flags
`android.builtInKotlin=false` and `android.newDsl=false`. Removing those flags
requires splitting `composeApp` into a KMP UI library plus a plain Android
application shell; do that as a deliberate change, not incidentally.

## 8. Guardrails

1. No DTO, HTTP error, or persistence type crosses into `composeApp`.
2. Composables render state and emit events; they do not fetch data.
3. Platform-specific code is limited to the HTTP engine, storage, and DI actuals.
4. The REST contract is frozen; adapt the client, never the API.
5. Keep `commonMain` free of `android.*` and browser APIs.

## 8.1 API endpoint configuration

- `EndpointSettings` in `shared/commonMain` is the single owner of endpoint
  normalization, override selection, reset, and persistence calls. It accepts
  only absolute HTTP(S) origins without paths and removes a trailing slash.
- Android and Wasm entry points supply the non-secret build default and an
  `EndpointOverrideStore`; platform storage never leaks into common UI or data
  code. Android debug defaults to the emulator's local API and release defaults
  to the deployed HTTPS API. The browser uses the Gradle `apiBaseUrl` property:
  it defaults to `http://localhost:8080` for development, while a distributable
  build must pass the exact production HTTPS origin. Its override is persisted
  in browser local storage.
- `App` recreates `AppContainer` when the effective origin changes. This creates
  a fresh HTTP client/auth repository, so process-memory auth state is not
  carried from one API origin to another. Settings UI receives only values and
  callbacks and resolves all labels/errors through `AppStrings`.

## 9. Auth/session data flow

The auth stack lives in `shared` under `com.algorithmlearning.shared.auth`,
adapting the frozen `/api/v1/auth` contract. It is the template every later data
task follows.

```text
auth/
  AuthModels.kt          # AuthUser, AuthSession, AccessToken, AuthState
  AuthFailure.kt         # AuthFailure + AuthFailureKind
  AuthRemote.kt          # transport contract (register/login/refresh/me/logout)
  AuthRepository.kt      # application contract (login/register/restore/...)
  RemoteAuthRepository.kt# in-memory token + single-flight refresh
  AuthSessionHolder.kt   # StateFlow<AuthState> for the UI
  data/                  # DTOs, mappers, KtorAuthRemote, client factory
```

Rules:

1. The access token lives only in `RemoteAuthRepository` memory. It is never
   persisted and never crosses into `composeApp`; screens read `AuthState`.
2. The refresh token is an HttpOnly cookie owned by the transport. Android
   installs `HttpCookies` in its engine actual; the Wasm build relies on the
   browser cookie jar. Platform cookie persistence to secure storage is a
   separate storage concern.
3. Refresh is single-flight: `RemoteAuthRepository` serializes it behind a
   `Mutex` and re-checks the token, so concurrent `401` handling produces one
   rotation. `AuthSessionHolder.restore()` coalesces app-start restores the same
   way.
4. `AuthFailure`/`AuthFailureKind` is the only error type that leaves the data
   layer; `KtorAuthRemote` maps statuses and transport errors to it.
5. Consumers of authenticated endpoints call `authRepository.accessToken()` and
   send `Authorization: Bearer <token>`, calling `invalidateAccessToken()` after
   a `401` so the next call refreshes. `CMP-003` repositories must take
   `AuthRepository` (or a token provider) through their constructor.
6. `AppContainer` is the only place that builds the stack. It takes `baseUrl`,
   `httpClient`, and `clock` as constructor parameters; tests pass a fake
   `AuthRemote`/`HttpClient` and a fixed `Clock` instead of using a locator.

Testing seams:

- `FakeAuthRemote` (commonTest) scripts results, failures, call counts, and a
  refresh gate for single-flight assertions.
- `KtorAuthRemote` tests drive a Ktor `MockEngine` to assert routes, the bearer
  header, DTO mapping, and status-to-`AuthFailureKind` mapping without a network.
- Auth data-flow tests run under `:shared:allTests` on Android and Wasm.

## 10. Library data flow

The problem-library data layer mirrors the auth stack. Everything lives in
`shared` under `com.algorithmlearning.shared.library` and adapts the frozen
`/api/v1` routes.

```text
library/
  LibraryModels.kt       # enums, Tag, ProblemSummary/Detail, Solution, Review, Dashboard, Page
  LibraryFailure.kt      # ApiFailure + ApiFailureKind
  LibraryRemotes.kt      # ProblemRemote, TagRemote, SolutionRemote, ReviewRemote, DashboardRemote
  LibraryRepositories.kt # repository contracts + Remote* delegating adapters
  data/                  # DTOs, mappers, ApiClient, Ktor*Remote implementations
```

Routes adapted (all under the `/api/v1` base path):

- Problems: `GET/POST /problems`, `GET/PUT/DELETE /problems/{id}`.
- Solutions: `GET/POST /problems/{id}/solutions`, `PUT/DELETE /solutions/{id}`.
- Tags: `GET/POST /tags` (`POST` returns `200` for an existing normalized name).
- Reviews: `POST /reviews`, `GET /reviews/today`, `GET /reviews/history`.
- Dashboard: `GET /dashboard`.

Rules:

1. Enum wire values are fixed by the deployed API: platform is `hacker_rank`
   (not `hackerRank`), difficulty `easy|medium|hard`, review status
   `neverReviewed|due|scheduled`, sort `updatedDesc|createdDesc|titleAsc`,
   language `kotlin|java|python|dart`. DTOs carry the raw string; mappers call
   `fromWire` and throw on unknown values (which `ApiClient.decode` converts to
   `ApiFailureKind.UNEXPECTED`).
2. `GET /dashboard` returns the counts-only shape
   (`totalProblems`, `easy`, `medium`, `hard`, `dueReviewCount`), matching the
   delivered controller rather than the richer response sketched in the spec.
   `/reviews/today` and `/reviews/history` return bare lists, not paged
   envelopes; only `GET /problems` returns a `Page`.
3. `ApiClient` is the only place that touches bearer auth. It reads a token from
   `AuthRepository.accessToken()`, attaches `Authorization: Bearer`, and on a
   `401` calls `invalidateAccessToken()`, refreshes once, and retries exactly
   once before surfacing `UNAUTHORIZED`.
4. `ApiClient.decode` is the single wrapper for body parsing and mapping, so a
   malformed payload becomes `ApiFailure(UNEXPECTED)` instead of leaking a
   serialization exception.
5. `ApiFailure` is the only error type leaving the data layer; it carries
   `kind`, `statusCode`, the problem `code`, and `fieldErrors` parsed from
   `application/problem+json`.
6. DTOs are `internal` to `library.data`. Domain models are immutable and use
   `kotlin.time.Instant`; no Ktor or serialization type reaches a repository
   caller or `composeApp`.
7. `AppContainer` builds one `ApiClient` and injects each Ktor remote into its
   `Remote*Repository`. Tests substitute a Ktor `MockEngine` or a fake remote.

Testing seams:

- `FakeAuthRepository` supplies tokens to `ApiClient` and records invalidations.
- `Fake*Remote` classes record calls for repository delegation tests.
- `MockEngine` drives every Ktor remote: path, method, query parameters, body,
  bearer header, DTO mapping, and status-to-`ApiFailureKind` mapping.
- Library data-flow tests run under `:shared:allTests` on Android and Wasm.

## 11. Auth and problem-management presentation

CMP-004 adds the first experience layer. It keeps the UI thin by separating a
presentation state holder from stateless composables:

```text
composeApp/
  commonMain/kotlin/com/algorithmlearning/app/
    App.kt                 # composition root wiring + auth gate + navigation
    Labels.kt              # enum/error-kind -> AppStrings resolvers
    auth/
      AuthViewModel.kt     # AuthFormState + AuthErrorKind
      AuthScreen.kt        # stateless login/register form
    problems/
      ProblemsViewModel.kt # ProblemsUiState + ProblemsMessage
      ProblemsScreen.kt    # stateless list/editor/detail + ProblemsActions
  commonTest/kotlin/...    # state-holder behavior (Android + Wasm)
  wasmJsTest/kotlin/...    # Compose structure tests (wasmJsBrowserTest)
```

Rules:

1. `App` is the only composable that constructs `AppContainer`. It builds each
   view model from the container's repositories and collects its `StateFlow`.
   Screens receive an immutable state object and a callback object
   (`ProblemsActions`) — never a repository, `HttpClient`, or container.
2. `ProblemsViewModel` owns all list/editor/detail state and maps `ApiFailure`
   to `ApiFailureKind`; `AuthViewModel` delegates the session lifecycle to
   `AuthSessionHolder` and maps `AuthFailure` to `AuthErrorKind`. View models
   never render text.
3. The auth gate renders `AuthScreen` until a session exists, a restoring notice
   during app-start `restore()`, and the signed-in scaffold afterwards. Session
   identity and sign-out live in the Settings tab.
4. Every user-visible string resolves through `AppStrings` and both catalog
   branches; `Labels.kt` maps domain enums and failure kinds to catalog fields.
5. The editor validates the required title locally and otherwise surfaces server
   `fieldErrors`, retaining the draft. Deleting a problem requires confirmation.
   Solutions are created and deleted independently of the problem.

Testing seams:

- `FakeAuthRepository`, `FakeProblemRepository`, `FakeTagRepository`, and
  `FakeSolutionRepository` (commonTest) record calls and script results or
  failures. Presentation tests drive view models with a test coroutine scope.
- `ProblemListState` is a sealed loading/content/failed state, so the distinct
  list states are asserted without a UI host.
- Compose structure tests render the stateless screens with fixed state and
  callback recorders under `wasmJsBrowserTest`.

## 12. Review presentation

CMP-005 adds the adaptive browse and staged Review Mode experience, built on the
same thin-presentation pattern:

```text
composeApp/src/commonMain/kotlin/com/algorithmlearning/app/review/
  ReviewViewModel.kt   # ReviewStage, DueReviewState, ReviewSessionState, ReviewUiState
  ReviewScreen.kt      # due browse list + staged session + ReviewActions
```

Rules:

1. `ReviewViewModel` depends on `ReviewRepository` and `ProblemRepository`; the
   problem detail (with its solutions) is loaded through the problem repository,
   and review submission through the review repository. It never touches HTTP.
2. `ReviewStage` is monotonic. Each reveal action advances only from its
   predecessor (`startThinking`, `revealHint`, `revealApproach`, `revealSolution`,
   `rateConfidence`), so the confidence stage is unreachable until the solution
   is shown. `submitReview` additionally requires the `CONFIDENCE` stage and a
   `0...4` confidence; out-of-range values are ignored.
3. Adaptive layout: `App` measures the available width with `BoxWithConstraints`
   and renders `ReviewScreen(twoPane = maxWidth >= 840.dp)`. The two-pane layout
   shows the Today's Review list beside the session (or a select prompt); the
   single-pane layout swaps between them. Both render the same state.
4. The Review tab is the browse entry (Today's Review due list, via
   `ReviewRepository.due`). The problem detail's review action opens the same
   session by calling `openProblem` and switching the navigator to the Review
   destination.
5. Every user-visible string resolves through `AppStrings`; `Labels.kt` maps the
   library enums and failure kinds to catalog fields.

Testing seams:

- `FakeReviewRepository` scripts the due list and submission and records calls.
- `ReviewViewModelTest` proves ordered disclosure, confidence bounds, submission
  gating, notes trimming, solution switching, and failure handling.
- `ReviewScreenUiTest` drives the real view model through the stateless UI to
  prove the ordered reveal, gated submit, and the adaptive two-pane layout.

## 13. Dashboard presentation

CMP-006 adds the dashboard/home aggregate:

```text
composeApp/src/commonMain/kotlin/com/algorithmlearning/app/dashboard/
  DashboardViewModel.kt  # DashboardState (loading/content/failed)
  DashboardScreen.kt     # totals, difficulty distribution, due count + DashboardActions
```

Rules:

1. `DashboardViewModel` depends only on the counts-only `DashboardRepository`
   and maps `ApiFailure` to `ApiFailureKind`; it holds no strings and no HTTP
   type.
2. `DashboardScreen` renders the loading spinner, a failed state with retry, a
   no-data state with an "add problem" action when `totalProblems == 0`, and
   the populated totals, per-difficulty distribution, and due-review count. The
   distribution bars use the counts as proportions of the total.
3. `App` reloads the aggregate each time the Dashboard destination becomes
   current (`LaunchedEffect(current)`), so the due count reflects a review
   submitted in the Review tab. The empty-state action switches to the Problems
   destination with a new draft.
4. Every user-visible string resolves through `AppStrings`.

Testing seams:

- `FakeDashboardRepository` scripts the aggregate and counts calls.
- `DashboardViewModelTest` proves the content, failure/retry, and reload
  behavior.
- `DashboardScreenUiTest` renders each state and asserts the totals,
  distribution, and due count.

## 14. Cross-platform acceptance

CMP-007 adds the release acceptance suite. It exercises all four experiences
against one stateful, test-only controlled API adapter at the shared repository
boundary, without a network or a running backend.

```text
composeApp/
  commonTest/kotlin/com/algorithmlearning/app/acceptance/
    ControlledApiAdapter.kt   # stateful fake over AuthRepository + library repos
    ComposeAcceptanceTest.kt  # cross-experience journey (Android + Wasm)
  wasmJsTest/kotlin/com/algorithmlearning/app/acceptance/
    AcceptanceUiTest.kt       # rendered Compose journeys (wasmJsBrowserTest)
```

Rules:

1. `ControlledApiAdapter` implements the existing `AuthRepository`,
   `ProblemRepository`, `TagRepository`, `SolutionRepository`,
   `ReviewRepository`, and `DashboardRepository` contracts over in-memory state.
   It lives in `commonTest`, is never shipped, and returns defensive copies so a
   caller cannot observe or mutate adapter internals.
2. One adapter instance is shared across experiences in a journey, so a create,
   edit, or review is visible to the list, the due review, and the dashboard.
   `ComposeAcceptanceTest` runs from `commonTest`, so the same journey executes
   on Android (`testDebugUnitTest`) and Web (`wasmJsBrowserTest`).
3. `AcceptanceUiTest` renders the real stateless screens bound to the real view
   models and the same adapter, and drives each experience through the UI:
   register, create a problem and solution, reveal/submit a review, and read the
   dashboard aggregate.
4. Owner isolation, authorization, and transport/status mapping remain API and
   data-layer concerns; the Compose acceptance adapter verifies only client
   behavior at its injected repository boundary. Every user-visible string still
   resolves through `AppStrings`.

Verification for this layer is the release task's `./gradlew
:composeApp:assembleDebug` and `./gradlew :composeApp:allTests`.

## 15. Review schedule context

`spaced-repetition` adds server-side `adaptive-v1` scheduling. The client only
renders the derived schedule; it never calculates dates.

- `ReviewSummary` and `Review` carry nullable `intervalDays` and
  `scheduleExplanationKey` (`Review` also carries `easeFactor` and
  `repetitions`). Historical `fixed-v1` events and never-reviewed problems
  serialize these as null; the DTOs default them, so the client tolerates the
  older payloads.
- The server returns a locale-neutral `scheduleExplanationKey`
  (`schedule.neverReviewed`, `schedule.fixed.rated`, `schedule.adaptive.rated`).
  `Labels.kt#scheduleExplanation` resolves it to catalog text; a null or unknown
  key renders the never-reviewed explanation rather than inventing a value.
- The shared `ScheduleContext` composable (`com.algorithmlearning.app`) renders
  the title, the localized explanation, and (when present) the next review time.
  The problem-detail pane shows the latest summary's schedule and the Review
  completion state shows the just-submitted review's schedule.
- `ReviewViewModel.ReviewSessionState.submittedReview` carries the created
  review so the completion state can render its schedule.
- The reconciled `GET /reviews/today` returns problem summaries
  (`ReviewRepository.due`), which the review browse already consumed; the due
  list gains no new mapping.
- Testing seams: `KtorReviewRemoteTest`/`KtorProblemRemoteTest` map the new DTO
  fields with `MockEngine`; `PresentationFakes.review` scripts schedule values;
  `ReviewScreenUiTest` and `ProblemsScreenUiTest` assert the rendered
  `schedule-context` through `AppStrings`.
