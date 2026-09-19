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
  shared/                    # domain, state, and composition root (Android + Wasm)
    src/commonMain/kotlin/com/algorithmlearning/shared
    src/commonTest/kotlin/com/algorithmlearning/shared
  composeApp/                # shared Compose UI + Android app + Wasm entry
    src/commonMain/kotlin/com/algorithmlearning/app
    src/androidMain/kotlin/com/algorithmlearning/app
    src/androidMain/AndroidManifest.xml
    src/wasmJsMain/kotlin/com/algorithmlearning/app
    src/wasmJsMain/resources/index.html
```

Dependency direction is one way: `composeApp` depends on `shared`; `shared` never
depends on `composeApp` or on any Compose UI type.

## 2. Layer responsibilities

| Layer | Lives in | Owns | Must not |
|---|---|---|---|
| Domain | `shared` `commonMain` | Immutable models, value types, repository contracts | Import Compose, Ktor DTOs, or platform types |
| State / navigation | `shared` `commonMain` | `Navigator`, `AppContainer`, language state | Touch Android/Wasm APIs |
| UI | `composeApp` `commonMain` | Composables, theme, screens | Read a repository or a service locator directly |
| Entry points | `composeApp` `androidMain` / `wasmJsMain` | `MainActivity`, Wasm `main`, manifest | Contain product logic |

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
  a repository contract, with a platform engine actual in `androidMain` and the
  Wasm engine in `wasmJsMain`. DTOs stay inside the data layer and are mapped to
  domain models before reaching the UI.

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
