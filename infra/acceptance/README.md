# MVP acceptance evidence

`ALG-018` closes the MVP only when every command in its work-graph verification
contract succeeds. This document records the boundary each command proves so a
release check does not overstate what an individual test covers.

## Controlled integration boundary

[`../../apps/learning_app/integration_test/mvp_acceptance_test.dart`](../../apps/learning_app/integration_test/mvp_acceptance_test.dart)
uses Flutter's `integration_test` harness and a stateful, test-only controlled
API adapter supplied through Riverpod provider overrides. It drives the real
problem-management and review widgets through this journey:

1. create a personal problem;
2. edit and browse the saved problem; and
3. reveal every Review Mode stage in order, select confidence, and submit it.

The adapter is intentionally restricted to the test harness: it provides a
deterministic API boundary without introducing a local cache or test behavior
into production widgets. It also makes the submitted confidence observable.

## Acceptance mapping

| Evidence | MVP acceptance covered |
|---|---|
| `services/api && ./mvnw -q verify` | Authentication/session rules, validation, migrations, review scheduling, dashboard aggregation, repository mappings, and owner-scoped data behavior. |
| Flutter integration journey | Web problem create/edit/browse plus the ordered review disclosure and confidence-submission journey. |
| `flutter analyze` and `flutter test` | Static checks and focused Flutter model, state, and screen coverage. |
| Compose health check | Fresh local PostgreSQL/API startup, deterministic Flyway migration, and Actuator readiness. |
| Flutter guide rule check | The staged Flutter changes conform to the repository guide's automated rules. |

Owner isolation remains an API/repository concern: the API suite seeds separate
owners and asserts that owner-scoped queries do not return another user's
records. The Flutter harness does not emulate authorization; it verifies only
the client behavior at its injected repository boundary.

## Compose client acceptance boundary

`CMP-007` repeats this boundary for the Kotlin Compose Multiplatform client in
`apps/multiplatform`. It adds a stateful, test-only controlled API adapter
(`composeApp/src/commonTest/.../acceptance/ControlledApiAdapter.kt`) over the
existing shared repository contracts, so no test behavior enters production
code.

- `ComposeAcceptanceTest` (`commonTest`) runs the same four-experience journey on
  Android (`testDebugUnitTest`) and Web (`wasmJsBrowserTest`): register, create
  and search a problem with a tag and solution, reveal and submit a staged
  review, then read the dashboard aggregate the journey produced.
- `AcceptanceUiTest` (`wasmJsTest`, `wasmJsBrowserTest`) renders the real
  stateless screens against the same adapter and drives each experience through
  the UI.
- The acceptance adapter verifies only client behavior at its injected
  repository boundary; owner isolation, authorization, and status mapping stay
  with the API and data-layer suites.

| Compose evidence | Acceptance covered |
|---|---|
| `apps/multiplatform && ./gradlew :composeApp:allTests` | Auth, problem management, browse/staged Review Mode, and dashboard presentation behavior plus their shared-session integration on Android and Web. |
| `apps/multiplatform && ./gradlew :composeApp:assembleDebug` | The shared `commonMain` Compose client builds for Android. |
| `apps/multiplatform/COMPOSE_GUIDE.md` | The client follows its architecture, DI, data, and testing contract (AC-CMP-02). |

## Required release commands

Run the exact ALG-018 verification list from the repository root. The Compose
stack is stopped with `docker compose -f infra/compose.yaml down` even if an
earlier check fails, preserving the named database volume while avoiding a
stray release-test stack.
