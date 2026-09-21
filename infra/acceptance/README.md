# MVP acceptance evidence

`ALG-018` closes the MVP only when every command in its work-graph verification
contract succeeds. This document records the boundary each command proves so a
release check does not overstate what an individual test covers.

## Controlled integration boundary

> **Historical.** The former Flutter client was retired (`CMP-008`) and later
> deleted (`FRR-001`). Its acceptance journey remains task-history evidence only;
> the active client acceptance suite is the Compose boundary below. See
> [`../../specs/compose-multiplatform-migration/retirement.md`](../../specs/compose-multiplatform-migration/retirement.md).

The removed client used a Flutter `integration_test` harness and a stateful,
test-only controlled API adapter supplied through Riverpod provider overrides.
It drove the real problem-management and review widgets through this journey:

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
| Former Flutter integration journey | Web problem create/edit/browse plus the ordered review disclosure and confidence-submission journey. |
| Former Flutter static checks | Focused Flutter model, state, and screen coverage. |
| Compose health check | Fresh local PostgreSQL/API startup, deterministic Flyway migration, and Actuator readiness. |

Owner isolation remains an API/repository concern: the API suite seeds separate
owners and asserts that owner-scoped queries do not return another user's
records. The former Flutter harness did not emulate authorization; it verified only
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

## Spaced-repetition acceptance boundary

`spaced-repetition` adds the `adaptive-v1` scheduler. Acceptance proves the
schedule end to end across the server and the Compose client.

- `ReviewAcceptanceIntegrationTest` (`services/api`, Testcontainers PostgreSQL,
  `disabledWithoutDocker`) applies the `V2` migration and proves adaptive-v1
  persistence, the fixed-v1-history bootstrap, the previous-interval
  progression, and latest-event due derivation.
- `AdaptiveReviewPolicyReviewPolicyTest` and `ReviewServiceReviewTest` prove the
  algorithm and service behavior without a database.
- `ComposeAcceptanceTest` (commonTest) drives the adaptive schedule through the
  controlled API adapter on Android and Web; `AcceptanceUiTest` (wasmJsTest)
  renders the `schedule-context` on Review completion.

| Evidence | Acceptance covered |
|---|---|
| `services/api && ./mvnw -q verify` | Adaptive policy, V2 migration, latest-event due, fixed-v1 compatibility, and the existing API suites. |
| `apps/multiplatform && ./gradlew :composeApp:allTests` | Schedule metadata mapping and the rendered, localized schedule context on Android and Web. |

## Required release commands

The ALG-018 Flutter commands are historical; the current client release checks
are `apps/multiplatform && ./gradlew :composeApp:assembleDebug` and
`apps/multiplatform && ./gradlew :composeApp:allTests`, alongside the API and
Compose health-check commands. The local Compose stack is stopped with
`docker compose -f infra/compose.yaml down` even if an earlier check fails,
preserving the named database volume while avoiding a stray release-test stack.
