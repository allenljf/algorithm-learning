# Compose Multiplatform Migration — Feature Plan

## Planning record

- Feature ID: `compose-multiplatform-migration`
- Specification: [spec.md](spec.md)
- Planning method: `work-graph` with three perspectives (planner, implementer,
  evaluator), because the migration replaces the entire client stack across
  platform targets while keeping a frozen REST contract.
- Execution contract: selected per ready task by `$execution-strategy`.

## Delivery strategy

Establish the Compose Multiplatform root and its engineering guide first, then
rebuild the client inside-out mirroring the delivered Flutter task order: data
flows before screens. Each UI task is verified against the behavior of its
Flutter counterpart (ALG-011..015). The Flutter client is retired only after the
cross-platform acceptance task passes.

## Planned phases

| Phase | Tasks | Outcome |
|---|---|---|
| 1. Foundation | CMP-001 | Compose Multiplatform root, shared modules, DI/navigation/theme, and a Compose engineering guide |
| 2. Data | CMP-002, CMP-003 | Auth/session and problem-library/review/dashboard data layers |
| 3. Experiences | CMP-004, CMP-005, CMP-006 | Auth/problem management, browse/Review Mode, dashboard/home UI |
| 4. Release | CMP-007, CMP-008 | Cross-platform acceptance and Flutter retirement |

## Dependency and parallelization rules

- `CMP-001` has no dependency; it writes only `apps/multiplatform` and the new
  Compose guide.
- `CMP-002` depends on `CMP-001` and the API auth foundation `ALG-005`.
- `CMP-003` depends on `CMP-001`, `CMP-002`, and the API library tasks
  `ALG-007..ALG-010`.
- `CMP-004`, `CMP-005`, and `CMP-006` depend on `CMP-002`/`CMP-003` and may run
  in one parallel group because they own separate feature directories.
- `CMP-007` depends on all three experiences; `CMP-008` depends on `CMP-007`.

## Architecture guardrails carried into execution

- The Spring Boot REST contract is frozen; the client adapts to it.
- DTOs stay in the data layer; domain models and UI state are separate and
  immutable.
- Composables render state and emit events only; repositories are injected at the
  composition root.
- Platform-specific code is limited to HTTP engine, storage, and DI actuals.

## Completion definition

The feature is complete when `apps/multiplatform` builds for Android and Web,
the Compose engineering guide exists, the four experiences match their Flutter
counterparts' acceptance behavior, the cross-platform acceptance task passes, and
the Flutter client is retired or explicitly archived.
