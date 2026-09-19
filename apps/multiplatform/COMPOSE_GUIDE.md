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
