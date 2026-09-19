# Flutter Client Retirement and Parity Notes

## Record

- Feature: `compose-multiplatform-migration` (task `CMP-008`)
- Date: 2026-09-19
- Decision: the Flutter client `apps/learning_app` is retired by **explicit
  archive**. It is retained, frozen, and marked inactive by
  [`../../apps/learning_app/ARCHIVED.md`](../../apps/learning_app/ARCHIVED.md);
  it is not built, tested, or deployed. `apps/multiplatform` is the active
  client.
- Acceptance basis: `AC-CMP-07` closes when the cross-platform acceptance suite
  passes and the Flutter client is removed or explicitly archived. `CMP-007`
  passed, so this task archives the client.

## Active client

| Concern | Retired Flutter client | Active Compose client |
|---|---|---|
| Root | `apps/learning_app` | `apps/multiplatform` |
| Language / UI | Dart, Material 3, Riverpod, GoRouter, Dio | Kotlin, Compose Multiplatform, Ktor, manual `AppContainer` DI |
| Targets | Android, iOS, Web | Android, Web (Wasm); iOS/Desktop deferred |
| Engineering guide | `flutter-dev-guide/` | `apps/multiplatform/COMPOSE_GUIDE.md` |
| Verification | `flutter analyze` / `flutter test` / `integration_test` | `./gradlew :composeApp:allTests` / `:composeApp:assembleDebug` |

## Parity mapping

| Delivered Flutter behavior | Compose replacement | Acceptance |
|---|---|---|
| `ALG-011` auth/session data flow | `CMP-002` shared auth data, `CMP-004` auth UI | `AC-CMP-03` |
| `ALG-012` library remote/repositories | `CMP-003` shared library data flows | `AC-CMP-04` |
| `ALG-013` problem management UI | `CMP-004` problems/tags/solutions UI | `AC-CMP-04` |
| `ALG-014` browse and staged Review Mode | `CMP-005` review presentation | `AC-CMP-05` |
| `ALG-015` dashboard/home | `CMP-006` dashboard presentation | `AC-CMP-06` |
| `ALG-018` MVP acceptance journey | `CMP-007` cross-platform acceptance | `AC-CMP-07` |
| `ALG-001..003` Flutter composition root | `CMP-001` Compose root and guide | `AC-CMP-01`, `AC-CMP-02` |

Behavior is preserved at the frozen `/api/v1` contract; the Compose client
reconciles two deployed deltas already recorded in `COMPOSE_GUIDE.md` (platform
wire value `hacker_rank`; counts-only `GET /dashboard`).

## Migration and parity evidence

- `CMP-007` added a stateful controlled API adapter and an acceptance journey
  that runs the same four experiences on Android and Web; `AcceptanceUiTest`
  drives the rendered Compose screens. Evidence is recorded in
  `apps/multiplatform/COMPOSE_GUIDE.md` section 14 and
  `infra/acceptance/README.md`.
- The Flutter `integration_test` journey remains the historical MVP acceptance
  evidence; it is frozen with the archived client and is no longer run.
- Owner isolation, authorization, and transport/status mapping were, and remain,
  API and data-layer concerns rather than client-emulated behavior.

## Known follow-ups (outside this task)

- iOS and Desktop Compose targets are deferred; Android and Web are the proven
  targets (`spec.md` section 5).
- Least-privilege Neon role rotation remains an operator follow-up recorded in
  the `NEO-003` closeout.
